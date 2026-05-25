package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Random;

/**
 * Испытание: тебя телепортирует 3 раза в случайные места — выживи 12 секунд.
 */
public class RandomTeleportChallenge implements Challenge {

    private static final int TOTAL_TICKS      = 20 * 12;
    private static final int TELEPORT_INTERVAL = 20 * 4;
    private static final Random RNG = new Random();

    private int ticksElapsed = 0;
    private int teleports = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, TOTAL_TICKS + 40, 0, false, false));
        ChallengeHud.announce(player, "Тебя будет телепортировать 3 раза — выживи!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;

        if (ticksElapsed % TELEPORT_INTERVAL == 0 && teleports < 3) {
            teleports++;
            double ox = (RNG.nextDouble() - 0.5) * 20;
            double oz = (RNG.nextDouble() - 0.5) * 20;
            double newX = player.getX() + ox;
            double newZ = player.getZ() + oz;
            double newY = player.getY() + 5;
            player.setPosition(newX, newY, newZ);
            ChallengeHud.progress(player, ">> Телепорт " + teleports + "/3!");
        }

        if (player.isDead() || player.getHealth() <= 0) {
            ChallengeHud.fail(player, "Не пережил телепортацию!");
            return ChallengeResult.FAIL;
        }

        int secondsLeft = (TOTAL_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, ">> Осталось " + secondsLeft + "с...");
        }

        if (ticksElapsed >= TOTAL_TICKS) {
            player.removeStatusEffect(StatusEffects.SLOW_FALLING);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи 3 случайные телепортации за 12 секунд"; }
}


