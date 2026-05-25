package com.luckychallenges.upgrade;

import com.luckychallenges.coin.CoinManager;
import com.luckychallenges.coin.CoinSync;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpgradeManager {

    // uuid -> (тип -> уровень)
    private static final Map<UUID, Map<UpgradeType, Integer>> data = new HashMap<>();

    public static int getLevel(UUID uuid, UpgradeType type) {
        return data.getOrDefault(uuid, Map.of()).getOrDefault(type, 0);
    }

    /**
     * Попытка купить следующий уровень улучшения.
     * @return true если успешно
     */
    public static boolean purchase(ServerPlayerEntity player, UpgradeType type) {
        UUID uuid = player.getUuid();
        int currentLevel = getLevel(uuid, type);
        if (currentLevel >= type.getMaxLevel()) return false;

        int price = type.prices[currentLevel];
        if (!CoinManager.spendCoins(player, price)) return false;

        data.computeIfAbsent(uuid, k -> new EnumMap<>(UpgradeType.class))
            .put(type, currentLevel + 1);

        // Применяем эффект немедленно
        UpgradeEffects.apply(player, type, currentLevel + 1);

        // Синхронизируем ОБА состояния (монеты и уровни) после обновления
        CoinSync.syncToClient(player);
        return true;
    }

    public static void setLevel(UUID uuid, UpgradeType type, int level) {
        if (level > 0) {
            data.computeIfAbsent(uuid, k -> new EnumMap<>(UpgradeType.class)).put(type, level);
        } else {
            Map<UpgradeType, Integer> map = data.get(uuid);
            if (map != null) {
                map.remove(type);
                if (map.isEmpty()) data.remove(uuid);
            }
        }
    }

    public static Map<UpgradeType, Integer> getAll(UUID uuid) {
        return data.getOrDefault(uuid, Map.of());
    }

    public static void clearAll() {
        data.clear();
    }

    /** Переприменить все улучшения (например после респауна). */
    public static void reapplyAll(ServerPlayerEntity player) {
        Map<UpgradeType, Integer> upgrades = data.get(player.getUuid());
        if (upgrades == null) return;
        for (Map.Entry<UpgradeType, Integer> e : upgrades.entrySet()) {
            UpgradeEffects.apply(player, e.getKey(), e.getValue());
        }
    }
}


