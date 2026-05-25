package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: не прыгай и не взлетай 15 секунд — тебе дают прыжок III,
 * который постоянно пытается тебя подбросить.
 */
public class StayOnGroundChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 15;
    private int successTicks = 0;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, REQUIRED_TICKS + 20, 2, false, false));
        ChallengeHud.announce(player, "Оставайся на земле 15 секунд с прыжком III!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        if (!player.isOnGround()) {
            if (successTicks > 0) {
                ChallengeHud.reset(player, "Ты взлетел!");
                successTicks = 0;
                lastAnnouncedSecond = -1;
            }
            return ChallengeResult.RUNNING;
        }
        successTicks++;
        int sec = successTicks / 20;
        if (sec != lastAnnouncedSecond && sec > 0) {
            lastAnnouncedSecond = sec;
            int rem = 15 - sec;
            if (rem > 0) ChallengeHud.progress(player, "🦶 Осталось " + rem + "с – не прыгай!");
        }
        if (successTicks >= REQUIRED_TICKS) {
            player.removeStatusEffect(StatusEffects.JUMP_BOOST);
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Оставайся на земле 15 секунд с прыжком III"; }
    @Override public int getTimeLimitTicks() { return REQUIRED_TICKS; }
}


