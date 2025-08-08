package com.yourname.gianttntmod;

import com.yourname.gianttntmod.config.GiantTNTConfig;
import com.yourname.gianttntmod.init.ModBlocks;
import com.yourname.gianttntmod.init.ModEntities;
import com.yourname.gianttntmod.init.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GiantTNTMod.MODID)
public class GiantTNTMod {
    public static final String MODID = "gianttntmod";
    private static final Logger LOGGER = LogManager.getLogger();

    public GiantTNTMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register configuration
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GiantTNTConfig.SPEC);
        
        // Register deferred registers
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        
        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Some preinit code
        LOGGER.info("Giant TNT Mod has been loaded!");
    }
} 