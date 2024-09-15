//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.registries;


import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.CardSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


/**
 * The More vault tables containers registry.
 */
public class MoreVaultTablesContainersRegistry
{
    /**
     * The Menu registry
     */
    public static final DeferredRegister<MenuType<?>> REGISTRY =
        DeferredRegister.create(ForgeRegistries.CONTAINERS, MoreVaultTablesMod.MODID);


    /**
     * The Jewel Selector Table Container registry
     */
    public static final RegistryObject<MenuType<JewelSelectorTableContainer>> JEWEL_SELECTOR_TABLE_CONTAINER =
        REGISTRY.register("jewel_selector_table_container",
            () -> IForgeMenuType.create((windowId, inventory, buffer) ->
                new JewelSelectorTableContainer(windowId,
                    inventory.player.getLevel(),
                    buffer.readBlockPos(),
                    inventory)));

    /**
     * The Card Selector Table Container registry
     */
    public static final RegistryObject<MenuType<CardSelectorTableContainer>> CARD_SELECTOR_TABLE_CONTAINER =
        REGISTRY.register("card_selector_table_container",
            () -> IForgeMenuType.create((windowId, inventory, buffer) ->
                new CardSelectorTableContainer(windowId,
                    inventory.player.getLevel(),
                    buffer.readBlockPos(),
                    inventory)));
}
