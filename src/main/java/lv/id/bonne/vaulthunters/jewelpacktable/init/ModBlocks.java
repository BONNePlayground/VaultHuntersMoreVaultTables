//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.jewelpacktable.init;


import java.util.function.Consumer;

import iskallia.vault.init.ModItems;
import lv.id.bonne.vaulthunters.jewelpacktable.JewelPackTableMod;
import lv.id.bonne.vaulthunters.jewelpacktable.block.JewelSelectorTableBlock;
import lv.id.bonne.vaulthunters.jewelpacktable.block.entity.JewelSelectorTable;
import lv.id.bonne.vaulthunters.jewelpacktable.block.renderer.JewelApplicationStationRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import com.mojang.datafixers.types.Type;

public class ModBlocks {
    public static final JewelSelectorTableBlock VAULT_JEWEL_APPLICATION_STATION;

    public static final BlockEntityType<JewelSelectorTable> JEWEL_SELECTOR_TABLE;

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        registerBlock(event, VAULT_JEWEL_APPLICATION_STATION, JewelPackTableMod.of("vault_jewel_application_station"));
    }

    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        registerTileEntity(event, JEWEL_SELECTOR_TABLE, JewelPackTableMod.of("vault_jewel_application_station_tile_entity"));
    }

    public static void registerTileEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(JEWEL_SELECTOR_TABLE, JewelApplicationStationRenderer::new);
    }

    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        registerBlockItem(event, VAULT_JEWEL_APPLICATION_STATION);
    }

    private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ResourceLocation id) {
        block.setRegistryName(id);
        event.getRegistry().register(block);
    }

    private static <T extends BlockEntity> void registerTileEntity(RegistryEvent.Register<BlockEntityType<?>> event, BlockEntityType<?> type, ResourceLocation id) {
        type.setRegistryName(id);
        event.getRegistry().register(type);
    }

    private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block) {
        registerBlockItem(event, block, 64);
    }

    private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, int maxStackSize) {
        registerBlockItem(event, block, maxStackSize, (properties) -> {
        });
    }

    private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, int maxStackSize, Consumer<Item.Properties> adjustProperties) {
        Item.Properties properties = (new Item.Properties()).tab(ModItems.VAULT_MOD_GROUP).stacksTo(maxStackSize);
        adjustProperties.accept(properties);
        registerBlockItem(event, block, new BlockItem(block, properties));
    }

    private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, BlockItem blockItem) {
        blockItem.setRegistryName(block.getRegistryName());
        event.getRegistry().register(blockItem);
    }

    private static void registerTallBlockItem(RegistryEvent.Register<Item> event, Block block) {
        DoubleHighBlockItem tallBlockItem = new DoubleHighBlockItem(block, (new Item.Properties()).tab(ModItems.VAULT_MOD_GROUP).stacksTo(64));
        tallBlockItem.setRegistryName(block.getRegistryName());
        event.getRegistry().register(tallBlockItem);
    }

    static {
        VAULT_JEWEL_APPLICATION_STATION = new JewelSelectorTableBlock();
        JEWEL_SELECTOR_TABLE = Builder.of(JewelSelectorTable::new,
            new Block[]{VAULT_JEWEL_APPLICATION_STATION}).
            build((Type) null);
    }
}
