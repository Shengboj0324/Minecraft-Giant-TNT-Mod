package com.yourname.gianttntmod.explosion;

import com.yourname.gianttntmod.config.GiantTNTConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "gianttntmod")
public class ExplosionManager {
    
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<PhasedExplosion> queuedExplosions = new ArrayList<>();
    private static final Random random = new Random();
    
    public static class PhasedExplosion {
        public final ServerLevel level;
        public final Entity source;
        public final double centerX, centerY, centerZ;
        public final List<ExplosionPhase> phases;
        public int currentPhase;
        public int ticksUntilNextPhase;
        public int particlesSpawned;
        
        public PhasedExplosion(ServerLevel level, Entity source, double x, double y, double z) {
            this.level = level;
            this.source = source;
            this.centerX = x;
            this.centerY = y;
            this.centerZ = z;
            this.phases = createExplosionPhases();
            this.currentPhase = 0;
            this.ticksUntilNextPhase = 1; // Start immediately
            this.particlesSpawned = 0;
        }
        
        private List<ExplosionPhase> createExplosionPhases() {
            List<ExplosionPhase> phaseList = new ArrayList<>();
            int explosionsPerPhase = Math.max(1, GiantTNTConfig.subExplosionCount / GiantTNTConfig.explosionPhases);
            
            for (int phase = 0; phase < GiantTNTConfig.explosionPhases; phase++) {
                List<BlockPos> explosionPositions = new ArrayList<>();
                
                int startIndex = phase * explosionsPerPhase;
                int endIndex = Math.min(startIndex + explosionsPerPhase, GiantTNTConfig.subExplosionCount);
                
                for (int i = startIndex; i < endIndex; i++) {
                    double angle = (2 * Math.PI * i) / GiantTNTConfig.subExplosionCount;
                    double distance = (i % 5) * (GiantTNTConfig.explosionRadius / 5.0F);
                    
                    double offsetX = Math.cos(angle) * distance;
                    double offsetZ = Math.sin(angle) * distance;
                    double offsetY = (i % 3 - 1) * 10; // Vertical variation
                    
                    BlockPos explosionPos = new BlockPos(
                        centerX + offsetX,
                        centerY + offsetY,
                        centerZ + offsetZ
                    );
                    
                    explosionPositions.add(explosionPos);
                }
                
                phaseList.add(new ExplosionPhase(explosionPositions));
            }
            
            return phaseList;
        }
    }
    
    public static class ExplosionPhase {
        public final List<BlockPos> explosionPositions;
        
        public ExplosionPhase(List<BlockPos> positions) {
            this.explosionPositions = positions;
        }
    }
    
    public static void queueMassiveExplosion(ServerLevel level, Entity source, double x, double y, double z) {
        // Validate and clamp configuration values for safety
        if (!validateExplosionConfig()) {
            return; // Skip explosion if config is unsafe
        }
        
        // Play initial explosion sound
        level.playSound(null, new BlockPos(x, y, z), SoundEvents.GENERIC_EXPLODE, 
            SoundSource.BLOCKS, 16.0F, 0.7F);
            
        queuedExplosions.add(new PhasedExplosion(level, source, x, y, z));
    }
    
    /**
     * Validates explosion configuration to prevent crashes and performance issues.
     * Automatically clamps dangerous values and logs warnings.
     */
    private static boolean validateExplosionConfig() {
        boolean configValid = true;
        boolean needsConfigReload = false;
        
        // Prevent division by zero in explosion phases
        if (GiantTNTConfig.explosionPhases <= 0) {
            GiantTNTConfig.explosionPhases = 1;
            logConfigWarning("explosionPhases was 0 or negative, clamped to 1");
            configValid = false;
            needsConfigReload = true;
        }
        
        // Prevent massive explosion radius that could crash server
        if (GiantTNTConfig.explosionRadius > 1000.0) {
            GiantTNTConfig.explosionRadius = 1000.0;
            logConfigWarning("explosionRadius exceeded 1000 blocks, clamped to 1000");
            configValid = false;
            needsConfigReload = true;
        }
        
        // Prevent too many sub-explosions
        if (GiantTNTConfig.subExplosionCount > 200) {
            GiantTNTConfig.subExplosionCount = 200;
            logConfigWarning("subExplosionCount exceeded 200, clamped to 200");
            configValid = false;
            needsConfigReload = true;
        }
        
        // Prevent particle spam
        if (GiantTNTConfig.particleCount > 5000) {
            GiantTNTConfig.particleCount = 5000;
            logConfigWarning("particleCount exceeded 5000, clamped to 5000");
            configValid = false;
            needsConfigReload = true;
        }
        
        // Validate phases don't exceed sub-explosion count
        if (GiantTNTConfig.explosionPhases > GiantTNTConfig.subExplosionCount) {
            GiantTNTConfig.explosionPhases = GiantTNTConfig.subExplosionCount;
            logConfigWarning("explosionPhases exceeded subExplosionCount, adjusted to match");
            configValid = false;
        }
        
        // Warn about config file persistence
        if (needsConfigReload) {
            LOGGER.warn("Config values have been auto-corrected for safety. Please update config/gianttntmod-common.toml with the corrected values and restart or run /reload to make changes permanent.");
        }
        
        return configValid;
    }
    
