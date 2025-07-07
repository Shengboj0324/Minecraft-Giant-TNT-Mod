package com.yourname.dimensionsmod.world.teleport;

import com.yourname.dimensionsmod.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TeleportInterceptor {
    private static final String TELEPORT_DATA_KEY = "DimensionsMod_TeleportData";
    private static final String DESTINATION_X_KEY = "DestX";
    private static final String DESTINATION_Y_KEY = "DestY";
    private static final String DESTINATION_Z_KEY = "DestZ";
    private static final String DESTINATION_DIM_KEY = "DestDim";
    private static final String TELEPORT_COOLDOWN_KEY = "TeleportCooldown";

    @SubscribeEvent
    public void onEnderPearlTeleport(EntityTeleportEvent.EnderPearl event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Only intercept teleportation in the overworld
            if (player.level.dimension() == Level.OVERWORLD) {
                // Calculate distance - if it's a significant teleport, route through Middle dimension
                double distance = player.blockPosition().distSqr(new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ()));
                
                if (distance > 100) { // 10+ block teleport gets routed through Middle dimension
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
                double distance = player.blockPosition().distSqr(new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ()));
                
                if (distance > 400) { // 20+ block teleport gets routed through Middle dimension
                    handleLongDistanceTeleport(player, event.getTargetX(), event.getTargetY(), event.getTargetZ(), Level.OVERWORLD);
                    event.setCanceled(true);
                }
            }
        }
    }

    private void handleLongDistanceTeleport(ServerPlayer player, double targetX, double targetY, double targetZ, net.minecraft.resources.ResourceKey<Level> targetDimension) {
        // Store the final destination in the player's data
        CompoundTag playerData = player.getPersistentData();
        CompoundTag teleportData = new CompoundTag();
        
        teleportData.putDouble(DESTINATION_X_KEY, targetX);
        teleportData.putDouble(DESTINATION_Y_KEY, targetY);
        teleportData.putDouble(DESTINATION_Z_KEY, targetZ);
        teleportData.putString(DESTINATION_DIM_KEY, targetDimension.location().toString());
        teleportData.putLong(TELEPORT_COOLDOWN_KEY, player.level.getGameTime() + 100); // 5 second transit time
        
        playerData.put(TELEPORT_DATA_KEY, teleportData);
        
        // Teleport to Middle dimension first
        DimensionTeleporter.teleportToDimension(player, ModDimensions.MIDDLE_LEVEL_KEY);
    }

    // Called every tick to check if players in Middle dimension should be teleported to their final destination
    public static void tickTeleportLogic(ServerLevel level) {
        if (level.dimension() == ModDimensions.MIDDLE_LEVEL_KEY) {
            for (ServerPlayer player : level.players()) {
                CompoundTag playerData = player.getPersistentData();
                
                if (playerData.contains(TELEPORT_DATA_KEY)) {
                    CompoundTag teleportData = playerData.getCompound(TELEPORT_DATA_KEY);
                    long cooldown = teleportData.getLong(TELEPORT_COOLDOWN_KEY);
                    
                    if (level.getGameTime() >= cooldown) {
                        // Time to complete the teleport
                        double destX = teleportData.getDouble(DESTINATION_X_KEY);
                        double destY = teleportData.getDouble(DESTINATION_Y_KEY);
                        double destZ = teleportData.getDouble(DESTINATION_Z_KEY);
                        String destDim = teleportData.getString(DESTINATION_DIM_KEY);
                        
                        // Remove the teleport data
                        playerData.remove(TELEPORT_DATA_KEY);
                        
                        // Teleport to final destination
                        if (destDim.equals(Level.OVERWORLD.location().toString())) {
                            DimensionTeleporter.teleportToDimension(player, Level.OVERWORLD);
                            // Set exact position after teleportation
                            player.teleportTo(destX, destY, destZ);
                        }
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
} 