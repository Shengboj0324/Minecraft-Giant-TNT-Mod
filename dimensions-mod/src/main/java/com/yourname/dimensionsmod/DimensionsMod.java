package com.yourname.dimensionsmod;

import com.yourname.dimensionsmod.init.ModBlocks;
import com.yourname.dimensionsmod.init.ModDimensions;
import com.yourname.dimensionsmod.init.ModItems;
import com.yourname.dimensionsmod.world.teleport.TeleportInterceptor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DimensionsMod.MODID)
public class DimensionsMod {
    public static final String MODID = "dimensionsmod";
    private static final Logger LOGGER = LogManager.getLogger();

    public DimensionsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register deferred registers
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModDimensions.register(modEventBus);
        
        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        
        // Register event handlers
        MinecraftForge.EVENT_BUS.register(new TeleportInterceptor());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModDimensions.setupDimensions();
        });
        LOGGER.info("Dimensions Mod has been loaded!");
    }
} 