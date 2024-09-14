//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.network.packets;


import java.util.function.Supplier;

import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;


public class VaultJewelApplicationStationMessage {
    public VaultJewelApplicationStationMessage() {
    }

    public static void encode(VaultJewelApplicationStationMessage message, FriendlyByteBuf buffer) {
    }

    public static VaultJewelApplicationStationMessage decode(FriendlyByteBuf buffer) {
        return new VaultJewelApplicationStationMessage();
    }

    public static void handle(VaultJewelApplicationStationMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = (NetworkEvent.Context)contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer requester = context.getSender();
            if (requester != null) {
                AbstractContainerMenu patt1027$temp = requester.containerMenu;
                if (patt1027$temp instanceof JewelSelectorTableContainer) {
                    JewelSelectorTableContainer container = (JewelSelectorTableContainer)patt1027$temp;
                    return;
                }
            }

        });
        context.setPacketHandled(true);
    }
}
