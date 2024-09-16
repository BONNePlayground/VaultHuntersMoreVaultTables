package lv.id.bonne.vaulthunters.morevaulttables.block;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import lv.id.bonne.vaulthunters.morevaulttables.block.entity.JewelSelectorTableTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;


/**
 * The Jewel selector table block.
 */
public class JewelSelectorTableBlock extends HorizontalDirectionalBlock implements EntityBlock
{
    /**
     * Instantiates a new Jewel selector table block.
     */
    public JewelSelectorTableBlock(Properties properties, VoxelShape shape) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().
            setValue(FACING, Direction.NORTH));
        SHAPE = shape;
    }


    /**
     * Create block state definition
     * @param builder The definition builder.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }


    /**
     * This method allows to rotate block opposite to player.
     * @param context The placement context.
     * @return The new block state.
     */
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        return Objects.requireNonNull(super.getStateForPlacement(context)).
            setValue(FACING, context.getHorizontalDirection().getOpposite());
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

            if (tile instanceof JewelSelectorTableTileEntity vaultJewelApplicationStationTile)
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

            if (tile instanceof JewelSelectorTableTileEntity)
            {
                JewelSelectorTableTileEntity applicationStation = (JewelSelectorTableTileEntity) tile;
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
        return MoreVaultTablesReferences.JEWEL_SELECTOR_TABLE_TILE_ENTITY.create(pos, state);
    }


    /**
     * The constant SHAPE.
     */
    private final VoxelShape SHAPE;
}
