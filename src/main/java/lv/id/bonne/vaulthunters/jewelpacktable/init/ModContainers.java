//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.jewelpacktable.init;


import lv.id.bonne.vaulthunters.jewelpacktable.block.menu.VaultJewelApplicationStationContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;


public class ModContainers {

    public static MenuType<VaultJewelApplicationStationContainer> VAULT_JEWEL_APPLICATION_STATION_CONTAINER;


    public ModContainers() {
    }

    public static void register(RegistryEvent.Register<MenuType<?>> event) {
        VAULT_JEWEL_APPLICATION_STATION_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
            Level world = inventory.player.getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            return new VaultJewelApplicationStationContainer(windowId, world, pos, inventory);
        });

        event.getRegistry().register(VAULT_JEWEL_APPLICATION_STATION_CONTAINER.setRegistryName("vault_jewel_application_station_container"));
    }
}

