package com.luckychallenges.block;

import com.luckychallenges.coin.CoinSync;
import com.luckychallenges.data.PlayerDataPersistence;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class GreenLuckyBlock extends Block {

    private static final Random RNG = new Random();

    private static final List<ItemStack> GOOD_LOOT = List.of(
        new ItemStack(Items.DIAMOND, 5),
        new ItemStack(Items.EMERALD, 4),
        new ItemStack(Items.NETHERITE_INGOT, 1),
        new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1),
        new ItemStack(Items.GOLDEN_APPLE, 3),
        new ItemStack(Items.DIAMOND_SWORD, 1),
        new ItemStack(Items.DIAMOND_PICKAXE, 1),
        new ItemStack(Items.ELYTRA, 1),
        new ItemStack(Items.TOTEM_OF_UNDYING, 1),
        new ItemStack(Items.SHULKER_BOX, 1),
        new ItemStack(Items.ENDER_PEARL, 8),
        new ItemStack(Items.BLAZE_ROD, 6),
        new ItemStack(Items.EXPERIENCE_BOTTLE, 16),
        new ItemStack(Items.IRON_INGOT, 12),
        new ItemStack(Items.GOLD_INGOT, 8),
        new ItemStack(Items.LAPIS_LAZULI, 24),
        new ItemStack(Items.REDSTONE, 32),
        new ItemStack(Items.ARROW, 16),
        new ItemStack(Items.COOKED_BEEF, 10),
        new ItemStack(Items.ENDER_CHEST, 1),
        new ItemStack(Items.OBSIDIAN, 8),
        new ItemStack(Items.CRYING_OBSIDIAN, 4),
        new ItemStack(Items.SLIME_BALL, 12)
    );

    private static final List<ItemStack> BAD_LOOT = List.of(
        new ItemStack(Items.ROTTEN_FLESH, 8),
        new ItemStack(Items.POISONOUS_POTATO, 5),
        new ItemStack(Items.GRAVEL, 16),
        new ItemStack(Items.DIRT, 32),
        new ItemStack(Items.DEAD_BUSH, 4),
        new ItemStack(Items.BONE, 3),
        new ItemStack(Items.SPIDER_EYE, 6),
        new ItemStack(Items.COBBLESTONE, 32),
        new ItemStack(Items.SAND, 24),
        new ItemStack(Items.CACTUS, 6),
        new ItemStack(Items.PUMPKIN, 2),
        new ItemStack(Items.STICK, 24)
    );

    private static final List<EntityType<?>> ANIMALS = List.of(
        EntityType.COW,
        EntityType.SHEEP,
        EntityType.CHICKEN,
        EntityType.PIG,
        EntityType.RABBIT,
        EntityType.HORSE,
        EntityType.WOLF,
        EntityType.CAT,
        EntityType.FOX,
        EntityType.GOAT,
        EntityType.LLAMA,
        EntityType.DONKEY
    );

    private static final List<EntityType<?>> MONSTERS = List.of(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.SPIDER,
        EntityType.CREEPER,
        EntityType.SLIME,
        EntityType.WITCH,
        EntityType.ENDERMAN,
        EntityType.RAVAGER,
        EntityType.VINDICATOR,
        EntityType.EVOKER,
        EntityType.VEX,
        EntityType.HOGLIN
    );

    public GreenLuckyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            com.luckychallenges.coin.CoinManager.addCoins(serverPlayer, 3);
            CoinSync.syncToClient(serverPlayer);
            PlayerDataPersistence.incrementBlocksBroken(serverPlayer);
            triggerLuck(world, pos, serverPlayer);
        }
        if (world instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                15, 0.4, 0.4, 0.4, 0.1);
            sw.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 1.0f, 1.2f);
        }
        return super.onBreak(world, pos, state, player);
    }

    private void triggerLuck(World world, BlockPos pos, ServerPlayerEntity player) {
        int luckLevel = com.luckychallenges.upgrade.UpgradeManager.getLevel(
            player.getUuid(), com.luckychallenges.upgrade.UpgradeType.LUCK);
        int roll = RNG.nextInt(100);
        int goodThreshold = 20 + luckLevel;

        if (roll < goodThreshold) {
            dropGoodLoot(world, pos);
        } else if (roll < goodThreshold + 10) {
            dropBadLoot(world, pos);
        } else if (roll < goodThreshold + 18) {
            applyPositiveEffects(player);
        } else if (roll < goodThreshold + 30) {
            applyNegativeEffects(player);
        } else if (roll < goodThreshold + 40) {
            spawnAnimals(world, pos);
        } else if (roll < goodThreshold + 55) {
            spawnMonsters(world, pos, player);
        } else {
            triggerSpecialEvent(world, pos, player);
        }
    }

    private void dropGoodLoot(World world, BlockPos pos) {
        int count = 1 + RNG.nextInt(3);
        for (int i = 0; i < count; i++)
            spawnItem(world, pos, GOOD_LOOT.get(RNG.nextInt(GOOD_LOOT.size())).copy());
    }

    private void dropBadLoot(World world, BlockPos pos) {
        int count = 1 + RNG.nextInt(2);
        for (int i = 0; i < count; i++)
            spawnItem(world, pos, BAD_LOOT.get(RNG.nextInt(BAD_LOOT.size())).copy());
    }

    private void applyPositiveEffects(ServerPlayerEntity player) {
        switch (RNG.nextInt(7)) {
            case 0 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20*60, 2)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20*60, 2)); }
            case 1 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20*60, 1)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20*60, 1)); }
            case 2 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20*30, 2)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20*60, 3)); }
            case 3 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 20*120, 0)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 20*120, 0)); }
            case 4 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20*60, 2)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 20*120, 0)); }
            case 5 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 20*60, 0)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 20*120, 1)); }
            default -> { player.clearStatusEffects(); player.setHealth(player.getMaxHealth()); player.getHungerManager().setFoodLevel(20); player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20*120, 0)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 20*60, 0)); }
        }
    }

    private void applyNegativeEffects(ServerPlayerEntity player) {
        switch (RNG.nextInt(7)) {
            case 0 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 20*12, 2)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20*20, 3)); }
            case 1 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20*15, 0)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20*15, 2)); }
            case 2 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20*30, 3)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20*30, 3)); }
            case 3 -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20*8, 4));
            case 4 -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20*8, 2));
            case 5 -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 20*10, 1)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20*5, 1)); }
            default -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 20*30, 3)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20*30, 2)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20*20, 1)); }
        }
    }

    private void spawnAnimals(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld sw)) return;
        int count = 1 + RNG.nextInt(3);
        for (int i = 0; i < count; i++) {
            EntityType<?> type = ANIMALS.get(RNG.nextInt(ANIMALS.size()));
            double x = pos.getX() + 0.5 + (RNG.nextDouble() - 0.5) * 3;
            double z = pos.getZ() + 0.5 + (RNG.nextDouble() - 0.5) * 3;
            double y = pos.getY() + 1;
            type.spawn(sw, BlockPos.ofFloored(x, y, z), SpawnReason.EVENT);
        }
    }

    private void spawnMonsters(World world, BlockPos pos, ServerPlayerEntity player) {
        if (!(world instanceof ServerWorld sw)) return;
        int count = 2 + RNG.nextInt(3);
        for (int i = 0; i < count; i++) {
            EntityType<?> type = MONSTERS.get(RNG.nextInt(MONSTERS.size()));
            double x = player.getX() + (RNG.nextDouble() - 0.5) * 4;
            double z = player.getZ() + (RNG.nextDouble() - 0.5) * 4;
            double y = player.getY();
            type.spawn(sw, BlockPos.ofFloored(x, y, z), SpawnReason.EVENT);
        }
    }

    private void triggerSpecialEvent(World world, BlockPos pos, ServerPlayerEntity player) {
        if (!(world instanceof ServerWorld sw)) return;
        switch (RNG.nextInt(10)) {
            case 0 -> world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3.5f, false, World.ExplosionSourceType.BLOCK);
            case 1 -> {
                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, sw);
                lightning.refreshPositionAfterTeleport(player.getX() + RNG.nextInt(5) - 2, player.getY(), player.getZ() + RNG.nextInt(5) - 2);
                sw.spawnEntity(lightning);
                // Вторая молния чуть позже
                LightningEntity l2 = new LightningEntity(EntityType.LIGHTNING_BOLT, sw);
                l2.refreshPositionAfterTeleport(player.getX() + RNG.nextInt(5) - 2, player.getY(), player.getZ() + RNG.nextInt(5) - 2);
                sw.spawnEntity(l2);
            }
            case 2 -> {
                player.teleport(sw, player.getX(), player.getY() + 20, player.getZ(), java.util.Set.of(), player.getYaw(), player.getPitch(), true);
                // без slow_falling — чистый удар!
            }
            case 3 -> {
                world.createExplosion(null, player.getX(), player.getY(), player.getZ(), 4f, true, World.ExplosionSourceType.BLOCK);
            }
            case 4 -> {
                int count = 5 + RNG.nextInt(5);
                for (int i = 0; i < count; i++) {
                    BlockPos tntPos = pos.add(RNG.nextInt(6) - 3, 1, RNG.nextInt(6) - 3);
                    sw.spawnEntity(new TntEntity(sw, tntPos.getX() + 0.5, tntPos.getY(), tntPos.getZ() + 0.5, player));
                }
            }
            case 5 -> {
                // Взрыв + монстры
                world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3f, false, World.ExplosionSourceType.BLOCK);
                spawnMonsters(world, pos, player);
            }
            case 6 -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20*6, 10));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20*6, 2));
            }
            case 7 -> {
                // Рой мобов
                for (int i = 0; i < 6 + RNG.nextInt(4); i++) {
                    EntityType<?> type = EntityType.VEX;
                    double x = player.getX() + (RNG.nextDouble() - 0.5) * 8;
                    double z = player.getZ() + (RNG.nextDouble() - 0.5) * 8;
                    type.spawn(sw, BlockPos.ofFloored(x, player.getY(), z), SpawnReason.EVENT);
                }
            }
            default -> {
                player.clearStatusEffects();
                player.setHealth(player.getMaxHealth());
                player.getHungerManager().setFoodLevel(20);
                spawnItem(world, pos, new ItemStack(Items.GOLDEN_APPLE, 2));
            }
        }
    }

    private void spawnItem(World world, BlockPos pos, ItemStack stack) {
        double ox = RNG.nextDouble() * 0.6 - 0.3;
        double oz = RNG.nextDouble() * 0.6 - 0.3;
        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5 + ox, pos.getY() + 0.75, pos.getZ() + 0.5 + oz, stack));
    }
}


