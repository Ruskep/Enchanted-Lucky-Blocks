package com.luckychallenges.network;

import com.luckychallenges.LuckyChallengesMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Сервер → клиент: открыть меню улучшений. */
public record OpenUpgradeMenuPacket() implements CustomPayload {

    public static final Id<OpenUpgradeMenuPacket> ID =
        new Id<>(Identifier.of(LuckyChallengesMod.MOD_ID, "open_upgrade_menu"));

    public static final PacketCodec<RegistryByteBuf, OpenUpgradeMenuPacket> CODEC =
        PacketCodec.of((v, b) -> {}, b -> new OpenUpgradeMenuPacket());

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}


