package com.luckychallenges.challenge;

import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Random;

/**
 * Испытание: съешь любую еду за 10 секунд.
 * Игроку выдаётся случайная еда в руку если инвентарь пуст.
 */
public class EatSomethingChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 10;
    private static final List<ItemStack> FOODS = List.of(
        new ItemStack(Items.BREAD),
        new ItemStack(Items.COOKED_BEEF),
        new ItemStack(Items.APPLE),
        new ItemStack(Items.CARROT),
        new ItemStack(Items.COOKED_CHICKEN)
    );

    private int ticksElapsed = 0;
    private int lastAnnouncedSecond = -1;
    private int initialFoodLevel = 0;

    @Override
    public void start(ServerPlayerEntity player) {
        initialFoodLevel = player.getHungerManager().getFoodLevel();
        // Снижаем голод чтобы можно было есть
        player.getHungerManager().setFoodLevel(10);
        // Даём еду если нет ничего съедобного
        boolean hasFood = false;
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).get(DataComponentTypes.FOOD) != null) { hasFood = true; break; }
        }
        if (!hasFood) {
            ItemStack food = FOODS.get(new Random().nextInt(FOODS.size())).copy();
            player.getInventory().insertStack(food);
        }
        ChallengeHud.announce(player, "Съешь что-нибудь за 10 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        // Если голод вырос — значит поел
        if (player.getHungerManager().getFoodLevel() > 10) {
            ChallengeHud.success(player);
            return ChallengeResult.SUCCESS;
        }
        int secondsLeft = (TIME_LIMIT_TICKS - ticksElapsed) / 20;
        if (secondsLeft != lastAnnouncedSecond && ticksElapsed % 20 == 0) {
            lastAnnouncedSecond = secondsLeft;
            if (secondsLeft > 0) ChallengeHud.progress(player, ">> Ешь! Осталось " + secondsLeft + "с!");
        }
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Не поел вовремя!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Съешь что-нибудь за 10 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


