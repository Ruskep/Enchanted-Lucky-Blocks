package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: пройди 15 блоков с замедлением IV за 20 секунд.
 */
public class SlowWalkChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 20;
    private static final double REQUIRED_DIST = 15.0;

    private int ticksElapsed = 0;
    private double distanceTravelled = 0;
    private double lastX, lastZ;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        lastX = player.getX();
        lastZ = player.getZ();
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, TIME_LIMIT_TICKS + 20, 3, false, false));
        ChallengeHud.announce(player, "Пройди 15 блоков с замедлением IV за 20 секунд!");
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
            player.removeStatusEffect(StatusEffects.SLOWNESS);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }

        int secondsLeft = (TIME_LIMIT_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            int blocksLeft = (int)(REQUIRED_DIST - distanceTravelled);
            if (secondsLeft > 0) ChallengeHud.progress(player, "🐌 Ещё " + blocksLeft + " блоков! Осталось " + secondsLeft + "с");
        }

        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            player.removeStatusEffect(StatusEffects.SLOWNESS);
            ChallengeHud.fail(player, "Не дошёл!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Пройди 15 блоков с замедлением за 20 секунд"; }
}


