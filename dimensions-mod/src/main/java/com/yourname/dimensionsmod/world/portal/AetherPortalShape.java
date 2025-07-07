package com.yourname.dimensionsmod.world.portal;

import com.yourname.dimensionsmod.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AetherPortalShape {
    private static final int MIN_WIDTH = 4;
    private static final int MIN_HEIGHT = 5;
    private static final int MAX_WIDTH = 23;
    private static final int MAX_HEIGHT = 23;

    private final LevelAccessor level;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private final Direction leftDir;
    private int portalBlockCount;
    private BlockPos bottomLeft;
    private int height;
    private int width;

    public AetherPortalShape(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
        this.level = level;
        this.axis = axis;
        this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.leftDir = this.rightDir.getOpposite();
        this.bottomLeft = this.calculateBottomLeft(pos);
        
        if (this.bottomLeft == null) {
            this.bottomLeft = pos;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.calculateWidth();
            if (this.width > 0) {
                this.height = this.calculateHeight();
            }
        }
    }

    private BlockPos calculateBottomLeft(BlockPos pos) {
        for (int i = Math.max(0, pos.getY() - MAX_HEIGHT); pos.getY() >= i && isEmpty(this.level.getBlockState(pos.below())); pos = pos.below()) {
        }

        Direction direction = this.leftDir;
        for (int j = Math.max(1, MAX_WIDTH) - 1; j >= 0 && isEmpty(this.level.getBlockState(pos.relative(direction))); --j) {
            pos = pos.relative(direction);
        }

        Direction direction1 = this.rightDir;
        for (int k = Math.max(1, MAX_WIDTH) - 1; k >= 0 && isEmpty(this.level.getBlockState(pos.relative(direction1))) && isEmpty(this.level.getBlockState(pos.relative(direction1).below())); --k) {
            pos = pos.relative(direction1);
        }

        return pos;
    }

    private int calculateWidth() {
        int l = Math.max(1, MAX_WIDTH);
        for (int i = 0; i < l; ++i) {
            BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i);
            if (!isEmpty(this.level.getBlockState(blockpos)) || !isGlowingStone(this.level.getBlockState(blockpos.below()))) {
                return i;
            }
        }
        return l;
    }

    private int calculateHeight() {
        BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos();
        int i = Math.max(1, this.level.getMaxBuildHeight() - this.bottomLeft.getY());
        
        for (int j = 0; j < i; ++j) {
            for (int k = 0; k < this.width; ++k) {
                blockpos.setWithOffset(this.bottomLeft, this.rightDir, k).move(Direction.UP, j);
                BlockState blockstate = this.level.getBlockState(blockpos);
                if (!isEmpty(blockstate)) {
                    if (isGlowingStone(blockstate)) {
                        return j;
                    }
                    break;
                }

                if (k == 0) {
                    blockpos = new BlockPos.MutableBlockPos().setWithOffset(this.bottomLeft, this.rightDir, k).move(this.leftDir).move(Direction.UP, j);
                    if (!isGlowingStone(this.level.getBlockState(blockpos))) {
                        return j;
                    }
                }

                if (k == this.width - 1) {
                    blockpos = new BlockPos.MutableBlockPos().setWithOffset(this.bottomLeft, this.rightDir, k).move(this.rightDir).move(Direction.UP, j);
                    if (!isGlowingStone(this.level.getBlockState(blockpos))) {
                        return j;
                    }
                }
            }
        }

        return i;
    }

    private boolean isEmpty(BlockState state) {
        return state.isAir() || state.is(ModBlocks.AETHER_PORTAL.get());
    }

    private boolean isGlowingStone(BlockState state) {
        return state.is(ModBlocks.GLOWING_STONE.get());
    }

    public boolean isComplete() {
        return this.isValid() && this.portalBlockCount == this.width * this.height;
    }

    public boolean isValid() {
        return this.bottomLeft != null && this.width >= MIN_WIDTH && this.width <= MAX_WIDTH && this.height >= MIN_HEIGHT && this.height <= MAX_HEIGHT;
    }

    public void createPortalBlocks() {
        BlockState blockstate = ModBlocks.AETHER_PORTAL.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, this.axis);
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((pos) -> {
            this.level.setBlock(pos, blockstate, 18);
        });
    }

    public boolean isPortalAt(BlockPos pos) {
        return this.isValid() && pos.getY() >= this.bottomLeft.getY() && pos.getY() < this.bottomLeft.getY() + this.height && pos.getX() >= this.bottomLeft.getX() && pos.getZ() >= this.bottomLeft.getZ() && pos.getX() < this.bottomLeft.getX() + this.width && pos.getZ() < this.bottomLeft.getZ() + this.width;
    }
} 