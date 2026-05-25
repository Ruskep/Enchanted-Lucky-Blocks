package com.luckychallenges.network;

import com.luckychallenges.LuckyChallengesMod;
import com.luckychallenges.upgrade.UpgradeType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/** Синхронизирует уровни улучшений с клиентом. */
public record UpgradeSyncPacket(Map<String, Integer> levels) implements CustomPayload {

    public static final Id<UpgradeSyncPacket> ID =
        new Id<>(Identifier.of(LuckyChallengesMod.MOD_ID, "upgrade_sync"));

    public static final PacketCodec<RegistryByteBuf, UpgradeSyncPacket> CODEC =
        PacketCodec.of(
            (value, buf) -> {
                buf.writeInt(value.levels().size());
                value.levels().forEach((k, v) -> { buf.writeString(k); buf.writeInt(v); });
            },
            buf -> {
                int size = buf.readInt();
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < size; i++) map.put(buf.readString(), buf.readInt());
                return new UpgradeSyncPacket(map);
            }
        );

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}


