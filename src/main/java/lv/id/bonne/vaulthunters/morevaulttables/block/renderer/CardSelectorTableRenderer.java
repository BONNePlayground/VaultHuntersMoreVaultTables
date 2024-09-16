package lv.id.bonne.vaulthunters.morevaulttables.block.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import iskallia.vault.block.ToolStationBlock;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.CardSelectorTableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;


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
            Direction facing = cardSelectorTable.getBlockState().getValue(ToolStationBlock.FACING);
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
        }
    }
}