package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: стой в темноте (уровень света ≤ 3) 8 секунд подряд.
 */
public class StayInDarkChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 8;
    private int successTicks = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Стой в темноте (свет ≤ 3) 8 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        int light = player.getEntityWorld().getLightLevel(player.getBlockPos());
        if (light > 3) {
            if (successTicks > 0) {
                ChallengeHud.reset(player, "Слишком светло! Найди темноту!");
                successTicks = 0;
                lastAnnouncedSecond = -1;
            }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 8 - sec;
            if (rem > 0) ChallengeHud.progress(player, "__ Осталось " + rem + "с – не выходи на свет!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Стой в темноте 8 секунд"; }
}


