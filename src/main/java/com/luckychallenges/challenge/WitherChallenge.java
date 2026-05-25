package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: выживи 8 секунд с эффектом Иссушения II.
 */
public class WitherChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 8;
    private int ticksElapsed = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, REQUIRED_TICKS + 20, 1, false, false));
        ChallengeHud.announce(player, "Выживи 8 секунд с иссушением II!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        if (player.isDead() || player.getHealth() <= 0) {
            ChallengeHud.fail(player, "Иссушение тебя убило!");
            return ChallengeResult.FAIL;
        }
        int secondsLeft = (REQUIRED_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "💀 Осталось " + secondsLeft + "с – иссушение!");
        }
        if (ticksElapsed >= REQUIRED_TICKS) {
            player.removeStatusEffect(StatusEffects.WITHER);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 3, 1));
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 8 секунд с иссушением II"; }
}


