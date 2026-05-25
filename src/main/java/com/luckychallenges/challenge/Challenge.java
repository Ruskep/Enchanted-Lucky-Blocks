package com.luckychallenges.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Challenge {
    void start(ServerPlayerEntity player);

    ChallengeResult tick(ServerPlayerEntity player);

    String getDescription();

    default String getDescriptionKey() {
        String name = getClass().getSimpleName().replace("Challenge", "");
        return "challenge.luckychallenges." + name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase() + ".desc";
    }

    default int getTimeLimitTicks() { return 0; }
}


