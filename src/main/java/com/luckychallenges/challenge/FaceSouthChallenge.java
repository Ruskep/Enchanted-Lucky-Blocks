package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: смотри строго на юг (yaw ~180°) 4 секунды подряд.
 * Допуск ±20 градусов.
 */
public class FaceSouthChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 4;
    private static final float TOLERANCE    = 20f;
    private int successTicks = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Смотри на ЮГ 4 секунды! (F3 поможет)");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        // Yaw: 0=юг, -90=восток, 90=запад, ±180=север (в Minecraft yaw 0 = юг)
        float yaw = ((player.getYaw() % 360) + 360) % 360; // нормализуем 0-360
        // Юг = 180 в нормализованном виде
        float diff = Math.abs(yaw - 180f);
        if (diff > 180f) diff = 360f - diff;

        if (diff > TOLERANCE) {
            if (successTicks > 0) {
                ChallengeHud.reset(player, "Отвернулся от юга!");
                successTicks = 0;
                lastAnnouncedSecond = -1;
            }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 4 - sec;
            if (rem > 0) ChallengeHud.progress(player, "🧭 Осталось " + rem + "с – держи юг!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Смотри на юг 4 секунды"; }
}


