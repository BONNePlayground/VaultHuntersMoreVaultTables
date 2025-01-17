//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.init;


import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.DollDismantlingTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.renderer.CardSelectorTableRenderer;
import lv.id.bonne.vaulthunters.morevaulttables.block.renderer.DollDismantlingRenderer;
import lv.id.bonne.vaulthunters.morevaulttables.block.renderer.JewelSelectorTableRenderer;
import lv.id.bonne.vaulthunters.morevaulttables.block.screen.CardSelectorTableScreen;
import lv.id.bonne.vaulthunters.morevaulttables.block.screen.JewelSelectorTableScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = MoreVaultTablesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MoreVaultTablesClientEvents
{
    @SubscribeEvent
    public static void setupRenderLayers(FMLClientSetupEvent event)
    {
        ItemBlockRenderTypes.setRenderLayer(MoreVaultTablesReferences.JEWEL_SELECTOR_TABLE_BLOCK, RenderType.cutout());
        BlockEntityRenderers.register(MoreVaultTablesReferences.JEWEL_SELECTOR_TABLE_TILE_ENTITY, JewelSelectorTableRenderer::new);
        MenuScreens.register(MoreVaultTablesReferences.JEWEL_SELECTOR_TABLE_CONTAINER, JewelSelectorTableScreen::new);

        ItemBlockRenderTypes.setRenderLayer(MoreVaultTablesReferences.CARD_SELECTOR_TABLE_BLOCK, RenderType.cutout());
        BlockEntityRenderers.register(MoreVaultTablesReferences.CARD_SELECTOR_TABLE_TILE_ENTITY, CardSelectorTableRenderer::new);
        MenuScreens.register(MoreVaultTablesReferences.CARD_SELECTOR_TABLE_CONTAINER, CardSelectorTableScreen::new);

        ItemBlockRenderTypes.setRenderLayer(MoreVaultTablesReferences.DOLL_DISMANTLING_BLOCK, RenderType.cutout());
        BlockEntityRenderers.register(MoreVaultTablesReferences.DOLL_DISMANTLING_TILE_ENTITY, DollDismantlingRenderer::new);
    }
}
