package com.luckychallenges;

import com.luckychallenges.client.ClientData;
import com.luckychallenges.hud.ChallengeHudRenderer;
import com.luckychallenges.hud.ChallengeTimerRenderer;
import com.luckychallenges.hud.CoinHudRenderer;
import com.luckychallenges.network.*;
import com.luckychallenges.screen.UpgradeScreen;
import com.luckychallenges.upgrade.UpgradeType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import java.util.HashMap;
import java.util.Map;

public class LuckyChallengesClient implements ClientModInitializer {

    private static final Map<Integer, Boolean> doubleJumpPrev = new HashMap<>();
    private static final Map<Integer, Boolean> doubleJumpOnGround = new HashMap<>();

    @Override
    public void onInitializeClient() {

        // HUD уведомления испытаний
        ClientPlayNetworking.registerGlobalReceiver(ChallengeHudPacket.ID,
            (payload, ctx) -> ctx.client().execute(() -> ChallengeHudRenderer.receive(payload)));

        // Таймер испытания
        ClientPlayNetworking.registerGlobalReceiver(ChallengeTimerPacket.ID,
            (payload, ctx) -> ctx.client().execute(() -> ChallengeTimerRenderer.receive(payload)));

        // Синхронизация монет
        ClientPlayNetworking.registerGlobalReceiver(CoinSyncPacket.ID,
            (payload, ctx) -> ctx.client().execute(() -> {
                ClientData.coins = payload.coins();
                // Обновить кнопки если меню открыто
                if (MinecraftClient.getInstance().currentScreen instanceof UpgradeScreen s)
                    s.refresh();
            }));

        // Синхронизация улучшений
        ClientPlayNetworking.registerGlobalReceiver(UpgradeSyncPacket.ID,
            (payload, ctx) -> ctx.client().execute(() -> {
                ClientData.upgradeLevels.clear();
                payload.levels().forEach((k, v) -> {
                    try { ClientData.upgradeLevels.put(UpgradeType.valueOf(k), v); }
                    catch (IllegalArgumentException ignored) {}
                });
                if (MinecraftClient.getInstance().currentScreen instanceof UpgradeScreen s)
                    s.refresh();
            }));

        // Открыть меню улучшений (если сервер пришлёт команду)
        ClientPlayNetworking.registerGlobalReceiver(OpenUpgradeMenuPacket.ID,
            (payload, ctx) -> ctx.client().execute(
                () -> MinecraftClient.getInstance().setScreen(new UpgradeScreen())));

        // Двойной прыжок (элитры) — срабатывает только если игрок был в воздухе
        // минимум 2 тика подряд (чтобы не срабатывало на обычном прыжке)
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null || mc.world == null) return;
            int id = mc.player.getId();
            boolean jumpHeld = mc.options.jumpKey.isPressed();
            boolean prev = doubleJumpPrev.getOrDefault(id, false);
            boolean onGround = mc.player.isOnGround();
            boolean wasOnGround = doubleJumpOnGround.getOrDefault(id, true);
            doubleJumpPrev.put(id, jumpHeld);
            doubleJumpOnGround.put(id, onGround);
            if (jumpHeld && !prev && !onGround && !wasOnGround
                && !mc.player.isGliding() && !mc.player.isTouchingWater()) {
                ClientPlayNetworking.send(new DoubleJumpPacket());
            }
        });

        // HUD рендеры
        HudRenderCallback.EVENT.register((ctx, counter) -> {
            ChallengeHudRenderer.render(ctx, counter);
            ChallengeTimerRenderer.render(ctx, counter);
            CoinHudRenderer.render(ctx, counter);
        });
    }
}


