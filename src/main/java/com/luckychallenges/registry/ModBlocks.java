package com.luckychallenges.registry;

import com.luckychallenges.LuckyChallengesMod;
import com.luckychallenges.block.GreenLuckyBlock;
import com.luckychallenges.block.YellowLuckyBlock;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModBlocks {

    private static final Identifier GREEN_ID = Identifier.of(LuckyChallengesMod.MOD_ID, "green_lucky_block");
    private static final Identifier YELLOW_ID = Identifier.of(LuckyChallengesMod.MOD_ID, "yellow_lucky_block");
    private static final RegistryKey<Block> GREEN_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, GREEN_ID);
    private static final RegistryKey<Block> YELLOW_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, YELLOW_ID);
    private static final RegistryKey<Item> GREEN_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, GREEN_ID);
    private static final RegistryKey<Item> YELLOW_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, YELLOW_ID);

    public static final Block GREEN_LUCKY_BLOCK = Registry.register(
        Registries.BLOCK, GREEN_ID,
        new GreenLuckyBlock(AbstractBlock.Settings.create()
            .registryKey(GREEN_BLOCK_KEY)
            .mapColor(MapColor.LIME)
            .strength(0.5f, 0.5f)
            .sounds(BlockSoundGroup.WOOD))
    );

    public static final Block YELLOW_LUCKY_BLOCK = Registry.register(
        Registries.BLOCK, YELLOW_ID,
        new YellowLuckyBlock(AbstractBlock.Settings.create()
            .registryKey(YELLOW_BLOCK_KEY)
            .mapColor(MapColor.YELLOW)
            .strength(0.5f, 0.5f)
            .sounds(BlockSoundGroup.WOOD))
    );

    public static final Item GREEN_LUCKY_BLOCK_ITEM = Registry.register(
        Registries.ITEM, GREEN_ID,
        new BlockItem(GREEN_LUCKY_BLOCK, new Item.Settings().registryKey(GREEN_ITEM_KEY))
    );

    public static final Item YELLOW_LUCKY_BLOCK_ITEM = Registry.register(
        Registries.ITEM, YELLOW_ID,
        new BlockItem(YELLOW_LUCKY_BLOCK, new Item.Settings().registryKey(YELLOW_ITEM_KEY))
    );

    private static final Identifier TAB_ID = Identifier.of(LuckyChallengesMod.MOD_ID, "tab");
    public static final ItemGroup CREATIVE_TAB = Registry.register(
        Registries.ITEM_GROUP, TAB_ID,
        FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.luckychallenges"))
            .icon(() -> new ItemStack(GREEN_LUCKY_BLOCK))
            .entries((ctx, entries) -> {
                entries.add(GREEN_LUCKY_BLOCK);
                entries.add(YELLOW_LUCKY_BLOCK);
            })
            .build()
    );

    public static void register() {
        // Инициализация статических полей (таб зарегистрируется при загрузке класса)
    }
}


