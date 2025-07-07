package com.yourname.gianttntmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yourname.gianttntmod.entities.GiantTNTEntity;
import com.yourname.gianttntmod.init.ModBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantTNTRenderer extends EntityRenderer<GiantTNTEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public GiantTNTRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.0F; // Larger shadow
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(GiantTNTEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);
        
        int fuse = entity.getFuse();
        if (fuse - partialTicks + 1.0F < 10.0F) {
            float f = 1.0F - (fuse - partialTicks + 1.0F) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float scale = 2.0F + f * 0.3F; // Scale up as it's about to explode
            poseStack.scale(scale, scale, scale);
        } else {
            poseStack.scale(2.0F, 2.0F, 2.0F); // Default larger size
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5D, -0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        
        // Add some rotation for visual effect
        if (fuse > 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(fuse * 4.0F));
        }
        
        BlockState blockstate = ModBlocks.GIANT_TNT.get().defaultBlockState();
        
        // Make it glow when close to exploding
        int light = packedLight;
        if (fuse < 20) {
            light = 15728880; // Maximum light level
        }
        
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, buffer, light, net.minecraft.client.renderer.block.model.BakedModel.FACE_BAKERY.bakeOff());
        
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GiantTNTEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
} 