package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class LowHealthChallenge implements Challenge {

    private static final int REQUIRED_TICKS  = 20 * 8;
    private int ticksElapsed = 0, lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        if (player.getHealth() > 8.0f) player.setHealth(8.0f);
        ChallengeHud.announce(player, "Выживи 8 секунд с 4 сердцами!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        if (player.isDead() || player.getHealth() <= 0) { ChallengeHud.fail(player, "Ты погиб!"); return ChallengeResult.FAIL; }
        int secondsLeft = (REQUIRED_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "❤ Осталось " + secondsLeft + "с – держись!");
        }
        if (ticksElapsed >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 8 секунд с 4 сердцами"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


