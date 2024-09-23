//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.init;


import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.CardSelectorTableTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.DollDismantlingTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.JewelSelectorTableTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.CardSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;


@ObjectHolder(MoreVaultTablesMod.MODID)
public class MoreVaultTablesReferences
{
    @ObjectHolder("jewel_sack_opener_block")
    public static final Block JEWEL_SELECTOR_TABLE_BLOCK = null;

    @ObjectHolder("jewel_sack_opener_tile_entity")
    public static final BlockEntityType<JewelSelectorTableTileEntity> JEWEL_SELECTOR_TABLE_TILE_ENTITY = null;

    @ObjectHolder("jewel_sack_opener_container")
    public static final MenuType<JewelSelectorTableContainer> JEWEL_SELECTOR_TABLE_CONTAINER = null;

    @ObjectHolder("card_pack_opener_block")
    public static final Block CARD_SELECTOR_TABLE_BLOCK = null;

    @ObjectHolder("card_pack_opener_tile_entity")
    public static final BlockEntityType<CardSelectorTableTileEntity> CARD_SELECTOR_TABLE_TILE_ENTITY = null;

    @ObjectHolder("card_pack_opener_container")
    public static final MenuType<CardSelectorTableContainer> CARD_SELECTOR_TABLE_CONTAINER = null;

    @ObjectHolder("doll_dismantling_block")
    public static final Block DOLL_DISMANTLING_BLOCK = null;

    @ObjectHolder("doll_dismantling_tile_entity")
    public static final BlockEntityType<DollDismantlingTileEntity> DOLL_DISMANTLING_TILE_ENTITY = null;
}
