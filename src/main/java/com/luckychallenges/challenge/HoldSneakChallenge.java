package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: зажми Shift и прыгни 5 раз (прыжок на корточках) за 10 секунд.
 */
public class HoldSneakChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 10;
    private static final int REQUIRED_JUMPS   = 5;

    private int ticksElapsed = 0;
    private int jumps = 0;
    private boolean wasOnGround = true;

    @Override
    public void start(ServerPlayerEntity player) {
        wasOnGround = player.isOnGround();
        ChallengeHud.announce(player, "Прыгни 5 раз зажав Shift за 10 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        boolean onGround = player.isOnGround();

        if (wasOnGround && !onGround && player.isSneaking()) {
            jumps++;
            ChallengeHud.progress(player, "🦆 Прыжок+Shift " + jumps + "/" + REQUIRED_JUMPS);
            if (jumps >= REQUIRED_JUMPS) {
                ChallengeHud.success(player);
                return ChallengeResult.SUCCESS;
            }
        }
        wasOnGround = onGround;

        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Только " + jumps + "/5 прыжков с Shift!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Прыгни 5 раз зажав Shift за 10 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


