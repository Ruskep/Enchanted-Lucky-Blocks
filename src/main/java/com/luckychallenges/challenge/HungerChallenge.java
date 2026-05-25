package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class HungerChallenge implements Challenge {

    private static final int TOTAL_TICKS    = 20 * 12;
    private static final int DRAIN_INTERVAL = 20 * 2;
    private int ticksElapsed = 0;

    @Override
    public void start(ServerPlayerEntity player) { ChallengeHud.announce(player, "Выживи 12 секунд голодая!"); }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        if (ticksElapsed % DRAIN_INTERVAL == 0) {
            player.getHungerManager().setFoodLevel(Math.max(0, player.getHungerManager().getFoodLevel() - 3));
            ChallengeHud.progress(player, "🍖 Голод! Осталось " + (TOTAL_TICKS - ticksElapsed) / 20 + "с...");
        }
        if (ticksElapsed >= TOTAL_TICKS) {
            player.getHungerManager().setFoodLevel(20);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 12 секунд голодая"; }
    @Override public int getTimeLimitTicks() { return TOTAL_TICKS; }
}


