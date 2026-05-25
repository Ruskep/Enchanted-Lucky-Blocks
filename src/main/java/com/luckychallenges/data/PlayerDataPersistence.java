package com.luckychallenges.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataPersistence {

    private static final Map<UUID, Integer> blocksBroken = new HashMap<>();
    private static final Map<UUID, Integer> challengesCompleted = new HashMap<>();

    public static int getBlocksBroken(UUID uuid) {
        return blocksBroken.getOrDefault(uuid, 0);
    }

    public static int getChallengesCompleted(UUID uuid) {
        return challengesCompleted.getOrDefault(uuid, 0);
    }

    public static void incrementBlocksBroken(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        int count = blocksBroken.merge(uuid, 1, Integer::sum);
        if (count >= 100) {
            grantAdvancement(player, "broken_100_blocks");
        }
    }

    public static void incrementChallengesCompleted(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        int count = challengesCompleted.merge(uuid, 1, Integer::sum);
        if (count >= 50) {
            grantAdvancement(player, "completed_50_challenges");
        }
    }

    private static void grantAdvancement(ServerPlayerEntity player, String advancementId) {
        if (!(player.getEntityWorld() instanceof ServerWorld world)) return;
        MinecraftServer server = world.getServer();
        if (server == null) return;
        var loader = server.getAdvancementLoader();
        var adv = loader.get(Identifier.of("luckychallenges", advancementId));
        if (adv != null) {
            player.getAdvancementTracker().grantCriterion(adv, "unlock");
        }
    }

    public static Map<UUID, Integer> getBlocksBrokenMap() {
        return blocksBroken;
    }

    public static Map<UUID, Integer> getChallengesCompletedMap() {
        return challengesCompleted;
    }

    public static void clearAll() {
        blocksBroken.clear();
        challengesCompleted.clear();
    }
}
