package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: не бегай (не спринтуй) 12 секунд.
 */
public class DontSprintChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 12;
    private int successTicks = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Не бегай 12 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        if (player.isSprinting()) {
            if (successTicks > 0) {
                ChallengeHud.reset(player, "Ты побежал!");
                player.damage(player.getEntityWorld(), player.getEntityWorld().getDamageSources().generic(), 2.0f);
                successTicks = 0;
                lastAnnouncedSecond = -1;
            }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 12 - sec;
            if (rem > 0) ChallengeHud.progress(player, "🐢 Осталось " + rem + "с – не беги!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Не бегай 12 секунд"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


