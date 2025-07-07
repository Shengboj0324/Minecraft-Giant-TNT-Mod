package com.yourname.dimensionsmod.init;

import com.yourname.dimensionsmod.DimensionsMod;
import com.yourname.dimensionsmod.blocks.AetherPortalBlock;
import com.yourname.dimensionsmod.blocks.BedrockPortalBlock;
import com.yourname.dimensionsmod.blocks.GlowingStoneBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DimensionsMod.MODID);

    // Glowing Stone for Aether Portal
    public static final RegistryObject<Block> GLOWING_STONE = BLOCKS.register("glowing_stone", 
        () -> new GlowingStoneBlock(BlockBehaviour.Properties.of(Material.STONE)
                .strength(2.0F, 6.0F)
                .sound(SoundType.STONE)
                .lightLevel((state) -> 15) // Full brightness
                .requiresCorrectToolForDrops()));

    // Aether Portal Block (invisible, like nether portal)
    public static final RegistryObject<Block> AETHER_PORTAL = BLOCKS.register("aether_portal",
        () -> new AetherPortalBlock(BlockBehaviour.Properties.of(Material.PORTAL)
                .strength(-1.0F)
                .sound(SoundType.GLASS)
                .lightLevel((state) -> 11)
                .noCollission()
                .noOcclusion()));

    // Bedrock Portal Block (invisible, like nether portal)
    public static final RegistryObject<Block> BEDROCK_PORTAL = BLOCKS.register("bedrock_portal",
        () -> new BedrockPortalBlock(BlockBehaviour.Properties.of(Material.PORTAL)
                .strength(-1.0F)
                .sound(SoundType.GLASS)
                .lightLevel((state) -> 8)
                .noCollission()
                .noOcclusion()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
} 