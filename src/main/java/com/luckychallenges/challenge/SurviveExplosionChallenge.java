package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

/**
 * Испытание: выживи после взрыва рядом с тобой.
 * Взрыв происходит через 2 секунды после старта.
 */
public class SurviveExplosionChallenge implements Challenge {

    private static final int EXPLOSION_TICK = 20 * 2;
    private static final int TOTAL_TICKS    = 20 * 6;
    private int ticksElapsed = 0;
    private boolean exploded = false;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        ChallengeHud.announce(player, "Выживи после взрыва! Приготовься...");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;

        if (ticksElapsed == EXPLOSION_TICK && !exploded) {
            exploded = true;
            // Взрыв рядом (не прямо на игроке)
            double ox = (Math.random() * 4) - 2;
            double oz = (Math.random() * 4) - 2;
            player.getEntityWorld().createExplosion(null,
                player.getX() + ox, player.getY(), player.getZ() + oz,
                2.5f, false, World.ExplosionSourceType.BLOCK);
        }

        if (exploded) {
            if (player.isDead() || player.getHealth() <= 0) {
                ChallengeHud.fail(player, "Тебя разнесло!");
                return ChallengeResult.FAIL;
            }
            int secondsLeft = (TOTAL_TICKS - ticksElapsed) / 20;
            if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
                lastAnnouncedSecond = secondsLeft;
                if (secondsLeft > 0) ChallengeHud.progress(player, "!! Выживи! Осталось " + secondsLeft + "с");
            }
        }

        if (ticksElapsed >= TOTAL_TICKS) {
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выживи после взрыва рядом"; }
}


