package com.luckychallenges;

import com.luckychallenges.challenge.ChallengeManager;
import com.luckychallenges.coin.CoinManager;
import com.luckychallenges.command.LuckyCommand;
import com.luckychallenges.data.PlayerDataPersistence;
import com.luckychallenges.network.NetworkRegistry;
import com.luckychallenges.registry.ModBlocks;
import com.luckychallenges.registry.ModItems;
import com.luckychallenges.upgrade.PerkEvents;
import com.luckychallenges.upgrade.UpgradeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuckyChallengesMod implements ModInitializer {

    public static final String MOD_ID = "luckychallenges";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        NetworkRegistry.register();
        ModBlocks.register();
        ModItems.register();
        PerkEvents.register();
        LuckyCommand.register();

        // Переприменяем улучшения и синхронизируем монеты при входе
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register(
            (handler, sender, server) -> {
                ServerPlayerEntity player = handler.player;
                com.luckychallenges.upgrade.UpgradeManager.reapplyAll(player);
                com.luckychallenges.coin.CoinSync.syncToClient(player);
            }
        );

        // Очищаем все статические данные при остановке сервера (смена мира/выход)
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            CoinManager.clearAll();
            UpgradeManager.clearAll();
            ChallengeManager.clearAll();
            PlayerDataPersistence.clearAll();
        });

        LOGGER.info("Lucky Challenges mod loaded!");
    }
}


