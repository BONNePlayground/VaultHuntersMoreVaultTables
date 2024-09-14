package lv.id.bonne.vaulthunters.jewelpacktable.block;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import iskallia.vault.block.base.FacedBlock;
import lv.id.bonne.vaulthunters.jewelpacktable.block.entity.JewelSelectorTable;
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


/**
 * The Jewel selector table block.
 */
public class JewelSelectorTableBlock extends FacedBlock implements EntityBlock
{
    /**
     * Instantiates a new Jewel selector table block.
     */
    public JewelSelectorTableBlock()
    {
        super(Properties.of(Material.STONE).strength(0.5F).noOcclusion());
    }


    /**
     * This method returns the shape of current table.
     * @param state The block state.
     * @param level The level where block is located.
     * @param pos The position of the block.
     * @param context The collision content.
     * @return The VoxelShape of current table.
     */
    @Override
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state,
        @NotNull BlockGetter level,
        @NotNull BlockPos pos,
        @NotNull CollisionContext context)
    {
        return SHAPE;
    }


    /**
     * The interaction that happens when player click on block.
     * @param state The block state.
     * @param level The level where block is located.
     * @param pos The position of the block.
     * @param player The player who clicks on block.
     * @param hand The hand with which player clicks on block.
     * @param hit The Hit result.
     * @return Interaction result outcome.
     */
    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state,
        Level level,
        @NotNull BlockPos pos,
        @NotNull Player player,
        @NotNull InteractionHand hand,
        @NotNull BlockHitResult hit)
    {
        if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }
        else if (player instanceof ServerPlayer serverPlayer)
        {
            BlockEntity tile = level.getBlockEntity(pos);

            if (tile instanceof JewelSelectorTable vaultJewelApplicationStationTile)
            {
                NetworkHooks.openGui(serverPlayer, vaultJewelApplicationStationTile, buffer -> buffer.writeBlockPos(pos));
                return InteractionResult.SUCCESS;
            }
            else
            {
                return InteractionResult.SUCCESS;
            }
        }
        else
        {
            return InteractionResult.SUCCESS;
        }
    }


    /**
     * This method indicates if entities can path find over this block.
     * @param state The block state.
     * @param level Level where block is located.
     * @param pos Position of the block.
     * @param type The path finder type.
     * @return {@code false} always
     */
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        return false;
    }


    /**
     * This method drops all items from container when block is broken.
     * @param state The BlockState.
     * @param level Level where block is broken.
     * @param pos Position of broken block.
     * @param newState New block state.
     * @param isMoving Boolean if block is moving.
     */
    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            BlockEntity tile = level.getBlockEntity(pos);

            if (tile instanceof JewelSelectorTable)
            {
                JewelSelectorTable applicationStation = (JewelSelectorTable) tile;
                applicationStation.getInputInventory().getOverSizedContents().forEach((overSizedStack) ->
                {
                    overSizedStack.splitByStackSize().forEach((splitStack) ->
                    {
                        Containers.dropItemStack(level,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            splitStack);
                    });
                });
                applicationStation.getInputInventory().clearContent();

                applicationStation.getOutputInventory().getOverSizedContents().forEach((overSizedStack) ->
                {
                    overSizedStack.splitByStackSize().forEach((splitStack) ->
                    {
                        Containers.dropItemStack(level,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            splitStack);
                    });
                });
                applicationStation.getOutputInventory().clearContent();

                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }


    /**
     * This method creates a new block entity.
     * @param pos Position for block.
     * @param state Block state.
     * @return New block entity.
     */
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
    {
        return ModBlocks.JEWEL_SELECTOR_TABLE.create(pos, state);
    }

    /**
     * The constant SHAPE.
     */
    public static VoxelShape SHAPE = Shapes.or(
        Block.box(1.0, 10.0, 1.0, 15.0, 12.0, 15.0),
        Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0));
}
