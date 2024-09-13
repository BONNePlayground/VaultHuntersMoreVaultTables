package lv.id.bonne.vaulthunters.jewelpacktable.block.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import iskallia.vault.block.ToolStationBlock;
import javax.annotation.Nonnull;
import lv.id.bonne.vaulthunters.jewelpacktable.block.entity.VaultJewelApplicationStationTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;


public class JewelApplicationStationRenderer implements BlockEntityRenderer<VaultJewelApplicationStationTileEntity>
{
  public JewelApplicationStationRenderer(BlockEntityRendererProvider.Context context) {
  }

  public void render(VaultJewelApplicationStationTileEntity tile, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
    ItemStack tool = tile.getRenderedTool();
    if (!tool.isEmpty()) {
      poseStack.pushPose();
      poseStack.translate(0.5, 0.65, 0.5);
      Direction facing = (Direction)tile.getBlockState().getValue(ToolStationBlock.FACING);
      poseStack.translate(0.0, 0.21, 0.0);
      poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - facing.toYRot()));
      poseStack.translate(-0.2, 0.0, 0.0);
      poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(45.0F));
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getItemRenderer().renderStatic(tool, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, 0);
      poseStack.popPose();
    }

  }
}