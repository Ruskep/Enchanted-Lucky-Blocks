package com.luckychallenges.challenge;

import com.luckychallenges.data.PlayerDataPersistence;
import com.luckychallenges.upgrade.UpgradeManager;
import com.luckychallenges.upgrade.UpgradeEffects;
import com.luckychallenges.upgrade.UpgradeType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ChallengeManager {

    private static final Map<UUID, Challenge> activeChallenges = new HashMap<>();
    private static final Map<UUID, Integer>   challengeTicks    = new HashMap<>();

    /** Штраф к макс. здоровью за провалы (кратно 6, т.е. 3 сердца) */
    private static final Map<UUID, Integer> healthPenalties = new HashMap<>();

    private static final List<Class<? extends Challenge>> CHALLENGE_TYPES = List.of(
        TakeDamageChallenge.class,
        DontLookDownChallenge.class,
        CursedSlotChallenge.class,
        StandStillChallenge.class,
        JumpChallenge.class,
        SprintChallenge.class,
        LookUpChallenge.class,
        CrouchChallenge.class,
        HungerChallenge.class,
        SpinChallenge.class,
        NoJumpChallenge.class,
        LowHealthChallenge.class,
        BlindnessChallenge.class,
        NauseaChallenge.class,
        HotbarSwitchChallenge.class,
        FireResistanceChallenge.class,
        LevitationChallenge.class,
        MiningFatigueChallenge.class,
        EatSomethingChallenge.class,
        DropItemChallenge.class,
        ReachHeightChallenge.class,
        DontSprintChallenge.class,
        SpeedRunChallenge.class,
        HoldSneakChallenge.class,
        FreeFallChallenge.class,
        MultiJumpChallenge.class,
        LookAroundChallenge.class,
        SwitchHandChallenge.class,
        KeepSpeedChallenge.class,
        StayOnGroundChallenge.class
    );

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ChallengeManager::onServerTick);
        ServerEntityEvents.ENTITY_LOAD.register((Entity entity, net.minecraft.server.world.ServerWorld world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                world.getServer().execute(() -> {
                    UpgradeManager.reapplyAll(player);
                });
            }
        });
    }

    public static void giveRandomChallenge(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (activeChallenges.containsKey(uuid)) {
            player.sendMessage(
                Text.translatable("challenge.luckychallenges.already_active").formatted(Formatting.RED),
                false
            );
            return;
        }

        Challenge challenge = createRandom();
        activeChallenges.put(uuid, challenge);
        challengeTicks.put(uuid, 0);
        challenge.start(player);
    }

    /** Вызывается когда испытание провалено — наказание */
    public static void onChallengeFailed(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        int penalty = healthPenalties.getOrDefault(uuid, 0) + 6;
        healthPenalties.put(uuid, penalty);

        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (attr == null) return;

        int healthLevel = UpgradeManager.getLevel(uuid, UpgradeType.HEALTH);
        double base = Math.max(1.0, 20.0 + healthLevel * 2.0 - penalty);

        if (base <= 2.0) {
            // Здоровья не осталось — сброс и спектатор
            healthPenalties.remove(uuid);
            attr.setBaseValue(Math.max(1.0, 20.0 + healthLevel * 2.0));
            player.setHealth(player.getMaxHealth());
            player.changeGameMode(GameMode.SPECTATOR);
            player.sendMessage(
                Text.translatable("challenge.luckychallenges.lives_gone").formatted(Formatting.DARK_RED),
                false
            );
        } else {
            attr.setBaseValue(base);
            if (player.getHealth() > player.getMaxHealth())
                player.setHealth(player.getMaxHealth());
        }
    }

    private static Challenge createRandom() {
        int index = new Random().nextInt(CHALLENGE_TYPES.size());
        try {
            return CHALLENGE_TYPES.get(index).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate challenge", e);
        }
    }

    private static void onServerTick(MinecraftServer server) {
        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, Challenge> entry : activeChallenges.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

            // Если игрок мёртв или временно недоступен — не удаляем испытание,
            // а ждём респауна (смерть не отменяет испытание)
            if (player == null || player.isRemoved()) continue;

            int elapsed = challengeTicks.merge(uuid, 1, Integer::sum);
            int limit = entry.getValue().getTimeLimitTicks();

            // Автоматический провал при истечении таймера
            if (limit > 0 && elapsed > limit) {
                toRemove.add(uuid);
                onChallengeFailed(player);
                continue;
            }

            ChallengeResult result = entry.getValue().tick(player);

            // Отправляем таймер каждый тик
            if (limit > 0) {
                int left = Math.max(0, limit - elapsed);
                ChallengeHud.timer(player, left, limit, entry.getValue().getDescriptionKey());
            }

            if (result == ChallengeResult.SUCCESS) {
                toRemove.add(uuid);
                PlayerDataPersistence.incrementChallengesCompleted(player);
            } else if (result == ChallengeResult.FAIL) {
                toRemove.add(uuid);
                onChallengeFailed(player);
            }
        }

        toRemove.forEach(uuid -> {
            activeChallenges.remove(uuid);
            challengeTicks.remove(uuid);
            // Скрываем таймер
            ServerPlayerEntity p = server.getPlayerManager().getPlayer(uuid);
            if (p != null) ChallengeHud.hideTimer(p);
        });
    }

    public static boolean hasActiveChallenge(UUID uuid) {
        return activeChallenges.containsKey(uuid);
    }

    public static int getHealthPenalty(UUID uuid) {
        return healthPenalties.getOrDefault(uuid, 0);
    }

    public static void setHealthPenalty(UUID uuid, int penalty) {
        if (penalty > 0) {
            healthPenalties.put(uuid, penalty);
        } else {
            healthPenalties.remove(uuid);
        }
    }

    public static void clearAll() {
        activeChallenges.clear();
        challengeTicks.clear();
        healthPenalties.clear();
    }
}


