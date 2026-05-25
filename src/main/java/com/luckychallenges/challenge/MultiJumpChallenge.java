package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: прыгни 20 раз за 20 секунд.
 */
public class MultiJumpChallenge implements Challenge {

    private static final int REQUIRED_JUMPS   = 20;
    private static final int TIME_LIMIT_TICKS = 20 * 20;

    private int jumps = 0;
    private int ticksElapsed = 0;
    private boolean wasOnGround = true;

    @Override
    public void start(ServerPlayerEntity player) {
        wasOnGround = player.isOnGround();
        ChallengeHud.announce(player, "Прыгни 20 раз за 20 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        boolean onGround = player.isOnGround();
        if (wasOnGround && !onGround) {
            jumps++;
            if (jumps % 5 == 0 || jumps >= REQUIRED_JUMPS)
                ChallengeHud.progress(player, "🐇 " + jumps + "/" + REQUIRED_JUMPS + " прыжков!");
            if (jumps >= REQUIRED_JUMPS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        }
        wasOnGround = onGround;
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Только " + jumps + "/20 прыжков!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Прыгни 20 раз за 20 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


