//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.block.menu;


import net.minecraft.server.level.ServerPlayer;


/**
 * This interface is used to easily use methods for packet handling for different tables.
 */
public interface ICraftingSpotManager
{
    /**
     * This method identifies an item in selected slot.
     *
     * @param serverPlayer player who performs identification.
     */
    void identifySelectedItem(ServerPlayer serverPlayer);


    /**
     * This method craft item in given slot and moves into crafting slot new item.
     *
     * @param slotIndex Slot index.
     * @param serverPlayer Server player.
     * @return {@code true} there should broadcast changes, {@code false} otherwise.
     */
    boolean craftAndMoveItem(int slotIndex, ServerPlayer serverPlayer);


    /**
     * This method moves item from item list into identification slot.
     */
    void moveItem();
}
