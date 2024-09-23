package lv.id.bonne.vaulthunters.morevaulttables.block.renderer;


import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import iskallia.vault.entity.entity.DollMiniMeEntity;
import iskallia.vault.init.ModEntities;
import iskallia.vault.item.VaultDollItem;
import lv.id.bonne.vaulthunters.morevaulttables.block.JewelSelectorTableBlock;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.DollDismantlingTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.JewelSelectorTableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;


public class DollDismantlingRenderer implements BlockEntityRenderer<DollDismantlingTileEntity>
{
    public DollDismantlingRenderer(BlockEntityRendererProvider.Context context)
    {
    }


    public void render(DollDismantlingTileEntity tileEntity,
        float partialTicks,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource buffer,
        int combinedLight,
        int combinedOverlay)
    {
        if (tileEntity.getDoll().isEmpty())
        {
            // Nothing to render.
            return;
        }

        DollMiniMeEntity miniMeEntity = tileEntity.getMiniMeEntity();

        if (miniMeEntity == null)
        {
            // Not an entity.
            return;
        }

        float y = 0.6f;

        long amount = tileEntity.getDoll().getOrCreateTag().getLong("amount");
        int totalItemsInDoll = tileEntity.getTotalItemsInDoll();

        float result = totalItemsInDoll * 1f / (amount == 0 ? Integer.MAX_VALUE : amount);
        y *= result;


        // Position the player model inside the block
        poseStack.pushPose();
        poseStack.translate(0.5, y, 0.5); // Adjust position to center inside the block
        poseStack.scale(0.6f, 0.6f, 0.6f); // Scale down the player model to fit the block

        long gameTime = tileEntity.getLevel().getGameTime();
        float rotationAngle = (gameTime % 360L) * 1.0f;

        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationAngle));

        // Use the player renderer to render the player
        Minecraft.getInstance().getEntityRenderDispatcher().
            getRenderer(miniMeEntity).
            render(miniMeEntity, 0.0f, partialTicks, poseStack, buffer, combinedLight);

        poseStack.popPose();


    }
}