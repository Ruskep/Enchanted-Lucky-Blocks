package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class DontLookDownChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 5;
    private int successTicks = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Не смотри ВНИЗ 5 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        if (player.getPitch() > 45.0f) {
            if (successTicks > 0) {
                ChallengeHud.reset(player, "Ты посмотрел вниз!");
                player.getHungerManager().setFoodLevel(Math.max(0, player.getHungerManager().getFoodLevel() - 2));
                successTicks = 0; lastAnnouncedSecond = -1;
            }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 5 - sec;
            if (rem > 0) ChallengeHud.progress(player, "👀 Осталось " + rem + "с – смотри вверх!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Не смотри вниз 5 секунд"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


