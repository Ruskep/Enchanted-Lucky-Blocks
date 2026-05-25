package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: прыгни 5 раз подряд без касания земли дольше 1 секунды.
 * Если стоишь на земле дольше 20 тиков — счётчик сбрасывается.
 */
public class JumpComboChallenge implements Challenge {

    private static final int REQUIRED_JUMPS   = 5;
    private static final int TIME_LIMIT_TICKS = 20 * 20;
    private static final int MAX_GROUND_TICKS = 20; // макс 1 сек на земле

    private int jumps = 0;
    private int ticksElapsed = 0;
    private int groundTicks = 0;
    private boolean wasOnGround = true;

    @Override
    public void start(ServerPlayerEntity player) {
        wasOnGround = player.isOnGround();
        ChallengeHud.announce(player, "Прыгни 5 раз, не стоя на земле дольше 1 секунды!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        boolean onGround = player.isOnGround();

        if (onGround) {
            groundTicks++;
            if (groundTicks > MAX_GROUND_TICKS && jumps > 0) {
                ChallengeHud.reset(player, "Слишком долго стоял! Сброс прыжков.");
                jumps = 0;
                groundTicks = 0;
            }
        } else {
            groundTicks = 0;
        }

        if (wasOnGround && !onGround) {
            jumps++;
            ChallengeHud.progress(player, "🐇 Прыжок " + jumps + "/" + REQUIRED_JUMPS);
            if (jumps >= REQUIRED_JUMPS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        }
        wasOnGround = onGround;

        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Только " + jumps + "/5 прыжков!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Прыгни 5 раз не задерживаясь на земле"; }
}


