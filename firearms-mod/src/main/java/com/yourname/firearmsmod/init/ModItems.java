package com.yourname.firearmsmod.init;

import com.yourname.firearmsmod.FirearmsMod;
import com.yourname.firearmsmod.items.FirearmItem;
import com.yourname.firearmsmod.items.AmmoItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FirearmsMod.MODID);

    // Firearms
    public static final RegistryObject<Item> ASSAULT_RIFLE = ITEMS.register("assault_rifle",
        () -> new FirearmItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(1).durability(500), 
            FirearmItem.FirearmType.ASSAULT_RIFLE));
    
    public static final RegistryObject<Item> SNIPER_RIFLE = ITEMS.register("sniper_rifle",
        () -> new FirearmItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(1).durability(300), 
            FirearmItem.FirearmType.SNIPER_RIFLE));
    
    public static final RegistryObject<Item> PISTOL = ITEMS.register("pistol",
        () -> new FirearmItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(1).durability(250), 
            FirearmItem.FirearmType.PISTOL));
    
    public static final RegistryObject<Item> SHOTGUN = ITEMS.register("shotgun",
        () -> new FirearmItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(1).durability(400), 
            FirearmItem.FirearmType.SHOTGUN));

    // Ammunition
    public static final RegistryObject<Item> RIFLE_AMMO = ITEMS.register("rifle_ammo",
        () -> new AmmoItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(64), AmmoItem.AmmoType.RIFLE));
    
    public static final RegistryObject<Item> PISTOL_AMMO = ITEMS.register("pistol_ammo",
        () -> new AmmoItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(64), AmmoItem.AmmoType.PISTOL));
    
    public static final RegistryObject<Item> SHOTGUN_SHELLS = ITEMS.register("shotgun_shells",
        () -> new AmmoItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(64), AmmoItem.AmmoType.SHOTGUN));
    
    public static final RegistryObject<Item> SNIPER_AMMO = ITEMS.register("sniper_ammo",
        () -> new AmmoItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(32), AmmoItem.AmmoType.SNIPER));

    // Crafting Components
    public static final RegistryObject<Item> GUN_BARREL = ITEMS.register("gun_barrel",
        () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    
    public static final RegistryObject<Item> GUN_STOCK = ITEMS.register("gun_stock",
        () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    
    public static final RegistryObject<Item> TRIGGER_MECHANISM = ITEMS.register("trigger_mechanism",
        () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    
    public static final RegistryObject<Item> GUNPOWDER_ENHANCED = ITEMS.register("gunpowder_enhanced",
        () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
} 