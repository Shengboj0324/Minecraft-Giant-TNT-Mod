package com.yourname.villageraimod.ai;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerAIEventHandler {
    private static final Map<UUID, VillagerRelationship> villagerRelationships = new HashMap<>();
    private static final String NBT_KEY = "VillagerAI_Relationships";

    @SubscribeEvent
    public void onPlayerInteractWithVillager(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Villager villager)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        UUID villagerId = villager.getUUID();
        UUID playerId = player.getUUID();
        
        VillagerRelationship relationship = getOrCreateRelationship(villager);
        
        // Handle different interaction types
        if (player.isCrouching()) {
            // Sneaking = trying to communicate
            handleCommunication(player, villager, relationship);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        } else {
            // Normal interaction
            relationship.updateRelationship(playerId, VillagerRelationship.RelationshipChange.POSITIVE_INTERACTION, 1.0f);
            
            VillagerEmotion emotion = relationship.getCurrentEmotion(playerId);
            String message = relationship.getPersonalizedMessage(playerId, VillagerRelationship.MessageType.GREETING);
            
            player.displayClientMessage(Component.literal("<Villager> " + message), false);
            
            // Save the updated relationship
            saveRelationshipToVillager(villager, relationship);
        }
    }

    @SubscribeEvent
    public void onVillagerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        UUID playerId = player.getUUID();
        VillagerRelationship relationship = getOrCreateRelationship(villager);
        
        // Being hurt severely damages relationship
        relationship.updateRelationship(playerId, VillagerRelationship.RelationshipChange.ATTACKED, 1.0f);
        
        String message = relationship.getPersonalizedMessage(playerId, VillagerRelationship.MessageType.COMPLAINT);
        player.displayClientMessage(Component.literal("<Villager> " + message), false);
        
        // If relationship is very bad, villager might become hostile
        if (relationship.isHostile(playerId)) {
            villager.setTarget(player);
        }
        
        saveRelationshipToVillager(villager, relationship);
    }

    private void handleCommunication(Player player, Villager villager, VillagerRelationship relationship) {
        UUID playerId = player.getUUID();
        
        // Generate context-appropriate message
        VillagerRelationship.MessageType messageType = determineMessageType(relationship, playerId);
        String message = relationship.getPersonalizedMessage(playerId, messageType);
        
        player.displayClientMessage(Component.literal("<Villager> " + message), false);
        
        // Check for special behaviors
        if (relationship.willFollow(playerId)) {
            player.displayClientMessage(Component.literal("§a[The villager looks ready to follow you!]"), false);
            // TODO: Implement following behavior
        } else if (relationship.willObey(playerId)) {
            player.displayClientMessage(Component.literal("§e[The villager seems willing to help you.]"), false);
        } else if (relationship.isHostile(playerId)) {
            player.displayClientMessage(Component.literal("§c[The villager glares at you with hostility!]"), false);
        }
        
        // Positive interaction for communication
        relationship.updateRelationship(playerId, VillagerRelationship.RelationshipChange.POSITIVE_INTERACTION, 0.5f);
        saveRelationshipToVillager(villager, relationship);
    }

    private VillagerRelationship.MessageType determineMessageType(VillagerRelationship relationship, UUID playerId) {
        float relationshipLevel = relationship.getRelationshipLevel(playerId);
        VillagerEmotion emotion = relationship.getCurrentEmotion(playerId);
        
        // Random selection based on emotion and relationship
        if (relationshipLevel < 0.3f) {
            return Math.random() < 0.7 ? VillagerRelationship.MessageType.COMPLAINT : VillagerRelationship.MessageType.QUESTION;
        } else if (relationshipLevel > 0.7f) {
            return Math.random() < 0.4 ? VillagerRelationship.MessageType.COMPLIMENT : VillagerRelationship.MessageType.SMALL_TALK;
        } else {
            VillagerRelationship.MessageType[] neutralTypes = {
                VillagerRelationship.MessageType.SMALL_TALK,
                VillagerRelationship.MessageType.QUESTION,
                VillagerRelationship.MessageType.TRADE_OFFER
            };
            return neutralTypes[(int)(Math.random() * neutralTypes.length)];
        }
    }

    private VillagerRelationship getOrCreateRelationship(Villager villager) {
        UUID villagerId = villager.getUUID();
        
        if (villagerRelationships.containsKey(villagerId)) {
            return villagerRelationships.get(villagerId);
        }
        
        // Try to load from NBT
        VillagerRelationship relationship = new VillagerRelationship();
        CompoundTag villagerData = villager.getPersistentData();
        
        if (villagerData.contains(NBT_KEY)) {
            relationship.loadFromNBT(villagerData.getCompound(NBT_KEY));
        }
        
        villagerRelationships.put(villagerId, relationship);
        return relationship;
    }

    private void saveRelationshipToVillager(Villager villager, VillagerRelationship relationship) {
        CompoundTag villagerData = villager.getPersistentData();
        villagerData.put(NBT_KEY, relationship.saveToNBT());
        
        // Also update in-memory cache
        villagerRelationships.put(villager.getUUID(), relationship);
    }

    // Periodic relationship decay
    public static void processRelationshipDecay() {
        for (VillagerRelationship relationship : villagerRelationships.values()) {
            relationship.processTimeDecay();
        }
    }
} 