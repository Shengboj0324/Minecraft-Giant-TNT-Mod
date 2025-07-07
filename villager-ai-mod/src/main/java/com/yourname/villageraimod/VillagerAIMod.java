package com.yourname.villageraimod;

import com.yourname.villageraimod.ai.VillagerAIEventHandler;
import com.yourname.villageraimod.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VillagerAIMod.MODID)
public class VillagerAIMod {
    public static final String MODID = "villageraimod";
    private static final Logger LOGGER = LogManager.getLogger();

    public VillagerAIMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        
        // Register event handlers
        MinecraftForge.EVENT_BUS.register(new VillagerAIEventHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.register();
        });
        LOGGER.info("Villager AI Mod has been loaded!");
    }
} 