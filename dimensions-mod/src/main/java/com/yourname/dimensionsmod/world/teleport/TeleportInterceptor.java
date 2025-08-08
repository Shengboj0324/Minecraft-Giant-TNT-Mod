package com.yourname.dimensionsmod.world.teleport;

import com.yourname.dimensionsmod.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * The TeleportInterceptor creates a mystical "Middle Dimension" experience by intercepting
 * long-distance teleportation and routing players through an ethereal transit realm.
 * 
 * This creates an immersive "dimensional rifts" effect where players briefly experience
 * the mysterious Middle Dimension before arriving at their final destination.
 */
public class TeleportInterceptor {
    private static final String TELEPORT_DATA_KEY = "DimensionsMod_TeleportData";
    private static final String DESTINATION_X_KEY = "DestX";
    private static final String DESTINATION_Y_KEY = "DestY";
    private static final String DESTINATION_Z_KEY = "DestZ";
    private static final String DESTINATION_DIM_KEY = "DestDim";
    private static final String TELEPORT_COOLDOWN_KEY = "TeleportCooldown";
    private static final String TELEPORT_START_TIME_KEY = "TeleportStartTime";
    
    // Teleportation thresholds for different types of teleportation
    private static final double ENDER_PEARL_THRESHOLD = 100; // 10+ block teleports
    private static final double GENERAL_TELEPORT_THRESHOLD = 400; // 20+ block teleports
    private static final long TRANSIT_TIME_TICKS = 100; // 5 seconds in Middle dimension
    private static final int TRANSIT_WARNING_TICKS = 60; // Warn player 3 seconds before teleport

