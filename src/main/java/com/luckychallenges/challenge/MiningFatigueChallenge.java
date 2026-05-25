package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class MiningFatigueChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 10;
    private int ticksElapsed = 0, lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, REQUIRED_TICKS + 20, 2, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, REQUIRED_TICKS + 20, 1, false, false));
        ChallengeHud.announce(player, "Выживи 10 секунд с усталостью шахтёра и слабостью!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        int secondsLeft = (REQUIRED_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "⛏ Осталось " + secondsLeft + "с – так слабо...");
        }
        if (ticksElapsed >= REQUIRED_TICKS) {
            player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
            player.removeStatusEffect(StatusEffects.WEAKNESS);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 10 секунд с усталостью шахтёра и слабостью"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


