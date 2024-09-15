package lv.id.bonne.vaulthunters.morevaulttables.block.entity;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import iskallia.vault.block.entity.base.FilteredInputInventoryTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.IntegrationRefinedStorage;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;


/**
 * The Jewel selector table allows to unpack Jewel Packs in single workstation without
 * using right-click menu.
 */
public class JewelSelectorTableTileEntity extends BlockEntity implements MenuProvider, FilteredInputInventoryTileEntity
{

    /**
     * Instantiates a new Jewel selector table.
     *
     * @param pos the pos
     * @param state the state
     */
    public JewelSelectorTableTileEntity(BlockPos pos, BlockState state)
    {
        super(MoreVaultTablesReferences.JEWEL_SELECTOR_TABLE_TILE_ENTITY, pos, state);
    }


    /**
     * Gets selected pouch.
     *
     * @return the selected pouch
     */
    public ItemStack getSelectedPouch()
    {
        return this.inputInventory.getItem(0);
    }


    /**
     * This method updates selected pouch item.
     * @param itemStack The selected pouch item.
     */
    public void updateSelectedPouch(ItemStack itemStack)
    {
        this.inputInventory.setItem(0, itemStack);
    }


    /**
     * Gets total size in jewels.
     *
     * @return the total size in jewels
     */
    public int getTotalSizeInJewels()
    {
        return this.totalSizeInJewels;
    }


    /**
     * This method returns number of pouches in input inventory.
     * @return Number of pouches in input inventory.
     */
    public int getTotalSizeInPouches()
    {
        return this.totalSizeInPouches;
    }


    /**
     * Still valid boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public boolean stillValid(Player player)
    {
        return this.level != null &&
            this.level.getBlockEntity(this.worldPosition) == this &&
            this.inputInventory.stillValid(player);
    }


    /**
     * Gets input inventory.
     *
     * @return the input inventory
     */
    public OverSizedInventory getInputInventory()
    {
        return this.inputInventory;
    }


    /**
     * Gets output inventory.
     *
     * @return the output inventory
     */
    public OverSizedInventory getOutputInventory()
    {
        return this.outputInventory;
    }


    /**
     * Gets jewel item.
     *
     * @param i the
     * @return the jewel item
     */
    public ItemStack getJewelItem(int i)
    {
        i = Mth.clamp(i, 0, 60);
        return this.outputInventory.getItem(i);
    }


    /**
     * Gets pouch item.
     * +1 is because first slot is for `unpacked` pouch.
     * @param i the
     * @return the pouch item
     */
    public ItemStack getPouchItem(int i)
    {
        i = Mth.clamp(i, 0, 60);
        return this.inputInventory.getItem(i + 1);
    }


    /**
     * Gets jewels.
     *
     * @return the jewels
     */
    public List<ItemStack> getJewels()
    {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < 60; ++i)
        {
            stacks.add(this.outputInventory.getItem(i));
        }

