package com.luckychallenges.network;

import com.luckychallenges.LuckyChallengesMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Синхронизирует количество монет с клиентом. */
public record CoinSyncPacket(int coins) implements CustomPayload {

    public static final Id<CoinSyncPacket> ID =
        new Id<>(Identifier.of(LuckyChallengesMod.MOD_ID, "coin_sync"));

    public static final PacketCodec<RegistryByteBuf, CoinSyncPacket> CODEC =
        PacketCodec.tuple(PacketCodecs.INTEGER, CoinSyncPacket::coins, CoinSyncPacket::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}


