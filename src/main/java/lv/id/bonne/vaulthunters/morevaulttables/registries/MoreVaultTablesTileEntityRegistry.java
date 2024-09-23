//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.registries;


import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.CardSelectorTableTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.DollDismantlingTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.JewelSelectorTableTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


/**
 * The More vault tables tile entity registry.
 */
public class MoreVaultTablesTileEntityRegistry
{
    /**
     * The Tile Entity registry
     */
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MoreVaultTablesMod.MODID);


    /**
     * The Jewel Selector Block Entity registry
     */
    public static final RegistryObject<BlockEntityType<JewelSelectorTableTileEntity>> JEWEL_SELECTOR_TILE_ENTITY =
        REGISTRY.register("jewel_sack_opener_tile_entity",
            () -> BlockEntityType.Builder.of(JewelSelectorTableTileEntity::new,
                MoreVaultTablesReferences.JEWEL_SELECTOR_TABLE_BLOCK).
                build(null));

    /**
     * The Card Selector Block Entity registry
     */
    public static final RegistryObject<BlockEntityType<CardSelectorTableTileEntity>> CARD_SELECTOR_TILE_ENTITY =
        REGISTRY.register("card_pack_opener_tile_entity",
            () -> BlockEntityType.Builder.of(CardSelectorTableTileEntity::new,
                    MoreVaultTablesReferences.CARD_SELECTOR_TABLE_BLOCK).
                build(null));

    /**
     * The Doll Dissection Block Entity registry
     */
    public static final RegistryObject<BlockEntityType<DollDismantlingTileEntity>> DOLL_DISMANTLING_TILE_ENTITY =
        REGISTRY.register("doll_dismantling_tile_entity",
            () -> BlockEntityType.Builder.of(DollDismantlingTileEntity::new,
                    MoreVaultTablesReferences.DOLL_DISMANTLING_BLOCK).
                build(null));
}
