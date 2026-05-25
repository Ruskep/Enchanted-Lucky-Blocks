package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Random;

public class HotbarSwitchChallenge implements Challenge {

    private static final int TIME_PER_SLOT_TICKS = 20 * 5;
    private static final int TOTAL_ROUNDS = 3;
    private static final Random RNG = new Random();
    private int round = 0, targetSlot = -1, ticksInRound = 0;

    @Override
    public void start(ServerPlayerEntity player) { nextRound(player); }

    private void nextRound(ServerPlayerEntity player) {
        round++; ticksInRound = 0; targetSlot = RNG.nextInt(9);
        ChallengeHud.announce(player, "Переключись на слот #" + (targetSlot + 1) + "! (" + round + "/" + TOTAL_ROUNDS + ")");
    }

    @Override
    public ChallengeResult tick(ServerPlayerEntity player) {
        ticksInRound++;
        if (player.getInventory().getSelectedSlot() == targetSlot) {
            if (round >= TOTAL_ROUNDS) { ChallengeHud.success(player); return ChallengeResult.SUCCESS; }
            nextRound(player); return ChallengeResult.RUNNING;
        }
        int secondsLeft = (TIME_PER_SLOT_TICKS - ticksInRound) / 20;
        if (ticksInRound % 20 == 0 && secondsLeft > 0)
            ChallengeHud.progress(player, "> Слот #" + (targetSlot + 1) + "! Осталось " + secondsLeft + "с");
        if (ticksInRound >= TIME_PER_SLOT_TICKS) {
            ChallengeHud.fail(player, "Не успел переключить слот!");
            return ChallengeResult.FAIL;
        }
        return ChallengeResult.RUNNING;
    }

    @Override public String getDescription() { return "Переключись на нужный слот хотбара 3 раза"; }
    @Override public int getTimeLimitTicks() { return TIME_PER_SLOT_TICKS * TOTAL_ROUNDS; }
}


