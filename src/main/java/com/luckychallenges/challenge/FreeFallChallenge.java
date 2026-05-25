package com.luckychallenges.challenge;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: тебя подбрасывает вверх — не касайся земли 4 секунды (свободное падение).
 */
public class FreeFallChallenge implements Challenge {

    private static final int REQUIRED_TICKS = 20 * 4;

    private int airTicks = 0;
    private int ticksElapsed = 0;
    private int lastAnnouncedSecond = -1;
    private boolean launched = false;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Не касайся земли 4 секунды!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;

        // Подбрасываем на 1-м тике
        if (ticksElapsed == 1) {
            player.setPosition(player.getX(), player.getY() + 15, player.getZ());
            launched = true;
        }

        if (launched) {
            if (player.isOnGround()) {
                if (airTicks > 0) {
                    ChallengeHud.reset(player, "Коснулся земли!");
                    airTicks = 0;
                    lastAnnouncedSecond = -1;
                    // Подбрасываем снова если не успел
                    if (ticksElapsed < 20 * 10) {
                        player.setPosition(player.getX(), player.getY() + 12, player.getZ());
                    }
                }
                return ChallengeResult.RUNNING;
            }

            airTicks++;
            int sec = airTicks / 20;
            if (sec != lastAnnouncedSecond && sec > 0) {
                lastAnnouncedSecond = sec;
                int rem = 4 - sec;
                if (rem > 0) ChallengeHud.progress(player, ">> Осталось " + rem + "с – не падай!");
            }
            if (airTicks >= REQUIRED_TICKS) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 20 * 5, 0, false, false));
                ChallengeHud.success(player);
                return ChallengeResult.SUCCESS;
            }
        }

        if (ticksElapsed >= 20 * 15) {
            ChallengeHud.fail(player, "Не удержался в воздухе!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Не касайся земли 4 секунды"; }
    @Override public int getTimeLimitTicks() { return 20 * 15; }
}


