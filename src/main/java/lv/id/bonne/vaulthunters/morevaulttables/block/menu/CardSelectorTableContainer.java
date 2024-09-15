package lv.id.bonne.vaulthunters.morevaulttables.block.menu;


import org.jetbrains.annotations.NotNull;
import java.util.List;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.BoosterPackItem;
import iskallia.vault.item.CardItem;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.CardSelectorTableTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.screen.CardSelectorTableScreen;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


/**
 * The card selector table container class.
 */
public class CardSelectorTableContainer extends OverSizedSlotContainer implements ICraftingSpotManager
{
    /**
     * Instantiates a new card selector table container.
     *
     * @param windowId the window id
     * @param world the world
     * @param pos the pos
     * @param playerInventory the player inventory
     */
    public CardSelectorTableContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory)
    {
        super(MoreVaultTablesReferences.CARD_SELECTOR_TABLE_CONTAINER, windowId, playerInventory.player);
        this.tilePos = pos;
        BlockEntity tile = world.getBlockEntity(this.tilePos);

        if (tile instanceof CardSelectorTableTileEntity craftingStationTileEntity)
        {
            this.tileEntity = craftingStationTileEntity;
            this.initSlots(playerInventory);
        }
        else
        {
            this.tileEntity = null;
        }
    }


    /**
     * This method init slots when player opens inventory.
     * @param playerInventory The player inventory list.
     */
    private void initSlots(Inventory playerInventory)
    {
        int hotbarSlot;
        int i;
        for (hotbarSlot = 0; hotbarSlot < 3; ++hotbarSlot)
        {
            for (i = 0; i < 9; ++i)
            {
                this.addSlot(new TabSlot(playerInventory, i + hotbarSlot * 9 + 9, 58 + i * 18, 108 + hotbarSlot * 18));
            }
        }

        for (hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot)
        {
            this.addSlot(new TabSlot(playerInventory, hotbarSlot, 58 + hotbarSlot * 18, 166));
        }

        Container invContainer = this.tileEntity.getInputInventory();

        // add one more empty slot
        this.addSlot(new TabSlot(invContainer, 0, -999, 50)
        {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack)
            {
                // Input inventory allows only card packs.
                return stack.getItem() instanceof BoosterPackItem;
            }


            @Override
            public void setChanged()
            {
                super.setChanged();

                if (CardSelectorTableContainer.this.player instanceof LocalPlayer)
                {
                    if (Minecraft.getInstance().screen instanceof CardSelectorTableScreen screen)
                    {
                        screen.setRegenerate();
                    }
                }
                else if (CardSelectorTableContainer.this.player instanceof ServerPlayer serverPlayer)
                {
                    CardSelectorTableContainer.this.identifySelectedItem(serverPlayer);
                }
            }
        });

        for (i = 0; i < 20; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                // Init slots somewhere far so they are not displayed.
                this.addSlot(new OverSizedTabSlot(invContainer, i * 3 + j + 1, -999 + j * 18, 50 + (i + 1) * 18)
                {
                    public boolean mayPlace(@NotNull ItemStack stack)
                    {
                        // Input inventory allows only card pouches.
                        return stack.getItem() instanceof BoosterPackItem;
                    }
                });
            }
        }

        Container outContainer = this.tileEntity.getOutputInventory();

        for (i = 0; i < 20; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                // Init slots somewhere far so they are not displayed.
                this.addSlot(new TabSlot(outContainer, i * 3 + j, 999 + j * 18, 50 + i * 18)
                {
                    public boolean mayPlace(@NotNull ItemStack stack)
                    {
                        // Output inventory allows only card items.
                        return stack.getItem() instanceof CardItem;
                    }
                });
            }
        }
    }


    /**
     * This method quick moves stacks between inventories.
     * @param player The player that performs quick move.
     * @param index The clicked index.
     * @return The moved item stack.
     */
    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            // Player Inventory and hotbar
            if (index >= 0 &&
                index < 36 &&
                this.moveOverSizedItemStackTo(slotStack, slot, 36, this.slots.size(), false))
            {
                return itemstack;
            }

            if (index >= 0 && index < 27)
            {
                // Player inventory
                if (!this.moveOverSizedItemStackTo(slotStack, slot, 27, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 27 && index < 36)
            {
                // Player hotbar
                if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 27, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 36, false))
            {
                // Player inventory
                return ItemStack.EMPTY;
            }

            if (slotStack.getCount() == 0)
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return itemstack;
    }


    /**
     * Gets tile pos.
     *
     * @return the tile pos
     */
    public BlockPos getTilePos()
    {
        return this.tilePos;
    }


    /**
     * Gets tile entity.
     *
     * @return the tile entity
     */
    public CardSelectorTableTileEntity getTileEntity()
    {
        return this.tileEntity;
    }


    /**
     * This method returns if current container is still valid.
     * @param player Player who opens menu.
     * @return {@code true} if container is valid, {@code false} otherwise.
     */
    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return this.tileEntity != null && this.tileEntity.stillValid(this.player);
    }


    /**
     * This method identifies a pouch item in selected slot.
     * @param serverPlayer player who performs identification.
     */
    @Override
    public void identifySelectedItem(ServerPlayer serverPlayer)
    {
        ItemStack selectedBoosterPack = this.getTileEntity().getSelectedPack();

        if (selectedBoosterPack.isEmpty() || BoosterPackItem.getOutcomes(selectedBoosterPack) != null)
        {
            // Not a pack or pack is identified already.
            return;
        }

        if (BoosterPackItem.getOutcomes(selectedBoosterPack) == null)
        {
            RandomSource random = JavaRandom.ofNanoTime();
            String id = BoosterPackItem.getId(selectedBoosterPack);
            BoosterPackItem.setOutcomes(selectedBoosterPack,
                ModConfigs.BOOSTER_PACK.getOutcomes(id, random).stream().
                    map(CardItem::create).
                    peek(item ->
                        item.inventoryTick(serverPlayer.level, serverPlayer, 0, false)).toList());
        }

        // Trigger update on menu.
        this.getTileEntity().updateSelectedPack(selectedBoosterPack);
    }


    /**
     * This method craft card item in given slot and moves into crafting slot new pack.
     * @param slotIndex Slot index.
     * @param serverPlayer Server player.
     * @return {@code true} there should broadcast changes, {@code false} otherwise.
     */
    @Override
    public boolean craftAndMoveItem(int slotIndex, ServerPlayer serverPlayer)
    {
        ItemStack selectedBoosterPack = this.getTileEntity().getSelectedPack();

        if (selectedBoosterPack.isEmpty() || BoosterPackItem.getOutcomes(selectedBoosterPack) == null)
        {
            // Do not have card in pouch.
            return false;
        }

        List<ItemStack> cards = BoosterPackItem.getOutcomes(selectedBoosterPack);

        if (cards.size() == 2)
        {
            // I add 3 empty spots before it.
            slotIndex -= 3;
        }

        if (cards.size() < slotIndex)
        {
            // Clicked outside card.
            return false;
        }

        if (this.getTileEntity().getTotalSizeInCards() < 60)
        {
            boolean moved = false;

            OverSizedInventory outputInventory = this.getTileEntity().getOutputInventory();

            for (int i = 0; i < outputInventory.getContainerSize() && !moved; i++)
            {
                ItemStack item = outputInventory.getItem(i);

                if (item.isEmpty())
                {
                    outputInventory.setItem(i, cards.get(slotIndex));
                    moved = true;
                }
            }

            if (moved)
            {
                // Remove item.
                OverSizedInventory inputInventory = this.getTileEntity().getInputInventory();
                inputInventory.setItem(0, ItemStack.EMPTY);

                this.moveItem();
            }
            else
            {
                // Log error message about no slots.
            }

            return true;
        }

        return false;
    }


    /**
     * This method moves item from pouches list into identification slot.
     */
    @Override
    public void moveItem()
    {
        // Remove item.
        OverSizedInventory inputInventory = this.getTileEntity().getInputInventory();

        if (!inputInventory.getItem(0).isEmpty())
        {
            return;
        }

        if (this.getTileEntity().getTotalSizeInPacks() <= 0)
        {
            return;
        }

        // Get next item
        for (int i = 1; i < inputInventory.getContainerSize(); i++)
        {
            ItemStack item = inputInventory.getItem(i);

            if (!item.isEmpty())
            {
                if (item.getCount() > 1)
                {
                    // Reduce count by 1
                    ItemStack copy = item.copy();
                    copy.setCount(copy.getCount() - 1);
                    inputInventory.setItem(i, copy);
                }
                else
                {
                    // Set item stack to air
                    inputInventory.setItem(i, ItemStack.EMPTY);
                }

                // set count to 1
                item.setCount(1);

                // set item in first slot.
                inputInventory.setItem(0, item);
                break;
            }
        }
    }


    /**
     * The tile entity that is selected.
     */
    private final CardSelectorTableTileEntity tileEntity;

    /**
     * The title pos for container.
     */
    private final BlockPos tilePos;
}
