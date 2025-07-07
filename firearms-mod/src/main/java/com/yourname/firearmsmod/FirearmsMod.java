package com.yourname.firearmsmod;

import com.yourname.firearmsmod.init.ModItems;
import com.yourname.firearmsmod.init.ModEntities;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FirearmsMod.MODID)
public class FirearmsMod {
    public static final String MODID = "firearmsmod";
    private static final Logger LOGGER = LogManager.getLogger();

    public FirearmsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register deferred registers
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        
        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Firearms Mod has been loaded!");
    }
} 