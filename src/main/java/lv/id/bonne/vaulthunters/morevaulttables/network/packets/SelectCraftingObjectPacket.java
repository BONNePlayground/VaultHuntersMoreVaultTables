//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.network.packets;


import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.item.JewelPouchItem;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.mixin.JewelPouchItemInvoker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;


public class SelectCraftingObjectPacket
{
    public SelectCraftingObjectPacket(int clickedSlot)
    {
        this.slot = clickedSlot;
    }


    public static void encode(SelectCraftingObjectPacket message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.slot);
    }


    public static SelectCraftingObjectPacket decode(FriendlyByteBuf buffer)
    {
        return new SelectCraftingObjectPacket(buffer.readInt());
    }


    public static void handle(SelectCraftingObjectPacket message,
        Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() ->
        {
            ServerPlayer requester = context.getSender();

            if (requester != null)
            {
                AbstractContainerMenu menu = requester.containerMenu;

                if (menu instanceof JewelSelectorTableContainer container)
                {
                    if (container.craftAndMoveItem(message.slot, requester))
                    {
                        container.identifyPouchItem(requester);
                        container.broadcastChanges();
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }


    /**
     * The clicked jewel slot.
     */
    private final int slot;
}
