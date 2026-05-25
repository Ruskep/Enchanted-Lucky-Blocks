package com.luckychallenges.challenge;

import com.luckychallenges.network.ChallengeHudPacket;
import com.luckychallenges.network.ChallengeTimerPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ChallengeHud {

    public static final int TYPE_ANNOUNCE = 0;
    public static final int TYPE_SUCCESS  = 1;
    public static final int TYPE_FAIL     = 2;
    public static final int TYPE_RESET    = 3;

    private static void playSound(ServerPlayerEntity player, RegistryEntry<SoundEvent> soundRef, float volume, float pitch) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        world.playSound(null, player.getX(), player.getY(), player.getZ(), soundRef, SoundCategory.MASTER, volume, pitch);
    }

    public static void announce(ServerPlayerEntity player, String message) {
        ServerPlayNetworking.send(player, new ChallengeHudPacket(TYPE_ANNOUNCE, message));
        playSound(player, SoundEvents.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
    }

    public static void success(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new ChallengeHudPacket(TYPE_SUCCESS, Text.translatable("challenge.luckychallenges.passed").getString()));
        playSound(player, Registries.SOUND_EVENT.getEntry(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE), 1.0f, 1.0f);
    }

    public static void fail(ServerPlayerEntity player, String reason) {
        ServerPlayNetworking.send(player, new ChallengeHudPacket(TYPE_FAIL, reason));
        playSound(player, Registries.SOUND_EVENT.getEntry(SoundEvents.ENTITY_WITHER_DEATH), 0.5f, 0.8f);
    }

    public static void reset(ServerPlayerEntity player, String reason) {
        ServerPlayNetworking.send(player, new ChallengeHudPacket(TYPE_RESET, reason));
        playSound(player, SoundEvents.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f);
    }

    public static void progress(ServerPlayerEntity player, String text) {}

    public static void progressKey(ServerPlayerEntity player, String key, Object... args) {}

    /** Обновить таймер на клиенте. ticksLeft=-1 скрывает таймер. */
    public static void timer(ServerPlayerEntity player, int ticksLeft, int totalTicks, String description) {
        ServerPlayNetworking.send(player, new ChallengeTimerPacket(ticksLeft, totalTicks, description));
    }

    /** Скрыть таймер. */
    public static void hideTimer(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new ChallengeTimerPacket(-1, 0, ""));
    }
}


