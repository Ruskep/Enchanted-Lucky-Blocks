package com.luckychallenges.network;

import com.luckychallenges.LuckyChallengesMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DoubleJumpPacket() implements CustomPayload {

    public static final Id<DoubleJumpPacket> ID =
        new Id<>(Identifier.of(LuckyChallengesMod.MOD_ID, "double_jump"));

    public static final PacketCodec<RegistryByteBuf, DoubleJumpPacket> CODEC =
        PacketCodec.unit(new DoubleJumpPacket());

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}