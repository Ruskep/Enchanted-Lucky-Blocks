package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class NoJumpChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 10;
    private int successTicks = 0, lastAnnouncedSecond = -1;
    private boolean wasOnGround = true;

    @Override
    public void start(ServerPlayerEntity player) {
        wasOnGround = player.isOnGround();
        ChallengeHud.announce(player, "Не прыгай 10 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        boolean onGround = player.isOnGround();
        if (wasOnGround && !onGround) {
            ChallengeHud.reset(player, "Ты прыгнул!");
            successTicks = 0; lastAnnouncedSecond = -1;
            wasOnGround = onGround;
            return ChallengeResult.RUNNING;
        }
        wasOnGround = onGround;
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 10 - sec;
            if (rem > 0) ChallengeHud.progress(player, "🚫 Осталось " + rem + "с – не прыгай!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Не прыгай 10 секунд"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


