package com.luckychallenges.command;

import com.luckychallenges.challenge.ChallengeManager;
import com.luckychallenges.coin.CoinManager;
import com.luckychallenges.coin.CoinSync;
import com.luckychallenges.upgrade.UpgradeEffects;
import com.luckychallenges.upgrade.UpgradeManager;
import com.luckychallenges.upgrade.UpgradeType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class LuckyCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("lucky")
                .requires(source -> {
                    if (source.getEntity() instanceof ServerPlayerEntity p) {
                        return source.getServer() != null && source.getServer().getPlayerManager().isOperator(p.getPlayerConfigEntry());
                    }
                    return true;
                })
                .then(CommandManager.literal("coins")
                    .then(CommandManager.literal("add")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    CoinManager.addCoins(target, amount);
                                    CoinSync.syncToClient(target);
                                    ctx.getSource().sendFeedback(
                                        () -> Text.translatable("command.luckychallenges.coins.added", amount, target.getName()),
                                        true);
                                    return 1;
                                }))))
                    .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    if (CoinManager.spendCoins(target, amount)) {
                                        CoinSync.syncToClient(target);
                                        ctx.getSource().sendFeedback(
                                            () -> Text.translatable("command.luckychallenges.coins.removed", amount, target.getName()),
                                            true);
                                    } else {
                                        ctx.getSource().sendError(
                                            Text.translatable("command.luckychallenges.coins.insufficient", target.getName()));
                                    }
                                    return 1;
                                })))))
                .then(CommandManager.literal("challenge")
                    .then(CommandManager.literal("start")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(ctx -> {
                                ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                                ChallengeManager.giveRandomChallenge(target);
                                ctx.getSource().sendFeedback(
                                    () -> Text.translatable("command.luckychallenges.challenge.given", target.getName()),
                                    true);
                                return 1;
                            }))))
                .then(CommandManager.literal("upgrades")
                    .then(CommandManager.literal("set")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .then(CommandManager.argument("type", StringArgumentType.word())
                                .then(CommandManager.argument("level", IntegerArgumentType.integer(0, 999))
                                    .executes(ctx -> {
                                        ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                                        String typeName = StringArgumentType.getString(ctx, "type");
                                        int level = IntegerArgumentType.getInteger(ctx, "level");
                                        try {
                                            UpgradeType type = UpgradeType.valueOf(typeName.toUpperCase());
                                            int finalLevel = Math.min(level, type.getMaxLevel());
                                            String finalName = type.displayName;
                                            UpgradeManager.setLevel(target.getUuid(), type, finalLevel);
                                            UpgradeEffects.apply(target, type, finalLevel);
                                            CoinSync.syncToClient(target);
                                            ctx.getSource().sendFeedback(
                                                () -> Text.literal("Установлено улучшение " + finalName + " ур. " + finalLevel + " игроку " + target.getName()),
                                                true);
                                            return 1;
                                        } catch (IllegalArgumentException e) {
                                            ctx.getSource().sendError(Text.literal("Неизвестное улучшение: " + typeName + ". Доступны: " + listUpgradeTypes()));
                                            return 0;
                                        }
                                    })))))
                    .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .then(CommandManager.argument("type", StringArgumentType.word())
                                .executes(ctx -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                                    String typeName = StringArgumentType.getString(ctx, "type");
                                    try {
                                        UpgradeType type = UpgradeType.valueOf(typeName.toUpperCase());
                                        UpgradeManager.setLevel(target.getUuid(), type, 0);
                                        UpgradeEffects.apply(target, type, 0);
                                        CoinSync.syncToClient(target);
                                        ctx.getSource().sendFeedback(
                                            () -> Text.literal("Удалено улучшение " + type.displayName + " у игрока " + target.getName()),
                                            true);
                                        return 1;
                                    } catch (IllegalArgumentException e) {
                                        ctx.getSource().sendError(Text.literal("Неизвестное улучшение: " + typeName));
                                        return 0;
                                    }
                                 }))))));
        });
    }

    private static String listUpgradeTypes() {
        StringBuilder sb = new StringBuilder();
        for (UpgradeType t : UpgradeType.values()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(t.name().toLowerCase());
        }
        return sb.toString();
    }
}
