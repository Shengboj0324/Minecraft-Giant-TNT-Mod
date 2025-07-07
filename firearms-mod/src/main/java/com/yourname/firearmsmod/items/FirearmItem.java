package com.yourname.firearmsmod.items;

import com.yourname.firearmsmod.entities.BulletEntity;
import com.yourname.firearmsmod.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class FirearmItem extends Item {
    private final FirearmType type;

    public enum FirearmType {
        PISTOL(8.0f, 1.5f, 15, 12, ModItems.PISTOL_AMMO),
        ASSAULT_RIFLE(12.0f, 2.0f, 30, 5, ModItems.RIFLE_AMMO),
        SNIPER_RIFLE(25.0f, 4.0f, 8, 30, ModItems.SNIPER_AMMO),
        SHOTGUN(15.0f, 1.0f, 6, 20, ModItems.SHOTGUN_SHELLS);

        private final float damage;
        private final float velocity;
        private final int magazineSize;
        private final int cooldown;
        private final RegistryObject<Item> ammoType;

        FirearmType(float damage, float velocity, int magazineSize, int cooldown, RegistryObject<Item> ammoType) {
            this.damage = damage;
            this.velocity = velocity;
            this.magazineSize = magazineSize;
            this.cooldown = cooldown;
            this.ammoType = ammoType;
        }

        public float getDamage() { return damage; }
        public float getVelocity() { return velocity; }
        public int getMagazineSize() { return magazineSize; }
        public int getCooldown() { return cooldown; }
        public Item getAmmoType() { return ammoType.get(); }
    }

    public FirearmItem(Properties properties, FirearmType type) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack weapon = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            // Check if player has ammo
            if (!player.isCreative() && !hasAmmo(player)) {
                // Play empty click sound
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.5F, 1.0F);
                return InteractionResultHolder.fail(weapon);
            }

            // Check cooldown
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(weapon);
            }

            // Fire the weapon
            fireWeapon(level, player, weapon);
            
            // Apply cooldown
            player.getCooldowns().addCooldown(this, type.getCooldown());
            
            // Consume ammo (if not creative)
            if (!player.isCreative()) {
                consumeAmmo(player);
            }
            
            // Damage weapon
            weapon.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.sidedSuccess(weapon, level.isClientSide());
    }

    private void fireWeapon(Level level, Player player, ItemStack weapon) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.getEyePosition().add(lookVec.scale(0.5));
        
        // Create different firing patterns based on weapon type
        if (type == FirearmType.SHOTGUN) {
            // Shotgun fires multiple pellets
            for (int i = 0; i < 8; i++) {
                Vec3 spread = lookVec.add(
                    (level.random.nextDouble() - 0.5) * 0.3,
                    (level.random.nextDouble() - 0.5) * 0.3,
                    (level.random.nextDouble() - 0.5) * 0.3
                ).normalize();
                
                createBullet(level, player, startPos, spread, type.getDamage() * 0.7f);
            }
        } else {
            // Single shot weapons
            Vec3 accuracy = lookVec;
            if (type != FirearmType.SNIPER_RIFLE) {
                // Add slight inaccuracy for non-sniper weapons
                accuracy = lookVec.add(
                    (level.random.nextDouble() - 0.5) * 0.05,
                    (level.random.nextDouble() - 0.5) * 0.05,
                    (level.random.nextDouble() - 0.5) * 0.05
                ).normalize();
            }
            
            createBullet(level, player, startPos, accuracy, type.getDamage());
        }

        // Play sound effects
        playFireSound(level, player);
        
        // Create muzzle flash particles
        createMuzzleFlash(level, startPos, lookVec);
        
        // Apply recoil to player
        applyRecoil(player);
    }

    private void createBullet(Level level, Player player, Vec3 startPos, Vec3 direction, float damage) {
        BulletEntity bullet = new BulletEntity(level, player, direction.scale(type.getVelocity()), damage);
        bullet.setPos(startPos.x, startPos.y, startPos.z);
        level.addFreshEntity(bullet);
    }

    private void playFireSound(Level level, Player player) {
        float pitch = 0.8F + level.random.nextFloat() * 0.4F;
        float volume = 1.0F;
        
        switch (type) {
            case PISTOL:
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, volume * 0.3F, pitch * 1.5F);
                break;
            case ASSAULT_RIFLE:
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, volume * 0.5F, pitch * 1.2F);
                break;
            case SNIPER_RIFLE:
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, volume * 0.8F, pitch * 0.8F);
                break;
            case SHOTGUN:
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, volume * 0.7F, pitch * 0.9F);
                break;
        }
    }

    private void createMuzzleFlash(Level level, Vec3 startPos, Vec3 direction) {
        if (level instanceof ServerLevel serverLevel) {
            Vec3 flashPos = startPos.add(direction.scale(0.8));
            
            // Create smoke and flame particles
            for (int i = 0; i < 10; i++) {
                Vec3 particleVel = direction.scale(0.1 + level.random.nextDouble() * 0.2)
                    .add((level.random.nextDouble() - 0.5) * 0.1,
                         (level.random.nextDouble() - 0.5) * 0.1,
                         (level.random.nextDouble() - 0.5) * 0.1);
                
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    flashPos.x, flashPos.y, flashPos.z, 1,
                    particleVel.x, particleVel.y, particleVel.z, 0.0);
                
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                    flashPos.x, flashPos.y, flashPos.z, 1,
                    particleVel.x, particleVel.y, particleVel.z, 0.0);
            }
        }
    }

    private void applyRecoil(Player player) {
        float recoilStrength = switch (type) {
            case PISTOL -> 0.02f;
            case ASSAULT_RIFLE -> 0.03f;
            case SNIPER_RIFLE -> 0.08f;
            case SHOTGUN -> 0.06f;
        };
        
        // Apply upward recoil
        player.setDeltaMovement(player.getDeltaMovement().add(0, recoilStrength, 0));
    }

    private boolean hasAmmo(Player player) {
        return player.getInventory().hasAnyMatching(stack -> 
            stack.getItem() == type.getAmmoType());
    }

    private void consumeAmmo(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == type.getAmmoType()) {
                stack.shrink(1);
                break;
            }
        }
    }

    public FirearmType getFirearmType() {
        return type;
    }
} 