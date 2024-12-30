package lv.id.bonne.vaulthunters.morevaulttables.block.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import lv.id.bonne.vaulthunters.morevaulttables.block.CardSelectorTableBlock;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.CardSelectorTableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class CardSelectorTableRenderer implements BlockEntityRenderer<CardSelectorTableTileEntity>
{
    public CardSelectorTableRenderer(BlockEntityRendererProvider.Context context)
    {
    }


    public void render(CardSelectorTableTileEntity cardSelectorTable,
        float partialTicks,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource buffer,
        int combinedLight,
        int combinedOverlay)
    {
        ItemStack boosterPack = cardSelectorTable.getSelectedPack();

        if (!boosterPack.isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.83, 0.5);
            Direction facing = cardSelectorTable.getBlockState().getValue(CardSelectorTableBlock.FACING);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F - facing.toYRot()));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0.0, 0.0, -0.10);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getItemRenderer().renderStatic(boosterPack,
                ItemTransforms.TransformType.FIXED,
                combinedLight,
                combinedOverlay,
                poseStack,
                buffer,
                0);
            poseStack.popPose();

            if (cardSelectorTable.canOperate())
            {
                this.spawnParticles(cardSelectorTable.getLevel(), cardSelectorTable.getBlockPos());
            }
        }
    }


    private void spawnParticles(Level level, BlockPos pos)
    {
        if (level.isClientSide)
        { // Ensure we're on the client side
            Minecraft mc = Minecraft.getInstance();

            // Randomized offsets for particle positions
            double xOffset = level.random.nextDouble() * 0.5D - 0.3D;
            double yOffset = level.random.nextDouble() * 0.3D + 0.7D;
            double zOffset = level.random.nextDouble() * 0.5D - 0.3D;

            // Spawn redstone particle
            DustParticleOptions redstoneParticle =
                new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F);

            // Spawn the particle
            mc.particleEngine.createParticle(
                redstoneParticle,
                pos.getX() + 0.5D + xOffset,
                pos.getY() + yOffset,
                pos.getZ() + 0.5D + zOffset,
                0.0D, 0.0D, 0.0D
            );
        }
    }
}