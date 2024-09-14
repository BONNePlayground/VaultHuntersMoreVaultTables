//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.init;


import iskallia.vault.client.atlas.TextureAtlasRegion;
import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.JewelSelectorTableTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;


@ObjectHolder(MoreVaultTablesMod.MODID)
public class MoreVaultTablesReferences
{
    @ObjectHolder("jewel_selector_table_block")
    public static final Block JEWEL_SELECTOR_TABLE_BLOCK = null;

    @ObjectHolder("jewel_selector_table_tile_entity")
    public static final BlockEntityType<JewelSelectorTableTileEntity> JEWEL_SELECTOR_TABLE_TILE_ENTITY = null;

    @ObjectHolder("jewel_selector_table_container")
    public static final MenuType<JewelSelectorTableContainer> JEWEL_SELECTOR_TABLE_CONTAINER = null;
}
