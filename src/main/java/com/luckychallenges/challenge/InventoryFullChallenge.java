package com.luckychallenges.challenge;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: инвентарь заполняется мусором — очисти 5 слотов за 15 секунд.
 */
public class InventoryFullChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 15;
    private static final int REQUIRED_CLEARED = 5;

    private int ticksElapsed = 0;
    private int lastAnnouncedSecond = -1;
    private int initialFilledSlots = 0;

    @Override
    public void start(ServerPlayerEntity player) {
        // Заполняем 5 случайных пустых слотов гравием
        int filled = 0;
        for (int i = 9; i < 36 && filled < 5; i++) {
            if (player.getInventory().getStack(i).isEmpty()) {
                player.getInventory().setStack(i, new ItemStack(Items.GRAVEL, 64));
                filled++;
            }
        }
        initialFilledSlots = countGravel(player);
        ChallengeHud.announce(player, "Выбрось 5 стаков гравия за 15 секунд!");
    }

    private int countGravel(ServerPlayerEntity player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(Items.GRAVEL)) count++;
        }
        return count;
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        int cleared = initialFilledSlots - countGravel(player);
        if (cleared >= REQUIRED_CLEARED) {
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        int secondsLeft = (TIME_LIMIT_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, "🪨 Выброшено " + cleared + "/5! Осталось " + secondsLeft + "с");
        }
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Не успел выбросить гравий!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Выбрось 5 стаков гравия за 15 секунд"; }
}


