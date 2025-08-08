package com.yourname.gianttntmod.entities;

import com.yourname.gianttntmod.config.GiantTNTConfig;
import com.yourname.gianttntmod.explosion.ExplosionManager;
import com.yourname.gianttntmod.init.ModEntities;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class GiantTNTEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(GiantTNTEntity.class, EntityDataSerializers.INT);
    private static final double LAUNCH_VELOCITY = 2.5; // Initial upward velocity
    private static final long GROUND_DELAY_MS = 2000; // 2 second delay after hitting ground
    
    @Nullable
    private LivingEntity owner;
    private boolean hasLaunched = false;
    private long groundHitTime = -1; // Game time when TNT hit ground
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
        this.setFuse(GiantTNTConfig.fuseTime);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;
        this.hasLaunched = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_FUSE_ID, GiantTNTConfig.fuseTime);
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
                groundHitTime = this.level.getGameTime();
                // Play a heavy impact sound
                if (!this.level.isClientSide) {
                    this.level.playSound(null, this.blockPosition(), SoundEvents.ANVIL_LAND, 
                        SoundSource.BLOCKS, 4.0F, 0.5F);
                }
            }
            
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
            
            if (hasHitGround && groundHitTime >= 0) {
                long timeSinceHit = (this.level.getGameTime() - groundHitTime) * 50; // Convert ticks to ms
                if (timeSinceHit >= GROUND_DELAY_MS) {
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
            
            // Enhanced particle effects (throttled for performance)
            if (this.level.isClientSide) {
                // Main fuse particles (every tick)
                this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
                
                // Additional dramatic particles when close to explosion (throttled)
                if (fuse < 20 && this.tickCount % 2 == 0) {
                    int particleCount = GiantTNTConfig.enablePerformanceMode ? 1 : 3;
                    for (int i = 0; i < particleCount; i++) {
                        this.level.addParticle(ParticleTypes.LAVA, 
                            this.getX() + (this.random.nextDouble() - 0.5D) * 2.0D,
                            this.getY() + this.random.nextDouble() * 2.0D,
                            this.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D,
                            0.0D, 0.0D, 0.0D);
                    }
                }
                
                // Trail particles when flying (throttled)
                if (!this.onGround && hasLaunched && this.tickCount % 3 == 0) {
                    int trailCount = GiantTNTConfig.enablePerformanceMode ? 2 : 5;
                    for (int i = 0; i < trailCount; i++) {
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
        // Use the new async explosion manager to prevent server lag
        if (this.level instanceof ServerLevel serverLevel) {
            ExplosionManager.queueMassiveExplosion(serverLevel, this, this.getX(), this.getY(), this.getZ());
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
        compound.putLong("GroundHitTime", this.groundHitTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setFuse(compound.getShort("Fuse"));
        this.hasLaunched = compound.getBoolean("HasLaunched");
        this.hasHitGround = compound.getBoolean("HasHitGround");
        this.groundHitTime = compound.getLong("GroundHitTime");
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