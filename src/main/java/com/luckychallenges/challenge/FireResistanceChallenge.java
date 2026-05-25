package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class FireResistanceChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 6;
    private int ticksElapsed = 0, lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        player.setOnFireFor(REQUIRED_TICKS / 20 + 2);
        ChallengeHud.announce(player, "Выживи 6 секунд в огне!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        if (player.isDead() || player.getHealth() <= 0) { ChallengeHud.fail(player, "Сгорел заживо!"); return ChallengeResult.FAIL; }
        int secondsLeft = (REQUIRED_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "🔥 Осталось " + secondsLeft + "с – ты горишь!");
        }
        if (ticksElapsed >= REQUIRED_TICKS) { player.extinguish(); ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 6 секунд в огне"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


