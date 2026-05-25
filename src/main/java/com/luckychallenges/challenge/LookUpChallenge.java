package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class LookUpChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 4;
    private int successTicks = 0, lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) { ChallengeHud.announce(player, "Смотри прямо ВВЕРХ 4 секунды!"); }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        if (player.getPitch() > -70.0f) {
            if (successTicks > 0) { ChallengeHud.reset(player, "Смотри выше!"); successTicks = 0; lastAnnouncedSecond = -1; }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 4 - sec;
            if (rem > 0) ChallengeHud.progress(player, "🌟 Осталось " + rem + "с – смотри вверх!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Смотри прямо вверх 4 секунды"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


