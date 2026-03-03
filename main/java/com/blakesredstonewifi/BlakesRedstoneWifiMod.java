package com.blakesredstonewifi;

import com.blakesredstonewifi.registry.ModBlockEntities;
import com.blakesredstonewifi.registry.ModBlocks;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlakesRedstoneWifiMod implements ModInitializer {
    public static final String MOD_ID = "blakesredstonewifi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModBlockEntities.register();
        LOGGER.info("Initialized {}", MOD_ID);
    }
}
