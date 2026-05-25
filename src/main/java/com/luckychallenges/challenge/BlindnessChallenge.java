package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class BlindnessChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 10;
    private int ticksElapsed = 0, lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, REQUIRED_TICKS + 20, 0, false, false));
        ChallengeHud.announce(player, "Выживи 10 секунд в полной темноте!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        int secondsLeft = (REQUIRED_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "🌑 Осталось " + secondsLeft + "с – ты слеп!");
        }
        if (ticksElapsed >= REQUIRED_TICKS) {
            player.removeStatusEffect(StatusEffects.BLINDNESS);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 10 секунд в слепоте"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


