package com.blakesredstonewifi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import com.blakesredstonewifi.registry.ModBlocks;

public class BlakesRedstoneWifiClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.WIFI_EMITTER, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.WIFI_RECEIVER, BlockRenderLayer.CUTOUT);
    }
}
