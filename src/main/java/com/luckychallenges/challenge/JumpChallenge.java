package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class JumpChallenge implements Challenge {

    private static final int REQUIRED_JUMPS   = 10;
    private static final int TIME_LIMIT_TICKS = 20 * 15;
    private int jumps = 0, ticksElapsed = 0;
    private boolean wasOnGround = true;

    @Override
    public void start(ServerPlayerEntity player) {
        wasOnGround = player.isOnGround();
        ChallengeHud.announce(player, "Прыгни 10 раз за 15 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        boolean onGround = player.isOnGround();
        if (wasOnGround && !onGround) {
            jumps++;
            ChallengeHud.progress(player, "🐇 Прыжок " + jumps + "/" + REQUIRED_JUMPS);
            if (jumps >= REQUIRED_JUMPS) { ChallengeHud.success(player); wasOnGround = onGround; return ChallengeResult.SUCCESS; }
        }
        wasOnGround = onGround;
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Только " + jumps + "/10 прыжков!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Прыгни 10 раз за 15 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


