package com.luckychallenges.network;

import com.luckychallenges.LuckyChallengesMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Packet sent from server to client to display a challenge HUD message.
 *
 * type:
 *   0 = announce (yellow)
 *   1 = success  (green)
 *   2 = fail     (red)
 *   3 = reset    (orange)
 *   4 = progress (action bar only — handled separately, not used here)
 */
public record ChallengeHudPacket(int type, String message) implements CustomPayload {

    public static final Id<ChallengeHudPacket> ID =
        new Id<>(Identifier.of(LuckyChallengesMod.MOD_ID, "challenge_hud"));

    public static final PacketCodec<RegistryByteBuf, ChallengeHudPacket> CODEC =
        PacketCodec.tuple(
            PacketCodecs.INTEGER, ChallengeHudPacket::type,
            PacketCodecs.STRING,  ChallengeHudPacket::message,
            ChallengeHudPacket::new
        );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}


