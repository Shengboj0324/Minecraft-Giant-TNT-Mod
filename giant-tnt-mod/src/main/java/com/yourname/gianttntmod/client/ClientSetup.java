package com.yourname.gianttntmod.client;

import com.yourname.gianttntmod.GiantTNTMod;
import com.yourname.gianttntmod.client.renderer.GiantTNTRenderer;
import com.yourname.gianttntmod.init.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GiantTNTMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.GIANT_TNT.get(), GiantTNTRenderer::new);
    }
} 