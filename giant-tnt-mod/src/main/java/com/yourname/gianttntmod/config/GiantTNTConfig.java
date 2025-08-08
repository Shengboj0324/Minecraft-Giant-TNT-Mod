package com.yourname.gianttntmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = "gianttntmod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class GiantTNTConfig {
    
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static final ForgeConfigSpec.DoubleValue EXPLOSION_RADIUS;
    public static final ForgeConfigSpec.IntValue SUB_EXPLOSION_COUNT;
    public static final ForgeConfigSpec.IntValue EXPLOSION_PHASES;
    public static final ForgeConfigSpec.IntValue PARTICLE_COUNT;
    public static final ForgeConfigSpec.IntValue FUSE_TIME;
    public static final ForgeConfigSpec.BooleanValue BREAKS_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PERFORMANCE_MODE;
    public static final ForgeConfigSpec.IntValue MAX_PARTICLES_PER_TICK;
    
    static {
        BUILDER.push("Giant TNT Configuration");
        
        EXPLOSION_RADIUS = BUILDER
            .comment("Explosion radius in blocks (diameter = radius * 2)")
            .defineInRange("explosionRadius", 325.0, 1.0, 1000.0);
            
        SUB_EXPLOSION_COUNT = BUILDER
            .comment("Number of sub-explosions to create")
            .defineInRange("subExplosionCount", 25, 1, 100);
            
        EXPLOSION_PHASES = BUILDER
            .comment("Number of ticks to spread explosions across (prevents lag)")
            .defineInRange("explosionPhases", 10, 1, 100);
            
        PARTICLE_COUNT = BUILDER
            .comment("Total number of explosion particles")
            .defineInRange("particleCount", 100, 10, 2000);
            
        FUSE_TIME = BUILDER
            .comment("Fuse time in ticks (20 ticks = 1 second)")
            .defineInRange("fuseTime", 160, 20, 1200);
            
        BREAKS_BLOCKS = BUILDER
            .comment("Whether explosions break blocks")
            .define("breaksBlocks", true);
            
        ENABLE_PERFORMANCE_MODE = BUILDER
            .comment("Reduces particles and spreads explosions for better performance")
            .define("enablePerformanceMode", false);
            
        MAX_PARTICLES_PER_TICK = BUILDER
            .comment("Maximum particles to spawn per tick in performance mode")
            .defineInRange("maxParticlesPerTick", 20, 1, 100);
        
        BUILDER.pop();
    }
    
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    
    public static double explosionRadius;
    public static int subExplosionCount;
    public static int explosionPhases;
    public static int particleCount;
    public static int fuseTime;
    public static boolean breaksBlocks;
    public static boolean enablePerformanceMode;
    public static int maxParticlesPerTick;
    
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        explosionRadius = EXPLOSION_RADIUS.get();
        subExplosionCount = SUB_EXPLOSION_COUNT.get();
        explosionPhases = EXPLOSION_PHASES.get();
        particleCount = PARTICLE_COUNT.get();
        fuseTime = FUSE_TIME.get();
        breaksBlocks = BREAKS_BLOCKS.get();
        enablePerformanceMode = ENABLE_PERFORMANCE_MODE.get();
        maxParticlesPerTick = MAX_PARTICLES_PER_TICK.get();
    }
}