package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: бегай со скоростью V не останавливаясь 5 секунд.
 * Скорость очень высокая — сложно не врезаться.
 */
public class KeepSpeedChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 5;

    private int successTicks = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, REQUIRED_TICKS + 60, 4, false, false));
        ChallengeHud.announce(player, "Бегай со скоростью V не останавливаясь 5 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        if (!player.isSprinting()) {
            if (successTicks > 0) {
                ChallengeHud.reset(player, "Остановился!");
                successTicks = 0;
                lastAnnouncedSecond = -1;
            }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 5 - sec;
            if (rem > 0) ChallengeHud.progress(player, "💨 Осталось " + rem + "с – не тормози!");
        }
        if (successTicks >= REQUIRED_TICKS) {
            player.removeStatusEffect(StatusEffects.SPEED);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Бегай со скоростью V не останавливаясь 5 секунд"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


