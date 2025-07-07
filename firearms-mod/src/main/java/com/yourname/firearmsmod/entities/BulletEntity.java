package com.yourname.firearmsmod.entities;

import com.yourname.firearmsmod.init.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class BulletEntity extends Entity {
    private Vec3 velocity;
    private float damage;
    private int tickCount = 0;
    private static final int MAX_LIFETIME = 100; // 5 seconds at 20 ticks per second
    @Nullable
    private LivingEntity owner;

    public BulletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.velocity = Vec3.ZERO;
        this.damage = 5.0f;
    }

    public BulletEntity(Level level, LivingEntity owner, Vec3 velocity, float damage) {
        this(ModEntities.BULLET.get(), level);
        this.owner = owner;
        this.velocity = velocity;
        this.damage = damage;
        this.setPos(owner.getEyePosition());
    }

    @Override
    protected void defineSynchedData() {
        // No synched data needed for bullets
    }

    @Override
    public void tick() {
        super.tick();
        
        tickCount++;
        
        // Remove bullet after maximum lifetime
        if (tickCount > MAX_LIFETIME) {
            this.discard();
            return;
        }

        // Get current position
        Vec3 currentPos = this.position();
        
        // Calculate next position
        Vec3 nextPos = currentPos.add(velocity);

        // Check for collisions
        HitResult hitResult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
            return;
        }

        // Move the bullet
        this.setPos(nextPos);

        // Apply gravity (very slight for realism)
        velocity = velocity.add(0, -0.001, 0);
        
        // Air resistance
        velocity = velocity.scale(0.998);

        // Create trail particles
        if (this.level.isClientSide && tickCount % 2 == 0) {
            this.level.addParticle(ParticleTypes.CRIT,
                currentPos.x, currentPos.y, currentPos.z,
                0.0, 0.0, 0.0);
        }
    }

    protected boolean canHitEntity(Entity entity) {
        return entity != this.owner && entity instanceof LivingEntity;
    }

    protected void onHit(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHit) {
            onHitEntity(entityHit);
        } else if (hitResult instanceof BlockHitResult blockHit) {
            onHitBlock(blockHit);
        }
        
        this.discard();
    }

    private void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();
        
        if (target instanceof LivingEntity livingTarget) {
            // Create damage source
            DamageSource damageSource = this.level.damageSources().thrown(this, this.owner);
            
            // Apply damage
            livingTarget.hurt(damageSource, this.damage);
            
            // Play hit sound
            this.level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 0.8F, 1.0F);
            
            // Create blood particles
            if (this.level.isClientSide) {
                for (int i = 0; i < 5; i++) {
                    this.level.addParticle(ParticleTypes.DAMAGE_INDICATOR,
                        target.getX() + (this.random.nextDouble() - 0.5) * target.getBbWidth(),
                        target.getY() + target.getBbHeight() * 0.5,
                        target.getZ() + (this.random.nextDouble() - 0.5) * target.getBbWidth(),
                        0.0, 0.1, 0.0);
                }
            }
        }
    }

    private void onHitBlock(BlockHitResult hitResult) {
        // Play ricochet sound
        this.level.playSound(null, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z,
            SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, 0.5F, 1.5F);
        
        // Create impact particles
        if (!this.level.isClientSide) {
            Vec3 hitPos = hitResult.getLocation();
            BlockState hitBlock = this.level.getBlockState(hitResult.getBlockPos());
            
            // Create block break particles
            for (int i = 0; i < 8; i++) {
                Vec3 particleVel = new Vec3(
                    (this.random.nextDouble() - 0.5) * 0.2,
                    this.random.nextDouble() * 0.2,
                    (this.random.nextDouble() - 0.5) * 0.2
                );
                
                this.level.addParticle(ParticleTypes.BLOCK_CRACK,
                    hitPos.x, hitPos.y, hitPos.z,
                    particleVel.x, particleVel.y, particleVel.z);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putDouble("VelX", this.velocity.x);
        compound.putDouble("VelY", this.velocity.y);
        compound.putDouble("VelZ", this.velocity.z);
        compound.putFloat("Damage", this.damage);
        compound.putInt("TickCount", this.tickCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.velocity = new Vec3(
            compound.getDouble("VelX"),
            compound.getDouble("VelY"),
            compound.getDouble("VelZ")
        );
        this.damage = compound.getFloat("Damage");
        this.tickCount = compound.getInt("TickCount");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        return false; // Bullets can't be damaged
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    public float getDamage() {
        return this.damage;
    }
} 