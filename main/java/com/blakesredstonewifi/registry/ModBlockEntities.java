package com.blakesredstonewifi.registry;

import com.blakesredstonewifi.BlakesRedstoneWifiMod;
import com.blakesredstonewifi.block.entity.WifiEmitterBlockEntity;
import com.blakesredstonewifi.block.entity.WifiReceiverBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {
    public static final BlockEntityType<WifiEmitterBlockEntity> WIFI_EMITTER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(BlakesRedstoneWifiMod.MOD_ID, "wifi_emitter"),
            FabricBlockEntityTypeBuilder.create(WifiEmitterBlockEntity::new, ModBlocks.WIFI_EMITTER).build()
    );

    public static final BlockEntityType<WifiReceiverBlockEntity> WIFI_RECEIVER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(BlakesRedstoneWifiMod.MOD_ID, "wifi_receiver"),
            FabricBlockEntityTypeBuilder.create(WifiReceiverBlockEntity::new, ModBlocks.WIFI_RECEIVER).build()
    );

    private ModBlockEntities() {
    }

    public static void register() {
    }
}
