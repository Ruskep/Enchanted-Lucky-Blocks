package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class SprintChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 6;
    private int successTicks = 0, lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) { ChallengeHud.announce(player, "Бегай без остановки 6 секунд!"); }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        if (!player.isSprinting()) {
            if (successTicks > 0) { ChallengeHud.reset(player, "Ты остановился!"); successTicks = 0; lastAnnouncedSecond = -1; }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 6 - sec;
            if (rem > 0) ChallengeHud.progress(player, "🏃 Осталось " + rem + "с – не останавливайся!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Бегай без остановки 6 секунд"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


