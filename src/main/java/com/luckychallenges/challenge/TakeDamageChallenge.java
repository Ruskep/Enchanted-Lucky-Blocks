package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class TakeDamageChallenge implements Challenge {

    private static final int TOTAL_TICKS     = 20 * 10;
    private static final int DAMAGE_INTERVAL = 20 * 2;
    private int ticksElapsed = 0;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Выживи 10 секунд, получая урон каждые 2 секунды!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        if (ticksElapsed % DAMAGE_INTERVAL == 0) {
            player.damage(player.getEntityWorld(), player.getEntityWorld().getDamageSources().generic(), 2.0f);
            if (player.isDead()) { ChallengeHud.fail(player, "Ты погиб!"); return ChallengeResult.FAIL; }
            ChallengeHud.progress(player, "!! Удар! Осталось " + (TOTAL_TICKS - ticksElapsed) / 20 + "с...");
        }
        if (ticksElapsed >= TOTAL_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 10 секунд под уроном"; }
    @Override public int getTimeLimitTicks() { return TOTAL_TICKS; }
}


