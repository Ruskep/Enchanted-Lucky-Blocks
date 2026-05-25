package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: посмотри во все 4 стороны (север, юг, восток, запад) за 10 секунд.
 * Каждая сторона засчитывается когда игрок смотрит в её направлении ±30°.
 */
public class LookAroundChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 10;
    private static final float TOLERANCE = 30f;

    // Yaw в Minecraft: 0=юг, -90=восток, 90=запад, 180/-180=север
    private static final float[] DIRECTIONS     = {0f, 180f, -90f, 90f};
    private static final String[] DIR_NAMES     = {"ЮГ", "СЕВЕР", "ВОСТОК", "ЗАПАД"};

    private final boolean[] visited = new boolean[4];
    private int visitedCount = 0;
    private int ticksElapsed = 0;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Посмотри на ЮГ, СЕВЕР, ВОСТОК и ЗАПАД!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        float yaw = player.getYaw();

        for (int i = 0; i < 4; i++) {
            if (visited[i]) continue;
            float diff = Math.abs(yaw - DIRECTIONS[i]);
            if (diff > 180f) diff = 360f - diff;
            if (diff <= TOLERANCE) {
                visited[i] = true;
                visitedCount++;
                ChallengeHud.progress(player, "🧭 " + DIR_NAMES[i] + " ✔ (" + visitedCount + "/4)");
                if (visitedCount >= 4) {
                    ChallengeHud.success(player);
                    return ChallengeResult.SUCCESS;
                }
            }
        }

        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Не успел посмотреть во все стороны!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Посмотри на все 4 стороны света за 10 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


