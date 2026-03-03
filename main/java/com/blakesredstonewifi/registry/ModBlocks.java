package com.blakesredstonewifi.registry;

import com.blakesredstonewifi.BlakesRedstoneWifiMod;
import com.blakesredstonewifi.block.WifiEmitterBlock;
import com.blakesredstonewifi.block.WifiReceiverBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    private static final Identifier WIFI_EMITTER_ID = Identifier.of(BlakesRedstoneWifiMod.MOD_ID, "wifi_emitter");
    private static final Identifier WIFI_RECEIVER_ID = Identifier.of(BlakesRedstoneWifiMod.MOD_ID, "wifi_receiver");

    public static final Block WIFI_EMITTER = Registry.register(
            Registries.BLOCK,
            WIFI_EMITTER_ID,
            new WifiEmitterBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, WIFI_EMITTER_ID))
                    .nonOpaque()
                    .luminance(state -> state.get(WifiEmitterBlock.POWER))
                    .strength(1.5F)
                    .requiresTool())
    );

    public static final Block WIFI_RECEIVER = Registry.register(
            Registries.BLOCK,
            WIFI_RECEIVER_ID,
            new WifiReceiverBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, WIFI_RECEIVER_ID))
                    .nonOpaque()
                    .luminance(state -> state.get(WifiReceiverBlock.POWER))
                    .strength(1.5F)
                    .requiresTool())
    );

    public static final Item WIFI_EMITTER_ITEM = Registry.register(
            Registries.ITEM,
            WIFI_EMITTER_ID,
            new BlockItem(WIFI_EMITTER, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, WIFI_EMITTER_ID)).useBlockPrefixedTranslationKey())
    );

    public static final Item WIFI_RECEIVER_ITEM = Registry.register(
            Registries.ITEM,
            WIFI_RECEIVER_ID,
            new BlockItem(WIFI_RECEIVER, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, WIFI_RECEIVER_ID)).useBlockPrefixedTranslationKey())
    );

    private ModBlocks() {
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(WIFI_EMITTER_ITEM);
            entries.add(WIFI_RECEIVER_ITEM);
        });
    }
}