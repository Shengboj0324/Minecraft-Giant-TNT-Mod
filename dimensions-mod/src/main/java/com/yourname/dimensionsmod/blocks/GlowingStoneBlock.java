package com.yourname.dimensionsmod.blocks;

import com.yourname.dimensionsmod.init.ModBlocks;
import com.yourname.dimensionsmod.world.portal.AetherPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GlowingStoneBlock extends Block {

    public GlowingStoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (itemStack.getItem() instanceof FlintAndSteelItem) {
            if (!level.isClientSide) {
                // Try to create an Aether portal
                AetherPortalShape portalShape = new AetherPortalShape(level, pos, net.minecraft.core.Direction.Axis.X);
                if (portalShape.isComplete()) {
                    portalShape.createPortalBlocks();
                    level.playSound(null, pos, SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.4F + 0.8F);
                    
                    if (!player.isCreative()) {
                        itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                    
                    return InteractionResult.SUCCESS;
                } else {
                    // Try the other axis
                    portalShape = new AetherPortalShape(level, pos, net.minecraft.core.Direction.Axis.Z);
                    if (portalShape.isComplete()) {
                        portalShape.createPortalBlocks();
                        level.playSound(null, pos, SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.4F + 0.8F);
                        
                        if (!player.isCreative()) {
                            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                        }
                        
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        
        return super.use(state, level, pos, player, hand, hit);
    }
} 