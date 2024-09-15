package lv.id.bonne.vaulthunters.morevaulttables.block.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import org.jetbrains.annotations.NotNull;
import java.util.List;

import iskallia.vault.block.BlackMarketBlock;
import iskallia.vault.item.JewelPouchItem;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.JewelSelectorTableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;


public class JewelSelectorTableRenderer implements BlockEntityRenderer<JewelSelectorTableTileEntity>
{
    public JewelSelectorTableRenderer(BlockEntityRendererProvider.Context context)
    {
    }


    public void render(JewelSelectorTableTileEntity jewelSelectorTable,
        float partialTicks,
        @NotNull PoseStack matrixStack,
        @NotNull MultiBufferSource buffer,
        int combinedLight,
        int combinedOverlay)
    {
        Level world = jewelSelectorTable.getLevel();

        if (world != null)
        {
            Direction dir = jewelSelectorTable.getBlockState().getValue(BlackMarketBlock.FACING);

            if (!jewelSelectorTable.getSelectedPouch().isEmpty())
            {
                List<JewelPouchItem.RolledJewel> jewels =
                    JewelPouchItem.getJewels(jewelSelectorTable.getSelectedPouch());

                for (int i = 0; i < jewels.size(); i++)
                {
                    JewelPouchItem.RolledJewel jewel = jewels.get(i);

                    matrixStack.pushPose();
                    this.renderInputItem(matrixStack,
                        buffer,
                        combinedLight,
                        combinedOverlay,
                        0.64F,
                        0.35F,
                        jewel.stack(),
                        dir,
                        i);
                    matrixStack.popPose();
                }

                matrixStack.pushPose();
                this.renderOutputItem(matrixStack,
                    buffer,
                    combinedLight,
                    combinedOverlay,
                    0.64F,
                    0.35F,
                    jewelSelectorTable.getSelectedPouch(),
                    dir,
                    0);
                matrixStack.popPose();
            }
        }
    }


    private void renderInputItem(PoseStack matrixStack,
        MultiBufferSource buffer,
        int lightLevel,
        int overlay,
        float yOffset,
        float scale,
        ItemStack itemStack,
        Direction dir,
        int i)
    {
        Minecraft minecraft = Minecraft.getInstance();
        matrixStack.pushPose();
        matrixStack.translate(0.5, yOffset, 0.5);
        matrixStack.scale(scale, scale, scale);
        BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
        boolean is3d = bakedModel.isGui3d();
        Block itemBlock = ForgeRegistries.BLOCKS.getValue(itemStack.getItem().getRegistryName());
        boolean shouldLower = is3d && (itemBlock == null || itemBlock == Blocks.AIR);
        int rot = 0;
        if (dir == Direction.WEST)
        {
            rot = 90;
        }

        if (dir == Direction.SOUTH)
        {
            rot = 180;
        }

        if (dir == Direction.EAST)
        {
            rot = 270;
        }

        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) rot));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.translate(i == 0 ? 0.0 : (i == 1 ? 0.8 : -0.8),
            0.7 + (i == 0 ? 0.0 : -0.05),
            (i == 0 ? -0.01 : 0.0) - (is3d ? (shouldLower ? 0.05 : 0.2) : 0.0));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(i == 0 ? 0.0F : (i == 1 ? -30.0F : 30.0F)));
        minecraft.getItemRenderer().render(itemStack,
            ItemTransforms.TransformType.FIXED,
            true,
            matrixStack,
            buffer,
            lightLevel,
            overlay,
            bakedModel);
        matrixStack.popPose();
    }


    private void renderOutputItem(PoseStack matrixStack,
        MultiBufferSource buffer,
        int lightLevel,
        int overlay,
        float yOffset,
        float scale,
        ItemStack itemStack,
        Direction dir,
        int i)
    {
        Minecraft minecraft = Minecraft.getInstance();
        matrixStack.pushPose();
        matrixStack.translate(0.5, yOffset, 0.5);
        matrixStack.scale(scale, scale, scale);
        int rot = 0;
        if (dir == Direction.WEST)
        {
            rot = 90;
        }

        if (dir == Direction.SOUTH)
        {
            rot = 180;
        }

        if (dir == Direction.EAST)
        {
            rot = 270;
        }

        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) rot));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.translate(i == 0 ? 0.0 : (i == 1 ? 0.5 : (i == 2 ? -0.5 : (i == 3 ? -0.9 : 0.9))),
            -0.7 + (i == 0 ? 0.1 : 0.0),
            0.0 + (i != 1 && i != 2 ? 0.0 : -0.05));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(
            i == 0 ? 0.0F : (i == 1 ? -50.0F : (i == 2 ? 100.0F : (i == 3 ? 0.0F : 100.0F)))));
        BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
        minecraft.getItemRenderer().render(itemStack,
            ItemTransforms.TransformType.FIXED,
            true,
            matrixStack,
            buffer,
            lightLevel,
            overlay,
            bakedModel);
        matrixStack.popPose();
    }
}