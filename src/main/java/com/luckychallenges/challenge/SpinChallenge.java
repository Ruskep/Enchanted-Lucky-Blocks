package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class SpinChallenge implements Challenge {

    private static final int REQUIRED_SPINS   = 3;
    private static final int TIME_LIMIT_TICKS = 20 * 10;
    private int spins = 0, ticksElapsed = 0;
    private float totalYawDelta = 0f, lastYaw = Float.NaN;

    @Override
    public void start(ServerPlayerEntity player) {
        lastYaw = player.getYaw();
        ChallengeHud.announce(player, "Покрутись вокруг своей оси 3 раза за 10 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        float yaw = player.getYaw();
        if (!Float.isNaN(lastYaw)) {
            float delta = yaw - lastYaw;
            while (delta > 180f) delta -= 360f;
            while (delta < -180f) delta += 360f;
            totalYawDelta += Math.abs(delta);
        }
        lastYaw = yaw;
        int newSpins = (int)(totalYawDelta / 360f);
        if (newSpins > spins) {
            spins = newSpins;
            ChallengeHud.progress(player, "🌀 Оборот " + spins + "/" + REQUIRED_SPINS + "!");
            if (spins >= REQUIRED_SPINS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        }
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Только " + spins + "/3 оборота!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Покрутись 3 раза за 10 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


