package com.luckychallenges.coin;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Хранит количество Лаки-баксов для каждого игрока.
 * Данные живут в памяти — для персистентности нужно подключить NBT/PlayerData.
 */
public class CoinManager {

    private static final Map<UUID, Integer> coins = new HashMap<>();

    public static int getCoins(UUID uuid) {
        return coins.getOrDefault(uuid, 0);
    }

    public static void addCoins(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUuid();
        coins.merge(uuid, amount, Integer::sum);
    }

    public static boolean spendCoins(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUuid();
        int current = getCoins(uuid);
        if (current < amount) return false;
        coins.put(uuid, current - amount);
        return true;
    }

    public static void setCoins(UUID uuid, int amount) {
        coins.put(uuid, Math.max(0, amount));
    }

    public static void clearAll() {
        coins.clear();
    }
}


