package lv.id.bonne.vaulthunters.jewelpacktable.block;

import org.jetbrains.annotations.Nullable;

import iskallia.vault.block.base.FacedBlock;
import javax.annotation.Nonnull;
import lv.id.bonne.vaulthunters.jewelpacktable.block.entity.VaultJewelApplicationStationTileEntity;
import lv.id.bonne.vaulthunters.jewelpacktable.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;


public class VaultJewelApplicationStationBlock extends FacedBlock implements EntityBlock {
  public static VoxelShape SHAPE = Shapes.or(Block.box(1.0, 10.0, 1.0, 15.0, 12.0, 15.0), Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0));

  public VaultJewelApplicationStationBlock() {
    super(Properties.of(Material.STONE).strength(0.5F).noOcclusion());
  }

  @Nonnull
  public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
    return SHAPE;
  }

  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    } else if (player instanceof ServerPlayer) {
      ServerPlayer sPlayer = (ServerPlayer)player;
      BlockEntity tile = level.getBlockEntity(pos);
      if (tile instanceof VaultJewelApplicationStationTileEntity) {
        VaultJewelApplicationStationTileEntity vaultJewelApplicationStationTile = (VaultJewelApplicationStationTileEntity)tile;
        NetworkHooks.openGui(sPlayer, vaultJewelApplicationStationTile, (buffer) -> {
          buffer.writeBlockPos(pos);
        });
        return InteractionResult.SUCCESS;
      } else {
        return InteractionResult.SUCCESS;
      }
    } else {
      return InteractionResult.SUCCESS;
    }
  }

  public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
    return false;
  }

  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      BlockEntity tile = level.getBlockEntity(pos);
      if (tile instanceof VaultJewelApplicationStationTileEntity) {
        VaultJewelApplicationStationTileEntity applicationStation = (VaultJewelApplicationStationTileEntity)tile;
        applicationStation.getInputInventory().getOverSizedContents().forEach((overSizedStack) -> {
          overSizedStack.splitByStackSize().forEach((splitStack) -> {
            Containers.dropItemStack(level, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), splitStack);
          });
        });
        applicationStation.getInputInventory().clearContent();

        applicationStation.getOutputInventory().getOverSizedContents().forEach((overSizedStack) -> {
          overSizedStack.splitByStackSize().forEach((splitStack) -> {
            Containers.dropItemStack(level, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), splitStack);
          });
        });
        applicationStation.getOutputInventory().clearContent();

        level.updateNeighbourForOutputSignal(pos, this);
      }
    }

    super.onRemove(state, level, pos, newState, isMoving);
  }

  @Nullable
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return ModBlocks.VAULT_JEWEL_APPLICATION_STATION_ENTITY.create(pos, state);
  }
}
