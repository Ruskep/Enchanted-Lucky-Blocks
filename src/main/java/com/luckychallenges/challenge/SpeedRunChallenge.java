package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: пробеги 30 блоков за 8 секунд (со скоростью III).
 */
public class SpeedRunChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 8;
    private static final double REQUIRED_DIST = 30.0;

    private int ticksElapsed = 0;
    private double distanceTravelled = 0;
    private double lastX, lastZ;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        lastX = player.getX();
        lastZ = player.getZ();
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, TIME_LIMIT_TICKS + 20, 2, false, false));
        ChallengeHud.announce(player, "Пробеги 30 блоков за 8 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        double dx = player.getX() - lastX;
        double dz = player.getZ() - lastZ;
        distanceTravelled += Math.sqrt(dx * dx + dz * dz);
        lastX = player.getX();
        lastZ = player.getZ();

        if (distanceTravelled >= REQUIRED_DIST) {
            player.removeStatusEffect(StatusEffects.SPEED);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        int secondsLeft = (TIME_LIMIT_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            int blocksLeft = (int)(REQUIRED_DIST - distanceTravelled);
            if (secondsLeft > 0) ChallengeHud.progress(player, "💨 Ещё " + blocksLeft + " блоков! Осталось " + secondsLeft + "с");
        }
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            player.removeStatusEffect(StatusEffects.SPEED);
            ChallengeHud.fail(player, "Не добежал! (" + (int)distanceTravelled + "/30 блоков)");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Пробеги 30 блоков за 8 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


