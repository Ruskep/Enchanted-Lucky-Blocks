package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class StandStillChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 8;
    private int successTicks = 0;
    private Vec3d lastPos = null;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        lastPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        ChallengeHud.announce(player, "Стой на месте 8 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        Vec3d pos = new Vec3d(player.getX(), player.getY(), player.getZ());
        double dist = lastPos != null ? pos.distanceTo(lastPos) : 0;
        lastPos = pos;
        if (dist > 0.05) {
            if (successTicks > 0) { ChallengeHud.reset(player, "Ты пошевелился!"); successTicks = 0; lastAnnouncedSecond = -1; }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 8 - sec;
            if (rem > 0) ChallengeHud.progress(player, "++ Осталось " + rem + "с – не двигайся!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Стой на месте 8 секунд"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


