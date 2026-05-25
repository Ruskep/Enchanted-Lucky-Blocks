package com.luckychallenges.client;

import com.luckychallenges.upgrade.UpgradeType;

import java.util.EnumMap;
import java.util.Map;

/** Клиентская копия данных игрока. */
public class ClientData {
    public static int coins = 0;
    public static final Map<UpgradeType, Integer> upgradeLevels = new EnumMap<>(UpgradeType.class);
}


