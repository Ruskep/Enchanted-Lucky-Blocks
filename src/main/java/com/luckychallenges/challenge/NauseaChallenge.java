package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class NauseaChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 8;
    private int ticksElapsed = 0, lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, REQUIRED_TICKS + 20, 1, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, REQUIRED_TICKS + 20, 2, false, false));
        ChallengeHud.announce(player, "Выживи 8 секунд с тошнотой и замедлением!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        int secondsLeft = (REQUIRED_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "🌀 Осталось " + secondsLeft + "с – тебя тошнит!");
        }
        if (ticksElapsed >= REQUIRED_TICKS) {
            player.removeStatusEffect(StatusEffects.NAUSEA);
            player.removeStatusEffect(StatusEffects.SLOWNESS);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 8 секунд с тошнотой и замедлением"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


