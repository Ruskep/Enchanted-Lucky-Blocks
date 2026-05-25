package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Испытание: переключись между слотами хотбара туда-обратно 8 раз за 8 секунд.
 * Считаем каждую смену слота.
 */
public class SwitchHandChallenge implements Challenge {

    private static final int TIME_LIMIT_TICKS = 20 * 8;
    private static final int REQUIRED_SWITCHES = 8;

    private int ticksElapsed = 0;
    private int switches = 0;
    private int lastSlot = -1;

    @Override
    public void start(ServerPlayerEntity player) {
        lastSlot = player.getInventory().getSelectedSlot();
        ChallengeHud.announce(player, "Переключи слот хотбара 8 раз за 8 секунд!");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksElapsed++;
        int currentSlot = player.getInventory().getSelectedSlot();
        if (currentSlot != lastSlot) {
            switches++;
            lastSlot = currentSlot;
            ChallengeHud.progress(player, "> Переключений: " + switches + "/" + REQUIRED_SWITCHES);
            if (switches >= REQUIRED_SWITCHES) {
                ChallengeHud.success(player);
                return ChallengeResult.SUCCESS;
            }
        }
        if (ticksElapsed >= TIME_LIMIT_TICKS) {
            ChallengeHud.fail(player, "Только " + switches + "/8 переключений!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Переключи слот хотбара 8 раз за 8 секунд"; }
    @Override public int getTimeLimitTicks() { return TIME_LIMIT_TICKS; }
}


