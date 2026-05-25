package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: поднимись на 10 блоков выше текущей позиции за 15 секунд.
 */
public class ReachHeightChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 15;
    private int ticksElapsed = 0;
    private double targetY = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        targetY = player.getY() + 10;
        ChallengeHud.announce(player, "Поднимись на 10 блоков вверх за 15 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        if (player.getY() >= targetY) {
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        int secondsLeft = (TIME_LIMIT_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            int blocksLeft = (int)(targetY - player.getY());
            if (secondsLeft > 0) ChallengeHud.progress(player, "⬆ Ещё " + blocksLeft + " блоков! Осталось " + secondsLeft + "с");
        }
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Не успел подняться!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Поднимись на 10 блоков вверх за 15 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