    private static void logConfigWarning(String issue) {
        LOGGER.warn("Configuration issue detected and auto-corrected: {}", issue);
    }
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Iterator<PhasedExplosion> iterator = queuedExplosions.iterator();
        while (iterator.hasNext()) {
            PhasedExplosion explosion = iterator.next();
            
            explosion.ticksUntilNextPhase--;
            
            if (explosion.ticksUntilNextPhase <= 0) {
                // Execute current phase
                if (explosion.currentPhase < explosion.phases.size()) {
                    executeExplosionPhase(explosion, explosion.phases.get(explosion.currentPhase));
                    explosion.currentPhase++;
                    explosion.ticksUntilNextPhase = 2; // 2 ticks between phases
                } else {
                    // All phases complete
                    iterator.remove();
                }
            }
            
            // Spawn particles gradually
            spawnParticlesGradually(explosion);
        }
    }
    
    private static void executeExplosionPhase(PhasedExplosion explosion, ExplosionPhase phase) {
        float baseRadius = (float) (GiantTNTConfig.explosionRadius / 5.0F);
        List<Explosion> explosions = new ArrayList<>();
        
        // First pass: Create and explode all explosions to calculate affected blocks
        for (BlockPos pos : phase.explosionPositions) {
            Explosion.BlockInteraction interaction = GiantTNTConfig.breaksBlocks ? 
                Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE;
                
            Explosion exp = new Explosion(explosion.level, explosion.source, null, null,
                pos.getX(), pos.getY(), pos.getZ(), baseRadius, false, interaction);
                
            exp.explode(); // Calculate affected blocks and entities
            explosions.add(exp);
        }
        
        // Second pass: Finalize all explosions in one batch to reduce network overhead
        for (Explosion exp : explosions) {
            exp.finalizeExplosion(true); // Apply block destruction and send packets
        }
        
        // Play phase sound (quieter than initial)
        if (!phase.explosionPositions.isEmpty()) {
            BlockPos firstPos = phase.explosionPositions.get(0);
            explosion.level.playSound(null, firstPos, SoundEvents.GENERIC_EXPLODE, 
                SoundSource.BLOCKS, 8.0F, 0.8F + random.nextFloat() * 0.4F);
        }
    }
    
    private static void spawnParticlesGradually(PhasedExplosion explosion) {
        if (explosion.particlesSpawned >= GiantTNTConfig.particleCount) return;
        
        int particlesToSpawn = GiantTNTConfig.enablePerformanceMode ? 
            GiantTNTConfig.maxParticlesPerTick : 
            Math.min(25, GiantTNTConfig.particleCount - explosion.particlesSpawned);
            
        for (int i = 0; i < particlesToSpawn && explosion.particlesSpawned < GiantTNTConfig.particleCount; i++) {
            double d0 = random.nextGaussian() * GiantTNTConfig.explosionRadius / 4.0D;
            double d1 = random.nextGaussian() * GiantTNTConfig.explosionRadius / 4.0D;
            double d2 = random.nextGaussian() * GiantTNTConfig.explosionRadius / 4.0D;
            
            explosion.level.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                explosion.centerX + d0, explosion.centerY + d1, explosion.centerZ + d2,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
                
            explosion.particlesSpawned++;
        }
    }
}