package com.yourname.gianttntmod.commands;

import com.yourname.gianttntmod.config.GiantTNTConfig;
import com.yourname.gianttntmod.entities.GiantTNTEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "gianttntmod")
public class GiantTNTCommand {
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        dispatcher.register(Commands.literal("gianttnt")
            .requires(source -> source.hasPermission(2)) // OP level required
            .then(Commands.literal("test")
                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 1000))
                    .executes(GiantTNTCommand::executeTest)
                )
                .executes(context -> executeTest(context, 50)) // Default test radius
            )
            .then(Commands.literal("spawn")
                .then(Commands.argument("target", EntityArgument.player())
                    .executes(GiantTNTCommand::executeSpawn)
                )
                .executes(GiantTNTCommand::executeSpawnSelf)
            )
            .then(Commands.literal("config")
                .executes(GiantTNTCommand::executeConfig)
            )
            .executes(GiantTNTCommand::executeHelp)
        );
    }
    
    private static int executeTest(CommandContext<CommandSourceStack> context) {
        int radius = IntegerArgumentType.getInteger(context, "radius");
        return executeTest(context, radius);
    }
    
    private static int executeTest(CommandContext<CommandSourceStack> context, int radius) {
        CommandSourceStack source = context.getSource();
        
        if (!(source.getLevel() instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("Command can only be used in a world"));
            return 0;
        }
        
        // Temporarily override config for testing
        double originalRadius = GiantTNTConfig.explosionRadius;
        GiantTNTConfig.explosionRadius = radius;
        
        try {
            // Spawn test TNT at command source location
            double x = source.getPosition().x;
            double y = source.getPosition().y + 5; // Spawn slightly above
            double z = source.getPosition().z;
            
            long startTime = System.currentTimeMillis();
            
            GiantTNTEntity testTNT = new GiantTNTEntity(serverLevel, x, y, z, null);
            testTNT.setFuse(60); // 3 second fuse for testing
            serverLevel.addFreshEntity(testTNT);
            
            source.sendSuccess(Component.literal(
                String.format("Test Giant TNT spawned with radius %d blocks. Timer started...", radius)
            ), true);
            
            // Schedule a performance check after explosion
            serverLevel.getServer().execute(() -> {
                try {
                    Thread.sleep(5000); // Wait 5 seconds for explosion to complete
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    source.sendSuccess(Component.literal(
                        String.format("Test completed in %d ms. Check server console for performance metrics.", duration)
                    ), true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
        } finally {
            // Restore original config
            GiantTNTConfig.explosionRadius = originalRadius;
        }
        
        return 1;
    }
    
    private static int executeSpawn(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
            return spawnGiantTNTAtEntity(source, targetPlayer);
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to spawn Giant TNT: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int executeSpawnSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Entity entity = source.getEntity();
        
        if (!(entity instanceof Player player)) {
            source.sendFailure(Component.literal("Command must be used by a player"));
            return 0;
        }
        
        return spawnGiantTNTAtEntity(source, player);
    }
    
    private static int spawnGiantTNTAtEntity(CommandSourceStack source, Entity target) {
        if (!(source.getLevel() instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("Command can only be used in a world"));
            return 0;
        }
        
        double x = target.getX();
        double y = target.getY() + 10; // Spawn 10 blocks above target
        double z = target.getZ();
        
        GiantTNTEntity giantTNT = new GiantTNTEntity(serverLevel, x, y, z, 
            target instanceof Player ? (Player) target : null);
        serverLevel.addFreshEntity(giantTNT);
        
        source.sendSuccess(Component.literal(
            String.format("Giant TNT spawned above %s with %d second fuse", 
                target.getName().getString(), GiantTNTConfig.fuseTime / 20)
        ), true);
        
        return 1;
    }
    
    private static int executeConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        source.sendSuccess(Component.literal("=== Giant TNT Configuration ==="), false);
        source.sendSuccess(Component.literal(String.format("Explosion Radius: %.1f blocks", GiantTNTConfig.explosionRadius)), false);
        source.sendSuccess(Component.literal(String.format("Sub-explosions: %d", GiantTNTConfig.subExplosionCount)), false);
        source.sendSuccess(Component.literal(String.format("Explosion Phases: %d", GiantTNTConfig.explosionPhases)), false);
        source.sendSuccess(Component.literal(String.format("Particle Count: %d", GiantTNTConfig.particleCount)), false);
        source.sendSuccess(Component.literal(String.format("Fuse Time: %d ticks (%.1f seconds)", GiantTNTConfig.fuseTime, GiantTNTConfig.fuseTime / 20.0)), false);
        source.sendSuccess(Component.literal(String.format("Breaks Blocks: %s", GiantTNTConfig.breaksBlocks)), false);
        source.sendSuccess(Component.literal(String.format("Performance Mode: %s", GiantTNTConfig.enablePerformanceMode)), false);
        
        return 1;
    }
    
    private static int executeHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        source.sendSuccess(Component.literal("=== Giant TNT Commands ==="), false);
        source.sendSuccess(Component.literal("/gianttnt test [radius] - Spawn test TNT with optional custom radius"), false);
        source.sendSuccess(Component.literal("/gianttnt spawn [player] - Spawn Giant TNT above player"), false);
        source.sendSuccess(Component.literal("/gianttnt config - Show current configuration"), false);
        source.sendSuccess(Component.literal("Note: Edit config/gianttntmod-common.toml to change settings"), false);
        
        return 1;
    }
}