        return stacks;
    }


    /**
     * Gets pouches.
     *
     * @return the pouches
     */
    public List<ItemStack> getPouches()
    {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < 60; ++i)
        {
            stacks.add(this.inputInventory.getItem(i + 1));
        }

        return stacks;
    }


    /**
     * This method loads table content from NBT data.
     * @param tag The NBT data that contains table content.
     */
    public void load(@NotNull CompoundTag tag)
    {
        super.load(tag);

        if (tag.contains("jewels"))
        {
            CompoundTag jewels = tag.getCompound("jewels");
            this.inputInventory.load(jewels);
            this.totalSizeInJewels = jewels.getInt("size");
        }

        if (tag.contains("pouches"))
        {
            CompoundTag pouches = tag.getCompound("pouches");
            this.outputInventory.load(pouches);
            this.totalSizeInPouches = pouches.getInt("size");
        }
    }


    /**
       * This method saves table content into NBT data.
       * @param tag The NBT data where table content need to be saved.
      */
    protected void saveAdditional(@NotNull CompoundTag tag)
    {
        super.saveAdditional(tag);

        CompoundTag jewels = new CompoundTag();
        jewels.putInt("size", this.totalSizeInJewels);
        this.inputInventory.save(jewels);

        CompoundTag pouches = new CompoundTag();
        pouches.putInt("size", this.totalSizeInPouches);
        this.outputInventory.save(pouches);

        tag.put("jewels", jewels);
        tag.put("pouches", pouches);
    }


    /**
     * This method updates NBT tag.
     * @return Method that updates NBT tag.
     */
    @NotNull
    public CompoundTag getUpdateTag()
    {
        return this.saveWithoutMetadata();
    }


    /**
     * This method updates table content to client.
     * @return Packet that is sent to client
     */
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    /**
     * This method returns the display name of the table.
     * @return The display name for table./
     */
    @NotNull
    public Component getDisplayName()
    {
        return this.getBlockState().getBlock().getName();
    }


    /**
     * This method creates menu when player clicks on block.
     * @param containerId Container that is clicked.
     * @param inv The table inventory.
     * @param player The player object.
     * @return Container Menu that is opened.
     */
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv, @NotNull Player player)
    {
        return this.getLevel() == null ? null :
            new JewelSelectorTableContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
    }


    /**
     * This method dictates if items can be inserted into this container.
     * @param slot Slot that is targeted.
     * @param itemStack Inserted item stack
     * @return {@code true} if item can be inserted, {@code false} otherwise
     */
    @Override
    public boolean canInsertItem(int slot, @NotNull ItemStack itemStack)
    {
        // The item will should not be placed in first slot.
        return itemStack.is(ModItems.JEWEL_POUCH);
    }


    /**
     * Indicates from which directions items can be inserted.
     * @param direction Insertion direction.
     * @return {@code true} if item can be inserted from that direction, {@code false} otherwise
     */
    @Override
    public boolean isInventorySideAccessible(@Nullable Direction direction)
    {
        // Allow all here.
        return true;
    }


    /**
     * This method returns input filter compatibility.
     * @param inventory The targeted inventory.
     * @param side The input side.
     * @return LazyOptional with capability inside it.
     * @param <T> Capability type.
     */
    @Override
    public <T> LazyOptional<T> getFilteredInputCapability(@NotNull Container inventory, @Nullable Direction side)
    {
        return !this.isInventorySideAccessible(side) ? LazyOptional.empty() :
            LazyOptional.of(() -> new FilteredInvWrapper(this, inventory)
            {
                @NotNull
                @Override
                public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
                {
                    if (!canInsertItem(slot, stack))
                    {
                        // Cannot insert.
                        return stack;
                    }
                    else if (slot == 0)
                    {
                        // Custom slot 0 processor
                        if (this.getStackInSlot(0).isEmpty())
                        {
                            if (stack.getCount() > 1)
                            {
                                stack = stack.copy();

                                if (!simulate)
                                {
                                    this.getInv().setItem(slot, stack.split(1));
                                    this.getInv().setChanged();
                                }
                                else
                                {
                                    stack.shrink(Math.min(stack.getCount(), 1));
                                }

                                return stack;
                            }
                            else
                            {
                                if (!simulate)
                                {
                                    ItemStack copy = stack.copy();
                                    this.getInv().setItem(slot, copy);
                                    this.getInv().setChanged();
                                }

                                return ItemStack.EMPTY;
                            }
                        }
                        else
                        {
                            // Cannot insert item into non-empty slot
                            return stack;
                        }
                    }
                    else
                    {
                        // Default processing.
                        return super.insertItem(slot, stack, simulate);
                    }
                }
            }).cast();
    }


    /**
     * This method returns output filter compatibility.
     * @param inventory The targeted inventory.
     * @param side The output side.
     * @return LazyOptional with capability inside it.
     * @param <T> Capability type.
     */
    public <T> LazyOptional<T> getFilteredOutputCapability(@NotNull Container inventory, @Nullable Direction side)
    {
        // Only down direction currently
        return side != Direction.DOWN ? LazyOptional.empty() :
            LazyOptional.of(() -> new FilteredInvWrapper(this, inventory)
            {
                /**
                 * This method use default extraction method.
                 * @param slot     Slot to extract from.
                 * @param amount   Amount to extract (may be greater than the current stack's max limit)
                 * @param simulate If true, the extraction is only simulated
                 * @return Extracted ItemStack.
                 */
                @NotNull
                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate)
                {
                    if (amount == 0)
                    {
                        return ItemStack.EMPTY;
                    }

                    ItemStack stackInSlot = inventory.getItem(slot);

                    if (stackInSlot.isEmpty())
                    {
                        return ItemStack.EMPTY;
                    }

                    if (simulate)
                    {
                        if (stackInSlot.getCount() < amount)
                        {
                            return stackInSlot.copy();
                        }
                        else
                        {
                            ItemStack copy = stackInSlot.copy();
                            copy.setCount(amount);
                            return copy;
                        }
                    }
                    else
                    {
                        int m = Math.min(stackInSlot.getCount(), amount);

                        ItemStack decrStackSize = inventory.removeItem(slot, m);
                        inventory.setChanged();
                        return decrStackSize;
                    }
                }
            }).cast();
    }


    /**
     * This method returns requested capability for given side direction.
     * @param cap The capability to check
     * @param side The Side to check from,
     *   <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
     * @return LazyOptional with capability.
     * @param <T> Capability Type.
     */
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (IntegrationRefinedStorage.shouldPreventImportingCapability(this.getLevel(), this.getBlockPos(), side))
        {
            return super.getCapability(cap, side);
        }
        else if (Direction.DOWN == side)
        {
            return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
                this.getFilteredOutputCapability(this.outputInventory, side) :
                super.getCapability(cap, side);
        }
        else
        {
            return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
                this.getFilteredInputCapability(this.inputInventory, side) :
                super.getCapability(cap, side);
        }
    }


    /**
     * This variable stores how many jewels are in the system currently.
     */
    private int totalSizeInJewels = 0;

    /**
     * This variable stores output inventory that contains all jewel items  that are identified.
     */
    private final OverSizedInventory outputInventory = new OverSizedInventory(60, this)
    {
        /**
         * This method updates how many jewels are in the system, to not allow overflows.
         */
        public void setChanged()
        {
            super.setChanged();
            JewelSelectorTableTileEntity.this.totalSizeInJewels = this.countItem(ModItems.JEWEL);
        }
    };

    /**
     * This variable stores how many pouches are still in system.
     */
    private int totalSizeInPouches = 0;

    /**
     * This variable stores pouches inventory.
     */
    private final OverSizedInventory inputInventory = new OverSizedInventory(61, this)
    {
        /**
         * This method updates how many jewel pouches are in the system
         */
        public void setChanged()
        {
            super.setChanged();

            JewelSelectorTableTileEntity.this.totalSizeInPouches = 0;

            for (int i = 1; i < 61; i++)
            {
                if (this.getItem(i).is(ModItems.JEWEL_POUCH))
                {
                    JewelSelectorTableTileEntity.this.totalSizeInPouches++;
                }
            }
        }
    };
}
