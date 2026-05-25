package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: выбрось предмет из руки за 5 секунд.
 */
public class DropItemChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 5;
    private int ticksElapsed = 0;
    private int lastAnnouncedSecond = -1;
    private int initialMainHandCount = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        initialMainHandCount = player.getMainHandStack().getCount();
        ChallengeHud.announce(player, "Выбрось предмет из руки за 5 секунд! (Q)");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        int currentCount = player.getMainHandStack().getCount();
        // Предмет выброшен если стак уменьшился или рука пуста
        if (initialMainHandCount > 0 && (player.getMainHandStack().isEmpty() || currentCount < initialMainHandCount)) {
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        int secondsLeft = (TIME_LIMIT_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "🗑 Выбрось предмет! Осталось " + secondsLeft + "с!");
        }
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Не выбросил предмет!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выбрось предмет из руки за 5 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


