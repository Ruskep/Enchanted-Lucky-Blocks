package com.luckychallenges.network;

import com.luckychallenges.LuckyChallengesMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Клиент → сервер: запрос на покупку улучшения. */
public record PurchaseUpgradePacket(String upgradeId) implements CustomPayload {

    public static final Id<PurchaseUpgradePacket> ID =
        new Id<>(Identifier.of(LuckyChallengesMod.MOD_ID, "purchase_upgrade"));

    public static final PacketCodec<RegistryByteBuf, PurchaseUpgradePacket> CODEC =
        PacketCodec.tuple(PacketCodecs.STRING, PurchaseUpgradePacket::upgradeId, PurchaseUpgradePacket::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}


