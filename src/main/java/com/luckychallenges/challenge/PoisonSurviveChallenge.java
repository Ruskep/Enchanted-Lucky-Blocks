package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: выживи 10 секунд с ядом II (яд не убивает, но оставит 1 HP).
 */
public class PoisonSurviveChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 10;
    private int ticksElapsed = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, REQUIRED_TICKS + 20, 1, false, false));
        ChallengeHud.announce(player, "Выживи 10 секунд с ядом II!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        int secondsLeft = (REQUIRED_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "☠ Осталось " + secondsLeft + "с – яд!");
        }
        if (ticksElapsed >= REQUIRED_TICKS) {
            player.removeStatusEffect(StatusEffects.POISON);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 4, 1));
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 10 секунд с ядом II"; }
}


