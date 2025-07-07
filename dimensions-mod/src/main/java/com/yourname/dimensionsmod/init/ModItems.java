package com.yourname.dimensionsmod.init;

import com.yourname.dimensionsmod.DimensionsMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DimensionsMod.MODID);

    // Glowing Stone Item
    public static final RegistryObject<Item> GLOWING_STONE = ITEMS.register("glowing_stone",
        () -> new BlockItem(ModBlocks.GLOWING_STONE.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
} 