package com.luckychallenges.challenge;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class CursedSlotChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 15;
    private int ticksElapsed = 0;
    private int cursedSlot = -1;
    private int lastAnnouncedSecond = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        cursedSlot = 4;
        for (int i = 0; i < 9; i++) {
            if (!player.getInventory().getStack(i).isEmpty()) { cursedSlot = i; break; }
        }
        player.getInventory().setSelectedSlot(cursedSlot);
        ChallengeHud.announce(player, "Убери предмет из проклятого слота #" + (cursedSlot + 1) + " за 15 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        if (player.getInventory().getStack(cursedSlot).isEmpty()) {
            ChallengeHud.success(player); return ChallengeResult.SUCCESS;
        }
        int secondsLeft = (TIME_LIMIT_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond) {
            lastAnnouncedSecond = secondsLeft;
            ChallengeHud.progress(player, "!! Очисти слот #" + (cursedSlot + 1) + "! Осталось " + secondsLeft + "с!");
        }
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Ты не успел очистить слот!");
            ItemStack stack = player.getInventory().getStack(cursedSlot);
            player.dropItem(stack, true);
            player.getInventory().removeOne(stack);
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Убери предмет из проклятого слота инвентаря"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


