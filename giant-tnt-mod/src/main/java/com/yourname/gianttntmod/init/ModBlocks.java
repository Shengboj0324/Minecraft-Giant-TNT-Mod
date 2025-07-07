package com.yourname.gianttntmod.init;

import com.yourname.gianttntmod.GiantTNTMod;
import com.yourname.gianttntmod.blocks.GiantTNTBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GiantTNTMod.MODID);

    public static final RegistryObject<Block> GIANT_TNT = BLOCKS.register("giant_tnt", 
        () -> new GiantTNTBlock(BlockBehaviour.Properties.of(Material.EXPLOSIVE)
                .strength(0.0F)
                .sound(SoundType.GRASS)
                .noOcclusion()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
} 