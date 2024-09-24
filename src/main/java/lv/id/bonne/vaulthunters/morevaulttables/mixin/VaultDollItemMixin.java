//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import iskallia.vault.item.VaultDollItem;
import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.DollDismantlingTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;


/**
 * This mixin allows to block Vault Doll placement on ground if enabled in config.
 */
@Mixin(value = VaultDollItem.class)
public class VaultDollItemMixin
{
    @Inject(method = "useOn",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void onDollUse(UseOnContext context,
        CallbackInfoReturnable<InteractionResult> cir,
        ItemStack stack,
        Player player,
        Level level,
        ServerLevel serverLevel,
        BlockPos clickedPos,
        Direction direction,
        BlockState blockState)
    {
        if (blockState.is(MoreVaultTablesReferences.DOLL_DISMANTLING_BLOCK))
        {
            // This manages vault doll placement into dismantler, if it is empty.

            DollDismantlingTileEntity blockEntity =
                (DollDismantlingTileEntity) level.getBlockEntity(clickedPos);

            if (blockEntity != null && blockEntity.getDoll().isEmpty())
            {
                ItemStack clone = stack.copy();
                clone.setCount(1);
                stack.shrink(1);

                blockEntity.updateDoll(clone, (ServerPlayer) player);
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
        }

        if (MoreVaultTablesMod.CONFIGURATION.getBlockVaultDolls())
        {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
