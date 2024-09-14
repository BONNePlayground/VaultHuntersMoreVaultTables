//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.jewelpacktable.init;


import lv.id.bonne.vaulthunters.jewelpacktable.block.menu.JewelSelectorTableContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;


public class ModContainers {

    public static MenuType<JewelSelectorTableContainer> JEWEL_SELECTOR_TABLE_CONTAINER;


    public ModContainers() {
    }

    public static void register(RegistryEvent.Register<MenuType<?>> event) {
        JEWEL_SELECTOR_TABLE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
            Level world = inventory.player.getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            return new JewelSelectorTableContainer(windowId, world, pos, inventory);
        });

        event.getRegistry().register(JEWEL_SELECTOR_TABLE_CONTAINER.setRegistryName("vault_jewel_application_station_container"));
    }
}

