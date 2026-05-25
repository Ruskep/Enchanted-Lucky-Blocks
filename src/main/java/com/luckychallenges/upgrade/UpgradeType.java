package com.luckychallenges.upgrade;

/**
 * Все доступные улучшения.
 * UPGRADE — прокачиваются до 5 раз.
 * PERK    — покупаются 1 раз.
 */
public enum UpgradeType {

    // Upgrade (5 levels)
    HEALTH      ("+ Здоровье",    "+1 сердце навсегда",          new int[]{40, 65, 95, 130, 170}),
    ARMOR       ("[] Броня",       "+0.5 брони навсегда",         new int[]{40, 60, 85, 115, 150}),
    LUCK        ("** Удача",       "+1% удачи в лаки блоках",     new int[]{40, 65, 95, 130, 170}),

    // Perks (one-time)
    ELYTRA_FLY  ("^ Полёт",       "Летай как на элитрах",        new int[]{80}),
    STEP_UP     (">> Двушаг",     "Поднимайся на 2 блока",       new int[]{60}),
    PACIFIST    ("~~ Пацифист",    "Мобы тебя игнорируют",        new int[]{100}),
    FIRE_IMMUNE ("!! Огнеупорный","Полный иммунитет к огню",      new int[]{70}),
    SPEED_BOOST ("++ Скорость",   "Постоянный Speed I",           new int[]{60}),
    NIGHT_VISION("oo Ночное зрение","Постоянное ночное зрение",   new int[]{40}),
    NO_FALL     ("vv Антипадение", "Никогда не получаешь урон от падения", new int[]{70}),
    MAGNET      ("** Магнит",     "Притягивает предметы с 8 блоков", new int[]{50}),
    REGEN       ("++ Регенерация","Постоянная регенерация I",     new int[]{80});

    public final String displayName;
    public final String description;
    /** Цены за каждый уровень. Длина = макс уровень. */
    public final int[] prices;

    UpgradeType(String displayName, String description, int[] prices) {
        this.displayName = displayName;
        this.description = description;
        this.prices = prices;
    }

    public int getMaxLevel() { return prices.length; }

    public boolean isPerk() { return prices.length == 1; }

    public String translationKey() {
        return "upgrade.luckychallenges." + name().toLowerCase();
    }
}


