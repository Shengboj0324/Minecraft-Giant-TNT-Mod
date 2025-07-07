package com.yourname.gianttntmod.entities;

import com.yourname.gianttntmod.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class GiantTNTEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(GiantTNTEntity.class, EntityDataSerializers.INT);
    private static final int DEFAULT_FUSE_TIME = 160; // 8 seconds at 20 ticks per second
    private static final double LAUNCH_VELOCITY = 2.5; // Initial upward velocity
    private static final float EXPLOSION_RADIUS = 325.0F; // 650 block diameter = 325 block radius
    private static final boolean BREAKS_BLOCKS = true;
    
    @Nullable
    private LivingEntity owner;
    private boolean hasLaunched = false;
    private int groundTickDelay = 40; // 2 second delay after hitting ground
    private boolean hasHitGround = false;

    public GiantTNTEntity(EntityType<? extends GiantTNTEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public GiantTNTEntity(Level level, double x, double y, double z, @Nullable LivingEntity owner) {
        this(ModEntities.GIANT_TNT.get(), level);
        this.setPos(x, y, z);
        double d0 = level.random.nextDouble() * (Math.PI * 2D);
        this.setDeltaMovement(-Math.sin(d0) * 0.02D, LAUNCH_VELOCITY, -Math.cos(d0) * 0.02D);
        this.setFuse(DEFAULT_FUSE_TIME);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;
        this.hasLaunched = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_FUSE_ID, DEFAULT_FUSE_TIME);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));

        if (this.onGround) {
            if (!hasHitGround) {
                hasHitGround = true;
                // Play a heavy impact sound
                if (!this.level.isClientSide) {
                    this.level.playSound(null, this.blockPosition(), SoundEvents.ANVIL_LAND, 
                        SoundSource.BLOCKS, 4.0F, 0.5F);
                }
            }
            
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
            
            if (hasHitGround) {
                groundTickDelay--;
                if (groundTickDelay <= 0) {
                    this.setFuse(Math.min(this.getFuse(), 10)); // Reduce fuse to explode soon
                }
            }
        }

        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);

        if (fuse <= 0) {
            this.discard();
            if (!this.level.isClientSide) {
                this.explode();
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            
            // Enhanced particle effects
            if (this.level.isClientSide) {
                // Main fuse particles
                this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
                
                // Additional dramatic particles when close to explosion
                if (fuse < 20) {
                    for (int i = 0; i < 3; i++) {
                        this.level.addParticle(ParticleTypes.LAVA, 
                            this.getX() + (this.random.nextDouble() - 0.5D) * 2.0D,
                            this.getY() + this.random.nextDouble() * 2.0D,
                            this.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D,
                            0.0D, 0.0D, 0.0D);
                    }
                }
                
                // Trail particles when flying
                if (!this.onGround && hasLaunched) {
                    for (int i = 0; i < 5; i++) {
                        this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            this.getX() + (this.random.nextDouble() - 0.5D) * 1.0D,
                            this.getY() - 0.5D,
                            this.getZ() + (this.random.nextDouble() - 0.5D) * 1.0D,
                            0.0D, -0.1D, 0.0D);
                    }
                }
            }
        }
    }

    private void explode() {
        // Play multiple explosion sounds for dramatic effect
        this.level.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, 
            SoundSource.BLOCKS, 16.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
        
        // Create the massive explosion
        if (this.level instanceof ServerLevel serverLevel) {
            // Create multiple smaller explosions in a pattern for better performance
            int explosionCount = 25; // Number of smaller explosions
            float baseRadius = EXPLOSION_RADIUS / 5.0F; // Each explosion is smaller
            
            for (int i = 0; i < explosionCount; i++) {
                double angle = (2 * Math.PI * i) / explosionCount;
                double distance = (i % 5) * (EXPLOSION_RADIUS / 5.0F); // Vary distance
                
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;
                double offsetY = (i % 3 - 1) * 10; // Some vertical variation
                
                BlockPos explosionPos = new BlockPos(
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ
                );
                
                // Create explosion at this position
                Explosion explosion = new Explosion(this.level, this, null, null,
                    explosionPos.getX(), explosionPos.getY(), explosionPos.getZ(),
                    baseRadius, BREAKS_BLOCKS, Explosion.BlockInteraction.BREAK);
                
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
            
            // Add dramatic particle effects
            for (int i = 0; i < 500; i++) {
                double d0 = this.random.nextGaussian() * EXPLOSION_RADIUS / 4.0D;
                double d1 = this.random.nextGaussian() * EXPLOSION_RADIUS / 4.0D;
                double d2 = this.random.nextGaussian() * EXPLOSION_RADIUS / 4.0D;
                
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX() + d0, this.getY() + d1, this.getZ() + d2,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE_ID, fuse);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putShort("Fuse", (short) this.getFuse());
        compound.putBoolean("HasLaunched", this.hasLaunched);
        compound.putBoolean("HasHitGround", this.hasHitGround);
        compound.putInt("GroundTickDelay", this.groundTickDelay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setFuse(compound.getShort("Fuse"));
        this.hasLaunched = compound.getBoolean("HasLaunched");
        this.hasHitGround = compound.getBoolean("HasHitGround");
        this.groundTickDelay = compound.getInt("GroundTickDelay");
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    protected float getEyeHeight() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
} 