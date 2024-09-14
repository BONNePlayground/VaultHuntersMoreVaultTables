package lv.id.bonne.vaulthunters.morevaulttables.block.menu;


import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.item.JewelPouchItem;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import lv.id.bonne.vaulthunters.morevaulttables.block.entity.JewelSelectorTableTileEntity;
import lv.id.bonne.vaulthunters.morevaulttables.block.screen.JewelSelectorTableScreen;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import lv.id.bonne.vaulthunters.morevaulttables.mixin.JewelPouchItemInvoker;
import lv.id.bonne.vaulthunters.morevaulttables.network.MoreVaultTablesNetwork;
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
 * The Jewel selector table container class.
 */
public class JewelSelectorTableContainer extends OverSizedSlotContainer
{
    /**
     * Instantiates a new Jewel selector table container.
     *
     * @param windowId the window id
     * @param world the world
     * @param pos the pos
     * @param playerInventory the player inventory
     */
    public JewelSelectorTableContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory)
    {
        super(MoreVaultTablesReferences.JEWEL_SELECTOR_TABLE_CONTAINER, windowId, playerInventory.player);
        this.tilePos = pos;
        BlockEntity tile = world.getBlockEntity(this.tilePos);

        if (tile instanceof JewelSelectorTableTileEntity craftingStationTileEntity)
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
                // Input inventory allows only jewel pouches.
                return stack.getItem() instanceof JewelPouchItem;
            }


            @Override
            public void setChanged()
            {
                super.setChanged();

                if (JewelSelectorTableContainer.this.player instanceof LocalPlayer)
                {
                    if (Minecraft.getInstance().screen instanceof JewelSelectorTableScreen screen)
                    {
                        screen.setRegenerate();
                    }
                }
                else if (JewelSelectorTableContainer.this.player instanceof ServerPlayer serverPlayer)
                {
                    JewelSelectorTableContainer.this.identifyPouchItem(serverPlayer);
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
                        // Input inventory allows only jewel pouches.
                        return stack.getItem() instanceof JewelPouchItem;
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
                        // Output inventory allows only jewel items.
                        return stack.getItem() instanceof JewelItem;
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
    public JewelSelectorTableTileEntity getTileEntity()
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
    public void identifyPouchItem(ServerPlayer serverPlayer)
    {
        ItemStack selectedPouch = this.getTileEntity().getSelectedPouch();

        if (selectedPouch.isEmpty() || !selectedPouch.getOrCreateTag().isEmpty())
        {
            // Not a jewel pouch or pouch is identified already.
            return;
        }

        int vaultLevel = JewelPouchItem.getStoredLevel(selectedPouch).orElseGet(() ->
            PlayerVaultStatsData.get(serverPlayer.getLevel()).getVaultStats(serverPlayer).getVaultLevel());

        int additionalIdentifiedJewels = 0;
        ExpertiseTree expertises =
            PlayerExpertisesData.get(serverPlayer.getLevel()).getExpertises(serverPlayer);

        JewelExpertise expertise;
        for (Iterator<?> var10 = expertises.getAll(JewelExpertise.class, Skill::isUnlocked).iterator();
            var10.hasNext(); additionalIdentifiedJewels += expertise.getAdditionalIdentifiedJewels())
        {
            expertise = (JewelExpertise) var10.next();
        }

        JewelPouchItemInvoker.invokeGenerateJewels(selectedPouch, vaultLevel, additionalIdentifiedJewels);

        // Trigger update on menu.
        this.getTileEntity().updateSelectedPouch(selectedPouch);
    }


    /**
     * This method craft jewel item in given slot and moves into crafting slot new pouch.
     * @param slotIndex Slot index.
     * @param serverPlayer Server player.
     * @return {@code true} there should broadcast changes, {@code false} otherwise.
     */
    public boolean craftAndMoveItem(int slotIndex, ServerPlayer serverPlayer)
    {
        ItemStack selectedPouch = this.getTileEntity().getSelectedPouch();

        if (selectedPouch.isEmpty() || selectedPouch.getOrCreateTag().isEmpty())
        {
            // Do not have jewels in pouch.
            return false;
        }

        List<JewelPouchItem.RolledJewel> jewels = JewelPouchItem.getJewels(selectedPouch);

        if (jewels.size() < slotIndex)
        {
            // Clicked outside jewels.
            return false;
        }

        if (this.getTileEntity().getTotalSizeInJewels() < 60)
        {
            boolean moved = false;

            OverSizedInventory outputInventory = this.getTileEntity().getOutputInventory();

            for (int i = 0; i < outputInventory.getContainerSize() && !moved; i++)
            {
                ItemStack item = outputInventory.getItem(i);

                if (item.isEmpty())
                {
                    JewelPouchItem.RolledJewel rolledJewel = jewels.get(slotIndex);
                    ItemStack result = rolledJewel.stack().copy();

                    if (!rolledJewel.identified())
                    {
                        if (result.getItem() instanceof VaultLevelItem levelItem)
                        {
                            int vaultLevel = JewelPouchItem.getStoredLevel(selectedPouch).
                                orElseGet(() -> PlayerVaultStatsData.get(serverPlayer.getLevel()).
                                    getVaultStats(serverPlayer).getVaultLevel());

                            levelItem.initializeVaultLoot(vaultLevel, result, null, null);
                        }

                        result = DataTransferItem.doConvertStack(result);
                        DataInitializationItem.doInitialize(result);
                    }

                    outputInventory.setItem(i, result);
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
    public void moveItem()
    {
        // Remove item.
        OverSizedInventory inputInventory = this.getTileEntity().getInputInventory();

        if (!inputInventory.getItem(0).isEmpty())
        {
            return;
        }

        if (this.getTileEntity().getTotalSizeInPouches() <= 0)
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
    private final JewelSelectorTableTileEntity tileEntity;

    /**
     * The title pos for container.
     */
    private final BlockPos tilePos;
}
