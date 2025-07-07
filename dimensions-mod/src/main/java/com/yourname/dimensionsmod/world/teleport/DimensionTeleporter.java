package com.yourname.dimensionsmod.world.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class DimensionTeleporter {

    public static void teleportToDimension(Entity entity, ResourceKey<Level> targetDimension) {
        if (entity.level.isClientSide || !(entity.level instanceof ServerLevel)) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) entity.level;
        MinecraftServer server = serverLevel.getServer();
        ServerLevel targetLevel = server.getLevel(targetDimension);

        if (targetLevel == null) {
            return;
        }

        if (entity instanceof ServerPlayer player) {
            player.changeDimension(targetLevel, new CustomTeleporter(targetLevel));
        } else {
            entity.changeDimension(targetLevel, new CustomTeleporter(targetLevel));
        }
    }

    public static class CustomTeleporter implements ITeleporter {
        private final ServerLevel targetLevel;

        public CustomTeleporter(ServerLevel targetLevel) {
            this.targetLevel = targetLevel;
        }

        @Override
        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
            Entity repositionedEntity = repositionEntity.apply(false);
            if (repositionedEntity != null) {
                // Calculate spawn position based on dimension
                Vec3 spawnPos = calculateSpawnPosition(repositionedEntity, destWorld);
                repositionedEntity.teleportTo(spawnPos.x, spawnPos.y, spawnPos.z);
                repositionedEntity.setPortalCooldown();
            }
            return repositionedEntity;
        }

        @Override
        public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
            Vec3 spawnPos = calculateSpawnPosition(entity, destWorld);
            return new PortalInfo(spawnPos, Vec3.ZERO, entity.getYRot(), entity.getXRot());
        }

        private Vec3 calculateSpawnPosition(Entity entity, ServerLevel destWorld) {
            BlockPos entityPos = entity.blockPosition();
            
            // For Aether dimension, spawn at cloud level
            if (destWorld.dimension().location().getPath().equals("aether")) {
                return new Vec3(entityPos.getX(), 128, entityPos.getZ());
            }
            
            // For Middle dimension, spawn at similar Y level but ensure it's safe
            if (destWorld.dimension().location().getPath().equals("middle")) {
                return new Vec3(entityPos.getX(), Math.max(64, entityPos.getY()), entityPos.getZ());
            }
            
            // For overworld, try to find a safe spawn position
            BlockPos spawnPos = findSafeSpawnPosition(destWorld, entityPos);
            return new Vec3(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        }

        private BlockPos findSafeSpawnPosition(ServerLevel level, BlockPos originalPos) {
            // Start at the original position and search for a safe spawn
            for (int y = Math.max(1, originalPos.getY() - 10); y <= Math.min(level.getMaxBuildHeight() - 1, originalPos.getY() + 10); y++) {
                BlockPos testPos = new BlockPos(originalPos.getX(), y, originalPos.getZ());
                if (level.getBlockState(testPos).isAir() && 
                    level.getBlockState(testPos.above()).isAir() && 
                    !level.getBlockState(testPos.below()).isAir()) {
                    return testPos;
                }
            }
            
            // If no safe position found, use the original position + offset
            return new BlockPos(originalPos.getX(), Math.max(1, originalPos.getY()), originalPos.getZ());
        }
    }
} 