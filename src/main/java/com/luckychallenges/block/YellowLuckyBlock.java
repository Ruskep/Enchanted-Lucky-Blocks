package com.luckychallenges.block;

import com.luckychallenges.challenge.ChallengeManager;
import com.luckychallenges.coin.CoinSync;
import com.luckychallenges.data.PlayerDataPersistence;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class YellowLuckyBlock extends Block {

    public YellowLuckyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            if (ChallengeManager.hasActiveChallenge(serverPlayer.getUuid())) {
                serverPlayer.sendMessage(
                    Text.literal("!! Сначала заверши текущее испытание!").formatted(Formatting.RED),
                    true
                );
                return state;
            }
            serverPlayer.sendMessage(
                Text.translatable("block.luckychallenges.yellow_challenge").formatted(Formatting.YELLOW),
                false
            );
            com.luckychallenges.coin.CoinManager.addCoins(serverPlayer, 9);
            CoinSync.syncToClient(serverPlayer);
            PlayerDataPersistence.incrementBlocksBroken(serverPlayer);
            ChallengeManager.giveRandomChallenge(serverPlayer);
        }
        if (world instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                12, 0.4, 0.4, 0.4, 0.05);
            sw.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.8f, 1.5f);
        }
        return super.onBreak(world, pos, state, player);
    }
}


