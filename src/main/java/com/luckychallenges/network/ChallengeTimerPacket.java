package com.luckychallenges.network;

import com.luckychallenges.LuckyChallengesMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Пакет: обновление таймера испытания на клиенте.
 * ticksLeft = -1 означает скрыть таймер.
 * totalTicks нужен чтобы менять цвет когда мало времени.
 */
public record ChallengeTimerPacket(int ticksLeft, int totalTicks, String description) implements CustomPayload {

    public static final Id<ChallengeTimerPacket> ID =
        new Id<>(Identifier.of(LuckyChallengesMod.MOD_ID, "challenge_timer"));

    public static final PacketCodec<RegistryByteBuf, ChallengeTimerPacket> CODEC =
        PacketCodec.tuple(
            PacketCodecs.INTEGER, ChallengeTimerPacket::ticksLeft,
            PacketCodecs.INTEGER, ChallengeTimerPacket::totalTicks,
            PacketCodecs.STRING,  ChallengeTimerPacket::description,
            ChallengeTimerPacket::new
        );

    public ChallengeTimerPacket(int ticksLeft, int totalTicks, String description) {
        this.ticksLeft = ticksLeft;
        this.totalTicks = totalTicks;
        this.description = description;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}


