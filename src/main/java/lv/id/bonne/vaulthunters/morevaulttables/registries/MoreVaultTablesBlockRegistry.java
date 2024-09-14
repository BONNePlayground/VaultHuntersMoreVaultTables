//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.registries;


import java.util.function.Supplier;

import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.JewelSelectorTableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


/**
 * The More vault tables mod block registry.
 */
public class MoreVaultTablesBlockRegistry
{
    /**
     * This method registers block into registry.
     *
     * @param name The name of the block.
     * @param block The block type
     * @param <T> Instance of Block.
     * @return Registered block.
     */
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = REGISTRY.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }


    /**
     * This method registers block item into registry.
     *
     * @param name The name of the block.
     * @param block The block type
     * @param <T> Instance of Block Item.
     * @return Registered block.
     */
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {
        return MoreVaultTablesItemRegistry.REGISTRY.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    /**
     * The constant JEWEL_TABLE_SHAPE.
     */
    public static VoxelShape JEWEL_TABLE_SHAPE = Shapes.or(
        Block.box(1.0, 10.0, 1.0, 15.0, 12.0, 15.0),
        Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0));

    /**
     * The Block registry
     */
    public static final DeferredRegister<Block> REGISTRY =
        DeferredRegister.create(ForgeRegistries.BLOCKS, MoreVaultTablesMod.MODID);

    /**
     * The Jewel Selector Table Block
     */
    public static final RegistryObject<Block> JEWEL_SELECTOR_TABLE_BLOCK = registerBlock("jewel_selector_table_block",
        () -> new JewelSelectorTableBlock(
            BlockBehaviour.Properties.copy(Blocks.STONECUTTER).
                strength(4f).
                sound(SoundType.CALCITE).
                requiresCorrectToolForDrops(),
            JEWEL_TABLE_SHAPE
        )
    );
}

