package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: плыви (находись в воде) 5 секунд подряд.
 */
public class SwimChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 5;
    private int successTicks = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Плыви в воде 5 секунд подряд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        if (!player.isTouchingWater()) {
            if (successTicks > 0) {
                ChallengeHud.reset(player, "Выйди из воды — нет! Зайди обратно!");
                successTicks = 0;
                lastAnnouncedSecond = -1;
            }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 5 - sec;
            if (rem > 0) ChallengeHud.progress(player, "🌊 Осталось " + rem + "с – плыви!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Плыви в воде 5 секунд подряд"; }
}


