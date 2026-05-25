package com.luckychallenges.coin;

import com.luckychallenges.network.CoinSyncPacket;
import com.luckychallenges.network.UpgradeSyncPacket;
import com.luckychallenges.upgrade.UpgradeManager;
import com.luckychallenges.upgrade.UpgradeType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class CoinSync {

    public static void syncToClient(ServerPlayerEntity player) {
        // Монеты
        ServerPlayNetworking.send(player,
            new CoinSyncPacket(CoinManager.getCoins(player.getUuid())));

        // Улучшения
        Map<String, Integer> levels = new HashMap<>();
        for (UpgradeType type : UpgradeType.values()) {
            int lvl = UpgradeManager.getLevel(player.getUuid(), type);
            if (lvl > 0) levels.put(type.name(), lvl);
        }
        ServerPlayNetworking.send(player, new UpgradeSyncPacket(levels));
    }
}


