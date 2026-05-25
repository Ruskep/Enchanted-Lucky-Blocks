package com.luckychallenges.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import com.luckychallenges.upgrade.UpgradeManager;
import com.luckychallenges.upgrade.UpgradeType;
import net.minecraft.entity.player.PlayerEntity;

public class NetworkRegistry {

    public static void register() {
        // S2C
        PayloadTypeRegistry.playS2C().register(ChallengeHudPacket.ID,    ChallengeHudPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ChallengeTimerPacket.ID,  ChallengeTimerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(CoinSyncPacket.ID,        CoinSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(UpgradeSyncPacket.ID,     UpgradeSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenUpgradeMenuPacket.ID, OpenUpgradeMenuPacket.CODEC);

        // C2S
        PayloadTypeRegistry.playC2S().register(PurchaseUpgradePacket.ID, PurchaseUpgradePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(DoubleJumpPacket.ID, DoubleJumpPacket.CODEC);

        // Обработка покупки на сервере
        ServerPlayNetworking.registerGlobalReceiver(PurchaseUpgradePacket.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> {
                try {
                    UpgradeType type = UpgradeType.valueOf(payload.upgradeId());
                    UpgradeManager.purchase(player, type);
                } catch (IllegalArgumentException ignored) {}
            });
        });

        // Двойной прыжок (элитры)
        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPacket.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> {
                if (UpgradeManager.getLevel(player.getUuid(), UpgradeType.ELYTRA_FLY) > 0
                    && !player.isOnGround()
                    && !player.isTouchingWater()
                    && !player.hasVehicle()
                    && !player.isGliding()) {
                    player.startGliding();
                }
            });
        });
    }
}


