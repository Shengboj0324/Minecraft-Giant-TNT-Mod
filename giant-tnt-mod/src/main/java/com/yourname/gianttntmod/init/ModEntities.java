package com.yourname.gianttntmod.init;

import com.yourname.gianttntmod.GiantTNTMod;
import com.yourname.gianttntmod.entities.GiantTNTEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GiantTNTMod.MODID);

    public static final RegistryObject<EntityType<GiantTNTEntity>> GIANT_TNT = ENTITIES.register("giant_tnt",
        () -> EntityType.Builder.<GiantTNTEntity>of(GiantTNTEntity::new, MobCategory.MISC)
                .sized(2.0F, 2.0F) // Larger than normal TNT
                .clientTrackingRange(16)
                .updateInterval(4)
                .build("giant_tnt"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
} 