    @SubscribeEvent
    public void onEnderPearlTeleport(EntityTeleportEvent.EnderPearl event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Only intercept teleportation in the overworld
            if (player.level.dimension() == Level.OVERWORLD) {
                BlockPos targetPos = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                double distance = player.blockPosition().distSqr(targetPos);
                
                if (distance > ENDER_PEARL_THRESHOLD) {
                    // Create mystical rift effect before teleportation
                    createDimensionalRiftEffects(player, targetPos);
                    
                    // Send atmospheric message
                    player.sendSystemMessage(Component.translatable("dimensionsmod.teleport.rift_opening")
                        .withStyle(style -> style.withColor(0x9932CC))); // Purple text
                    
                    handleLongDistanceTeleport(player, event.getTargetX(), event.getTargetY(), event.getTargetZ(), Level.OVERWORLD);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityTeleport(EntityTeleportEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !(event instanceof EntityTeleportEvent.EnderPearl)) {
            // Intercept other types of teleportation in the overworld
            if (player.level.dimension() == Level.OVERWORLD) {
                BlockPos targetPos = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                double distance = player.blockPosition().distSqr(targetPos);
                
                if (distance > GENERAL_TELEPORT_THRESHOLD) {
                    // Create more intense rift effects for longer teleports
                    createDimensionalRiftEffects(player, targetPos);
                    
                    // Send dramatic message for major teleportation
                    player.sendSystemMessage(Component.translatable("dimensionsmod.teleport.dimensional_rift")
                        .withStyle(style -> style.withColor(0x8A2BE2).withBold(true))); // Blue-violet, bold
                    
                    handleLongDistanceTeleport(player, event.getTargetX(), event.getTargetY(), event.getTargetZ(), Level.OVERWORLD);
                    event.setCanceled(true);
                }
            }
        }
    }

    /**
     * Initiates a mystical journey through the Middle Dimension.
     * The player is temporarily transported to an ethereal realm before reaching their final destination.
     */
    private void handleLongDistanceTeleport(ServerPlayer player, double targetX, double targetY, double targetZ, net.minecraft.resources.ResourceKey<Level> targetDimension) {
        // Store the final destination in the player's persistent data
        CompoundTag playerData = player.getPersistentData();
        CompoundTag teleportData = new CompoundTag();
        
        long currentTime = player.level.getGameTime();
        
        teleportData.putDouble(DESTINATION_X_KEY, targetX);
        teleportData.putDouble(DESTINATION_Y_KEY, targetY);
        teleportData.putDouble(DESTINATION_Z_KEY, targetZ);
        teleportData.putString(DESTINATION_DIM_KEY, targetDimension.location().toString());
        teleportData.putLong(TELEPORT_COOLDOWN_KEY, currentTime + TRANSIT_TIME_TICKS);
        teleportData.putLong(TELEPORT_START_TIME_KEY, currentTime);
        
        playerData.put(TELEPORT_DATA_KEY, teleportData);
        
        // Apply mystical transit effects
        applyTransitEffects(player);
        
        // Play dimensional rift sound
        player.level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 
            SoundSource.PLAYERS, 1.0F, 0.5F);
        
        // Send the player through the mystical Middle Dimension
        player.sendSystemMessage(Component.translatable("dimensionsmod.teleport.entering_middle")
            .withStyle(style -> style.withColor(0x483D8B).withItalic(true))); // Dark slate blue, italic
        
        DimensionTeleporter.teleportToDimension(player, ModDimensions.MIDDLE_LEVEL_KEY);
    }

    /**
     * Manages the mystical transit experience in the Middle Dimension.
     * Provides atmospheric feedback and completes the dimensional journey.
     */
    public static void tickTeleportLogic(ServerLevel level) {
        if (level.dimension() == ModDimensions.MIDDLE_LEVEL_KEY) {
            for (ServerPlayer player : level.players()) {
                CompoundTag playerData = player.getPersistentData();
                
                if (playerData.contains(TELEPORT_DATA_KEY)) {
                    CompoundTag teleportData = playerData.getCompound(TELEPORT_DATA_KEY);
                    long cooldown = teleportData.getLong(TELEPORT_COOLDOWN_KEY);
                    long startTime = teleportData.getLong(TELEPORT_START_TIME_KEY);
                    long currentTime = level.getGameTime();
                    long remainingTime = cooldown - currentTime;
                    
                    // Provide atmospheric updates during transit
                    provideTransitFeedback(player, level, startTime, currentTime, remainingTime);
                    
                    // Time to complete the mystical journey?
                    if (currentTime >= cooldown) {
                        completeDimensionalJourney(player, teleportData, playerData, level);
                    }
                }
            }
        }
    }

    // Check if a player is currently in transit through the Middle dimension
    public static boolean isInTransit(ServerPlayer player) {
        return player.getPersistentData().contains(TELEPORT_DATA_KEY);
    }

    // Get remaining transit time for a player
    public static long getTransitTimeRemaining(ServerPlayer player, ServerLevel level) {
        CompoundTag playerData = player.getPersistentData();
        if (playerData.contains(TELEPORT_DATA_KEY)) {
            CompoundTag teleportData = playerData.getCompound(TELEPORT_DATA_KEY);
            long cooldown = teleportData.getLong(TELEPORT_COOLDOWN_KEY);
            return Math.max(0, cooldown - level.getGameTime());
        }
        return 0;
    }
    
    /**
     * Creates stunning visual effects when a dimensional rift opens.
     * Spawns particles and plays atmospheric sounds.
     */
    private void createDimensionalRiftEffects(ServerPlayer player, BlockPos targetPos) {
        ServerLevel level = (ServerLevel) player.level;
        
        // Create swirling portal particles around the player
        for (int i = 0; i < 20; i++) {
            double angle = (i * Math.PI * 2) / 20.0;
            double radius = 2.0;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            double y = player.getY() + player.random.nextDouble() * 3.0;
            
            level.sendParticles(ParticleTypes.PORTAL, x, y, z, 1, 
                0.1 * (player.random.nextDouble() - 0.5), 
                0.1 * (player.random.nextDouble() - 0.5), 
                0.1 * (player.random.nextDouble() - 0.5), 0.1);
        }
        
        // Add mystical reverse particles
        for (int i = 0; i < 10; i++) {
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, 
                player.getX() + (player.random.nextDouble() - 0.5) * 4,
                player.getY() + player.random.nextDouble() * 2,
                player.getZ() + (player.random.nextDouble() - 0.5) * 4,
                1, 0, 0.1, 0, 0.05);
        }
        
        // Mystical ambient sound
        level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_AMBIENT, 
            SoundSource.PLAYERS, 0.8F, 1.2F);
    }
    
    /**
     * Applies beautiful magical effects to the player during dimensional transit.
     */
    private void applyTransitEffects(ServerPlayer player) {
        // Grant brief speed and regeneration during the mystical journey
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, TRANSIT_TIME_TICKS, 1, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, TRANSIT_TIME_TICKS, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, TRANSIT_TIME_TICKS, 1, false, false));
        
        // Visual effect particles around the player
        ServerLevel level = (ServerLevel) player.level;
        for (int i = 0; i < 30; i++) {
            level.sendParticles(ParticleTypes.ENCHANT, 
                player.getX() + (player.random.nextDouble() - 0.5) * 3,
                player.getY() + player.random.nextDouble() * 2,
                player.getZ() + (player.random.nextDouble() - 0.5) * 3,
                1, 0, 0.1, 0, 0.1);
        }
    }
    
    /**
     * Provides atmospheric feedback during the player's journey through the Middle Dimension.
     */
    private static void provideTransitFeedback(ServerPlayer player, ServerLevel level, long startTime, long currentTime, long remainingTime) {
        long elapsed = currentTime - startTime;
        
        // Warning message when about to teleport
        if (remainingTime == TRANSIT_WARNING_TICKS) {
            player.sendSystemMessage(Component.translatable("dimensionsmod.teleport.preparing_exit")
                .withStyle(style -> style.withColor(0xFFD700).withBold(true))); // Gold, bold
            
            // Play preparation sound
            level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRIGGER, 
                SoundSource.PLAYERS, 0.7F, 1.5F);
        }
        
        // Mystical ambient effects during transit
        if (elapsed % 20 == 0) { // Every second
            // Spawn ethereal particles around the player
            for (int i = 0; i < 5; i++) {
                level.sendParticles(ParticleTypes.END_ROD, 
                    player.getX() + (player.random.nextDouble() - 0.5) * 2,
                    player.getY() + player.random.nextDouble() * 2,
                    player.getZ() + (player.random.nextDouble() - 0.5) * 2,
                    1, 0, 0.1, 0, 0.05);
            }
        }
        
        // Progress indicator every 2 seconds
        if (elapsed % 40 == 0 && remainingTime > TRANSIT_WARNING_TICKS) {
            int secondsRemaining = (int) (remainingTime / 20);
            player.sendSystemMessage(Component.translatable("dimensionsmod.teleport.transit_progress", secondsRemaining)
                .withStyle(style -> style.withColor(0x9370DB))); // Medium purple
        }
    }
    
    /**
     * Completes the mystical dimensional journey with spectacular effects.
     */
    private static void completeDimensionalJourney(ServerPlayer player, CompoundTag teleportData, CompoundTag playerData, ServerLevel level) {
        // Extract destination information
        double destX = teleportData.getDouble(DESTINATION_X_KEY);
        double destY = teleportData.getDouble(DESTINATION_Y_KEY);
        double destZ = teleportData.getDouble(DESTINATION_Z_KEY);
        String destDim = teleportData.getString(DESTINATION_DIM_KEY);
        
        // Remove the teleport data
        playerData.remove(TELEPORT_DATA_KEY);
        
        // Create dramatic exit effects
        createExitPortalEffects(player, level);
        
        // Triumphant completion message
        player.sendSystemMessage(Component.translatable("dimensionsmod.teleport.journey_complete")
            .withStyle(style -> style.withColor(0x00FF00).withBold(true))); // Bright green, bold
        
        // Teleport to final destination
        if (destDim.equals(Level.OVERWORLD.location().toString())) {
            DimensionTeleporter.teleportToDimension(player, Level.OVERWORLD);
            
            // Precisely position the player at their intended destination
            player.getServer().execute(() -> {
                player.teleportTo(destX, destY, destZ);
                
                // Final arrival effects in overworld
                createArrivalEffects(player);
            });
        }
    }
    
    /**
     * Creates stunning portal exit effects in the Middle Dimension.
     */
    private static void createExitPortalEffects(ServerPlayer player, ServerLevel level) {
        // Spectacular portal explosion effect
        for (int i = 0; i < 50; i++) {
            double angle = player.random.nextDouble() * Math.PI * 2;
            double radius = player.random.nextDouble() * 4;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            double y = player.getY() + player.random.nextDouble() * 3;
            
            level.sendParticles(ParticleTypes.PORTAL, x, y, z, 1,
                Math.cos(angle) * 0.2, 0.2, Math.sin(angle) * 0.2, 0.3);
        }
        
        // Mystical exit sound
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 
            SoundSource.PLAYERS, 1.2F, 0.8F);
    }
    
    /**
     * Creates beautiful arrival effects when the player reaches their final destination.
     */
    private static void createArrivalEffects(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level;
        
        // Gentle arrival particles
        for (int i = 0; i < 20; i++) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, 
                player.getX() + (player.random.nextDouble() - 0.5) * 2,
                player.getY() + player.random.nextDouble() * 2,
                player.getZ() + (player.random.nextDouble() - 0.5) * 2,
                1, 0, 0.1, 0, 0.1);
        }
        
        // Soft arrival sound
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, 
            SoundSource.PLAYERS, 0.5F, 2.0F);
        
        // Brief beneficial effect for successful journey
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 0, false, false));
    }
} 