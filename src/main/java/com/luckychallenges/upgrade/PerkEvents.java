package com.luckychallenges.upgrade;

import com.luckychallenges.block.YellowLuckyBlock;
import com.luckychallenges.challenge.ChallengeManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.List;

public class PerkEvents {

    public static void register() {

        // ── МАГНИТ: притягивает предметы в радиусе 8 блоков ─────────────────
        ServerTickEvents.END_SERVER_TICK.register(PerkEvents::tickMagnet);

        // ── ПАЦИФИСТ: мобы игнорируют игрока ─────────────────────────────────
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player
                && source.getAttacker() instanceof MobEntity
                && UpgradeManager.getLevel(player.getUuid(), UpgradeType.PACIFIST) > 0) {
                return false;
            }
            return true;
        });
        ServerTickEvents.END_SERVER_TICK.register(PerkEvents::tickPacifist);

        // ── ПОЛЁТ (ELYTRA_FLY): обрабатывается через DoubleJumpPacket ────────

        // ── АНТИПАДЕНИЕ: сбрасываем fallDistance, без эффекта ────────────────
        ServerTickEvents.END_SERVER_TICK.register(PerkEvents::tickNoFall);

        // ── РЕГЕНЕРАЦИЯ: хилим вручную, без эффекта ──────────────────────────
        ServerTickEvents.END_SERVER_TICK.register(PerkEvents::tickRegen);

        // ── ОГНЕУПОРНЫЙ: тушим огонь каждый тик ──────────────────────────────
        ServerTickEvents.END_SERVER_TICK.register(PerkEvents::tickFireImmune);

        // ── ЖЁЛТЫЙ БЛОК: не ломается при активном испытании ─────────────────
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayerEntity sp)) return true;
            if (state.getBlock() instanceof YellowLuckyBlock && ChallengeManager.hasActiveChallenge(sp.getUuid())) {
                return false;
            }
            return true;
        });
    }

    private static void tickMagnet(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (PlayerEntity p : world.getPlayers()) {
                if (!(p instanceof ServerPlayerEntity sp)) continue;
                if (UpgradeManager.getLevel(sp.getUuid(), UpgradeType.MAGNET) <= 0) continue;

                Box box = sp.getBoundingBox().expand(8.0);
                List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, box, e -> true);
                for (ItemEntity item : items) {
                    double dx = sp.getX() - item.getX();
                    double dy = sp.getY() - item.getY();
                    double dz = sp.getZ() - item.getZ();
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist < 0.5) continue;
                    double speed = 0.15;
                    item.setVelocity(dx / dist * speed, dy / dist * speed, dz / dist * speed);
                }
            }
        }
    }

    private static void tickPacifist(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (PlayerEntity p : world.getPlayers()) {
                if (!(p instanceof ServerPlayerEntity sp)) continue;
                if (UpgradeManager.getLevel(sp.getUuid(), UpgradeType.PACIFIST) <= 0) continue;

                // Сбрасываем таргет у всех мобов в радиусе 32 блоков
                Box box = sp.getBoundingBox().expand(32.0);
                List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, box,
                    mob -> mob.getTarget() == sp || mob.getAttacker() == sp);
                for (MobEntity mob : mobs) {
                    mob.setTarget(null);
                    mob.setAttacker(null);
                    mob.setAttacking(false);
                    if (mob instanceof Angerable angerable) {
                        angerable.forgive(world, sp);
                        angerable.setAngryAt(null);
                    }
                }
            }
        }
    }

    private static void tickNoFall(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (PlayerEntity p : world.getPlayers()) {
                if (!(p instanceof ServerPlayerEntity sp)) continue;
                if (UpgradeManager.getLevel(sp.getUuid(), UpgradeType.NO_FALL) > 0) {
                    sp.fallDistance = 0;
                }
            }
        }
    }

    private static int regenTick = 0;

    private static void tickRegen(MinecraftServer server) {
        regenTick++;
        if (regenTick < 40) return;
        regenTick = 0;
        for (ServerWorld world : server.getWorlds()) {
            for (PlayerEntity p : world.getPlayers()) {
                if (!(p instanceof ServerPlayerEntity sp)) continue;
                if (UpgradeManager.getLevel(sp.getUuid(), UpgradeType.REGEN) > 0) {
                    float max = sp.getMaxHealth();
                    if (sp.getHealth() < max) {
                        sp.setHealth(Math.min(max, sp.getHealth() + 1f));
                    }
                }
            }
        }
    }

    private static void tickFireImmune(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (PlayerEntity p : world.getPlayers()) {
                if (!(p instanceof ServerPlayerEntity sp)) continue;
                if (UpgradeManager.getLevel(sp.getUuid(), UpgradeType.FIRE_IMMUNE) > 0) {
                    sp.setFireTicks(0);
                }
            }
        }
    }

}


