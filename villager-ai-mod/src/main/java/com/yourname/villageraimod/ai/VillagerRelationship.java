package com.yourname.villageraimod.ai;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerRelationship {
    private final Map<UUID, Float> playerRelationships = new HashMap<>();
    private final Map<UUID, VillagerEmotion> playerEmotions = new HashMap<>();
    private final Map<UUID, Long> lastInteraction = new HashMap<>();
    private final Map<UUID, Integer> conversationDepth = new HashMap<>();
    
    // Constants for relationship changes
    private static final float POSITIVE_INTERACTION = 0.05f;
    private static final float NEGATIVE_INTERACTION = -0.08f;
    private static final float GIFT_BONUS = 0.15f;
    private static final float ATTACK_PENALTY = -0.25f;
    private static final float TRUST_DECAY_RATE = 0.001f; // Daily decay when not interacting

    public float getRelationshipLevel(UUID playerId) {
        return playerRelationships.getOrDefault(playerId, 0.5f); // Start neutral
    }

    public VillagerEmotion getCurrentEmotion(UUID playerId) {
        return playerEmotions.getOrDefault(playerId, VillagerEmotion.NEUTRAL);
    }

    public void updateRelationship(UUID playerId, RelationshipChange change, float intensity) {
        float currentLevel = getRelationshipLevel(playerId);
        float newLevel = currentLevel;
        
        switch (change) {
            case POSITIVE_INTERACTION:
                newLevel += POSITIVE_INTERACTION * intensity;
                break;
            case NEGATIVE_INTERACTION:
                newLevel += NEGATIVE_INTERACTION * intensity;
                break;
            case GIFT_RECEIVED:
                newLevel += GIFT_BONUS * intensity;
                break;
            case ATTACKED:
                newLevel += ATTACK_PENALTY * intensity;
                break;
            case HELPED:
                newLevel += POSITIVE_INTERACTION * 2.0f * intensity;
                break;
            case IGNORED:
                newLevel += NEGATIVE_INTERACTION * 0.5f * intensity;
                break;
        }
        
        // Clamp between 0 and 1
        newLevel = Math.max(0.0f, Math.min(1.0f, newLevel));
        
        playerRelationships.put(playerId, newLevel);
        playerEmotions.put(playerId, VillagerEmotion.fromPositivity(newLevel));
        lastInteraction.put(playerId, System.currentTimeMillis());
        
        // Update conversation depth
        if (change == RelationshipChange.POSITIVE_INTERACTION) {
            int depth = conversationDepth.getOrDefault(playerId, 0);
            conversationDepth.put(playerId, Math.min(depth + 1, 10));
        }
    }

    public void processTimeDecay() {
        long currentTime = System.currentTimeMillis();
        long dayInMillis = 24 * 60 * 60 * 1000; // 1 day
        
        for (UUID playerId : playerRelationships.keySet()) {
            long lastInteract = lastInteraction.getOrDefault(playerId, currentTime);
            long timeDiff = currentTime - lastInteract;
            
            if (timeDiff > dayInMillis) {
                // Slowly decay relationship towards neutral
                float currentLevel = playerRelationships.get(playerId);
                float decayAmount = TRUST_DECAY_RATE * (timeDiff / dayInMillis);
                
                if (currentLevel > 0.5f) {
                    currentLevel = Math.max(0.5f, currentLevel - decayAmount);
                } else if (currentLevel < 0.5f) {
                    currentLevel = Math.min(0.5f, currentLevel + decayAmount);
                }
                
                playerRelationships.put(playerId, currentLevel);
                playerEmotions.put(playerId, VillagerEmotion.fromPositivity(currentLevel));
            }
        }
    }

    public boolean willFollow(UUID playerId) {
        float relationship = getRelationshipLevel(playerId);
        return relationship > 0.75f; // High trust required to follow
    }

    public boolean willObey(UUID playerId) {
        float relationship = getRelationshipLevel(playerId);
        return relationship > 0.6f; // Moderate trust required to obey commands
    }

    public boolean isHostile(UUID playerId) {
        float relationship = getRelationshipLevel(playerId);
        return relationship < 0.2f; // Very low relationship causes hostility
    }

    public int getConversationDepth(UUID playerId) {
        return conversationDepth.getOrDefault(playerId, 0);
    }

    public String getPersonalizedMessage(UUID playerId, MessageType messageType) {
        VillagerEmotion emotion = getCurrentEmotion(playerId);
        float relationship = getRelationshipLevel(playerId);
        int depth = getConversationDepth(playerId);
        
        return MessageGenerator.generateMessage(emotion, relationship, depth, messageType);
    }

    public CompoundTag saveToNBT() {
        CompoundTag compound = new CompoundTag();
        
        // Save relationships
        CompoundTag relationshipsTag = new CompoundTag();
        for (Map.Entry<UUID, Float> entry : playerRelationships.entrySet()) {
            relationshipsTag.putFloat(entry.getKey().toString(), entry.getValue());
        }
        compound.put("relationships", relationshipsTag);
        
        // Save last interactions
        CompoundTag interactionsTag = new CompoundTag();
        for (Map.Entry<UUID, Long> entry : lastInteraction.entrySet()) {
            interactionsTag.putLong(entry.getKey().toString(), entry.getValue());
        }
        compound.put("interactions", interactionsTag);
        
        // Save conversation depths
        CompoundTag depthTag = new CompoundTag();
        for (Map.Entry<UUID, Integer> entry : conversationDepth.entrySet()) {
            depthTag.putInt(entry.getKey().toString(), entry.getValue());
        }
        compound.put("depths", depthTag);
        
        return compound;
    }

    public void loadFromNBT(CompoundTag compound) {
        // Load relationships
        if (compound.contains("relationships")) {
            CompoundTag relationshipsTag = compound.getCompound("relationships");
            for (String key : relationshipsTag.getAllKeys()) {
                try {
                    UUID playerId = UUID.fromString(key);
                    float relationship = relationshipsTag.getFloat(key);
                    playerRelationships.put(playerId, relationship);
                    playerEmotions.put(playerId, VillagerEmotion.fromPositivity(relationship));
                } catch (IllegalArgumentException e) {
                    // Invalid UUID, skip
                }
            }
        }
        
        // Load interactions
        if (compound.contains("interactions")) {
            CompoundTag interactionsTag = compound.getCompound("interactions");
            for (String key : interactionsTag.getAllKeys()) {
                try {
                    UUID playerId = UUID.fromString(key);
                    long time = interactionsTag.getLong(key);
                    lastInteraction.put(playerId, time);
                } catch (IllegalArgumentException e) {
                    // Invalid UUID, skip
                }
            }
        }
        
        // Load conversation depths
        if (compound.contains("depths")) {
            CompoundTag depthTag = compound.getCompound("depths");
            for (String key : depthTag.getAllKeys()) {
                try {
                    UUID playerId = UUID.fromString(key);
                    int depth = depthTag.getInt(key);
                    conversationDepth.put(playerId, depth);
                } catch (IllegalArgumentException e) {
                    // Invalid UUID, skip
                }
            }
        }
    }

    public enum RelationshipChange {
        POSITIVE_INTERACTION,
        NEGATIVE_INTERACTION,
        GIFT_RECEIVED,
        ATTACKED,
        HELPED,
        IGNORED
    }

    public enum MessageType {
        GREETING,
        FAREWELL,
        QUESTION,
        COMMAND_RESPONSE,
        SMALL_TALK,
        TRADE_OFFER,
        COMPLAINT,
        COMPLIMENT
    }
} 