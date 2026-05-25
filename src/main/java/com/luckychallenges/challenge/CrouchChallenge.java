package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public class CrouchChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 7;
    private int successTicks = 0, lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) { ChallengeHud.announce(player, "Сиди на корточках 7 секунд!"); }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        if (!player.isSneaking()) {
            if (successTicks > 0) { ChallengeHud.reset(player, "Ты встал!"); successTicks = 0; lastAnnouncedSecond = -1; }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 7 - sec;
            if (rem > 0) ChallengeHud.progress(player, "🦆 Осталось " + rem + "с – не вставай!");
        }
        if (successTicks >= REQUIRED_TICKS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Сиди на корточках 7 секунд"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


