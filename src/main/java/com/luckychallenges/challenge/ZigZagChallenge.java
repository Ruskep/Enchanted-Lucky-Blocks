package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: смени направление взгляда (yaw) 6 раз на 90°+ за 8 секунд.
 * Считаем резкие повороты.
 */
public class ZigZagChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS  = 20 * 8;
    private static final int REQUIRED_TURNS    = 6;
    private static final float TURN_THRESHOLD  = 80f;

    private int ticksElapsed = 0;
    private int turns = 0;
    private float lastYaw = Float.NaN;
    private float accumulatedDelta = 0f;
    private float lastDeltaSign = 0f;

    @Override
    public void start(ServerPlayerEntity player) {
        lastYaw = player.getYaw();
        ChallengeHud.announce(player, "Резко поверни голову 6 раз за 8 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        float yaw = player.getYaw();

        if (!Float.isNaN(lastYaw)) {
            float delta = yaw - lastYaw;
            while (delta > 180f) delta -= 360f;
            while (delta < -180f) delta += 360f;

            float sign = Math.signum(delta);
            if (sign != 0 && sign != lastDeltaSign) {
                // Смена направления — сбрасываем накопленный угол
                if (Math.abs(accumulatedDelta) >= TURN_THRESHOLD) {
                    turns++;
                    ChallengeHud.progress(player, "↩ Поворот " + turns + "/" + REQUIRED_TURNS + "!");
                    if (turns >= REQUIRED_TURNS) {
                        ChallengeHud.success(player);
                        return ChallengeResult.SUCCESS;
                    }
                }
                accumulatedDelta = 0f;
                lastDeltaSign = sign;
            }
            accumulatedDelta += delta;
        }
        lastYaw = yaw;

        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Только " + turns + "/6 поворотов!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Резко поверни голову 6 раз за 8 секунд"; }
}


