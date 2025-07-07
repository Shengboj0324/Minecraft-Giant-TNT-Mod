package com.yourname.dimensionsmod.init;

import com.yourname.dimensionsmod.DimensionsMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class ModDimensions {
    public static final DeferredRegister<DimensionType> DIMENSION_TYPES = DeferredRegister.create(Registry.DIMENSION_TYPE_REGISTRY, DimensionsMod.MODID);

    // Dimension Keys
    public static final ResourceKey<Level> AETHER_LEVEL_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY, 
        new ResourceLocation(DimensionsMod.MODID, "aether"));
    
    public static final ResourceKey<Level> MIDDLE_LEVEL_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY, 
        new ResourceLocation(DimensionsMod.MODID, "middle"));

    // Dimension Type Keys
    public static final ResourceKey<DimensionType> AETHER_TYPE_KEY = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, 
        new ResourceLocation(DimensionsMod.MODID, "aether"));
    
    public static final ResourceKey<DimensionType> MIDDLE_TYPE_KEY = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, 
        new ResourceLocation(DimensionsMod.MODID, "middle"));

    public static void register(IEventBus eventBus) {
        DIMENSION_TYPES.register(eventBus);
    }

    public static void setupDimensions() {
        // Additional dimension setup if needed
    }
} 