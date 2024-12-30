package lv.id.bonne.vaulthunters.morevaulttables.block.entity;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import iskallia.vault.block.entity.base.FilteredInputInventoryTileEntity;
import iskallia.vault.container.oversized.OverSizedInvWrapper;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.IntegrationRefinedStorage;
import iskallia.vault.item.BoosterPackItem;
import iskallia.vault.item.CardItem;
import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.CardSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;


/**
 * The Card selector table allows to unpack Card Packs in single workstation without using right-click menu.
 */
public class CardSelectorTableTileEntity extends BlockEntity implements MenuProvider, FilteredInputInventoryTileEntity
{
    /**
     * Instantiates a new Card selector table.
     *
     * @param pos the pos
     * @param state the state
     */
    public CardSelectorTableTileEntity(BlockPos pos, BlockState state)
    {
        super(MoreVaultTablesReferences.CARD_SELECTOR_TABLE_TILE_ENTITY, pos, state);

        // Energy related stuff.
        int energyStorage = MoreVaultTablesMod.CONFIGURATION.getCardOpenerEnergyStorage();
        int energyTransfer = MoreVaultTablesMod.CONFIGURATION.getCardOpenerEnergyTransfer();

        this.energyConsumption = MoreVaultTablesMod.CONFIGURATION.getCardOpenerEnergyConsumption();
        this.energyStorage = new EnergyStorage(energyStorage, energyTransfer, energyTransfer);

        this.energyCapability = LazyOptional.of(() -> this.energyStorage);
    }


    /**
     * Gets selected packs.
     *
     * @return the selected packs
     */
    public ItemStack getSelectedPack()
    {
        return this.inputInventory.getItem(0);
    }


    /**
     * This method updates selected pack item.
     *
     * @param itemStack The selected pack item.
     */
    public void updateSelectedPack(ItemStack itemStack)
    {
        this.inputInventory.setItem(0, itemStack);
    }


    /**
     * Gets total size in cards.
     *
     * @return the total size in cards
     */
    public int getTotalSizeInCards()
    {
        return this.totalSizeInCards;
    }


    /**
     * This method returns number of packs in input inventory.
     *
     * @return Number of packs in input inventory.
     */
    public int getTotalSizeInPacks()
    {
        return this.totalSizeInPacks;
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
     * Gets card item.
     *
     * @param i the
     * @return the card item
     */
    public ItemStack getCardItem(int i)
    {
        i = Mth.clamp(i, 0, 60);
        return this.outputInventory.getItem(i);
    }


    /**
     * Gets pack item. +1 is because first slot is for `unpacked` pack.
     *
     * @param i the
     * @return the pack item
     */
    public ItemStack getPackItem(int i)
    {
        i = Mth.clamp(i, 0, 60);
        return this.inputInventory.getItem(i + 1);
    }


    /**
     * Gets cards.
     *
     * @return the cards
     */
    public List<ItemStack> getCards()
    {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < 60; ++i)
        {
            stacks.add(this.outputInventory.getItem(i));
        }

        return stacks;
    }


    /**
     * Gets packs.
     *
     * @return the packs
     */
    public List<ItemStack> getPacks()
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
     *
     * @param tag The NBT data that contains table content.
     */
    @Override
    public void load(@NotNull CompoundTag tag)
    {
        super.load(tag);

        if (tag.contains("cards"))
        {
            CompoundTag cards = tag.getCompound("cards");
            this.inputInventory.load(cards);
            this.totalSizeInCards = cards.getInt("size");
        }

        if (tag.contains("packs"))
        {
            CompoundTag packs = tag.getCompound("packs");
            this.outputInventory.load(packs);
            this.totalSizeInPacks = packs.getInt("size");
        }

        // Load energy data from NBT
        this.energyStorage.receiveEnergy(tag.getInt(CardSelectorTableTileEntity.ENERGY), false);
        this.redstonePowered = tag.getBoolean(CardSelectorTableTileEntity.REDSTONE);
    }


    /**
     * This method saves table content into NBT data.
     *
     * @param tag The NBT data where table content need to be saved.
     */
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag)
    {
        super.saveAdditional(tag);

        CompoundTag cards = new CompoundTag();
        cards.putInt("size", this.totalSizeInCards);
        this.inputInventory.save(cards);

        CompoundTag packs = new CompoundTag();
        packs.putInt("size", this.totalSizeInPacks);
        this.outputInventory.save(packs);

        tag.put("cards", cards);
        tag.put("packs", packs);

        // Put energy storing in tile entity
        tag.putInt(CardSelectorTableTileEntity.ENERGY, this.energyStorage.getEnergyStored());
        tag.putBoolean(CardSelectorTableTileEntity.REDSTONE, this.redstonePowered);
    }


    /**
     * This method updates NBT tag.
     *
     * @return Method that updates NBT tag.
     */
    @NotNull
    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveWithoutMetadata();
    }


    /**
     * This method updates table content to client.
     *
     * @return Packet that is sent to client
     */
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    /**
     * This method returns the display name of the table.
     *
     * @return The display name for table./
     */
    @NotNull
    @Override
    public Component getDisplayName()
    {
        return this.getBlockState().getBlock().getName();
    }


    /**
     * This method creates menu when player clicks on block.
     *
     * @param containerId Container that is clicked.
     * @param inv The table inventory.
     * @param player The player object.
     * @return Container Menu that is opened.
     */
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv, @NotNull Player player)
    {
        return this.getLevel() == null ? null :
            new CardSelectorTableContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
    }


    /**
     * This method dictates if items can be inserted into this container.
     *
     * @param slot Slot that is targeted.
     * @param itemStack Inserted item stack
     * @return {@code true} if item can be inserted, {@code false} otherwise
     */
    public boolean canInsertItem(int slot, @NotNull ItemStack itemStack)
    {
        // The item will should not be placed in first slot.
        return itemStack.is(ModItems.BOOSTER_PACK);
    }


    /**
     * Indicates from which directions items can be inserted.
     *
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
     * This method returns output filter compatibility.
     *
     * @param <T> Capability type.
     * @param inventory The targeted inventory.
     * @param side The output side.
     * @return LazyOptional with capability inside it.
     */
    public <T> LazyOptional<T> getFilteredOutputCapability(@NotNull Container inventory, @Nullable Direction side)
    {
        // Only down direction currently
        return side != Direction.DOWN ? LazyOptional.empty() :
            LazyOptional.of(() -> new OverSizedInvWrapper(inventory)
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
     *
     * @param cap The capability to check
     * @param side The Side to check from,
     * <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
     * @param <T> Capability Type.
     * @return LazyOptional with capability.
     */
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityEnergy.ENERGY)
        {
            // Energy capability
            return this.energyCapability.cast();
        }
        else if (IntegrationRefinedStorage.shouldPreventImportingCapability(this.getLevel(), this.getBlockPos(), side))
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
                this.getFilteredInputCapability(side, new Container[]{this.inputInventory}) :
                super.getCapability(cap, side);
        }
    }


    /**
     * This method checks if energy levels are enough for tile entity to operate.
     *
     * @return {@code true} if it has enough energy, {@code false} otherwise
     */
    public boolean canOperate()
    {
        return this.redstonePowered &&
            !this.getSelectedPack().isEmpty() &&
            this.totalSizeInCards < this.getOutputInventory().getContainerSize() &&
            this.energyStorage.getEnergyStored() >= this.energyConsumption;
    }


    /**
     * This method consumes energy for operating
     */
    private void consumeEnergy()
    {
        this.energyStorage.extractEnergy(this.energyConsumption, false);
    }


    /**
     * This method manages entity ticking process.
     */
    public void tick()
    {
        if (this.getLevel() != null && !this.getLevel().isClientSide)
        {
            if (this.canOperate() && this.tickCounter++ > MoreVaultTablesMod.CONFIGURATION.getCardOpenerSpeed())
            {
                List<ItemStack> outcomes = BoosterPackItem.getOutcomes(this.getSelectedPack());

                if (outcomes == null)
                {
                    this.identifySelectedItem(null);
                    outcomes = BoosterPackItem.getOutcomes(this.getSelectedPack());
                }

                int selectedSlot = this.getLevel().random.nextInt(outcomes.size());

                if (outcomes.size() == 2)
                {
                    // 2 cards are offset by 3 slots in gui.
                    selectedSlot += 3;
                }

                this.craftAndMoveItem(selectedSlot, null);

                this.consumeEnergy();

                this.getLevel().sendBlockUpdated(this.getBlockPos(),
                    this.getBlockState(),
                    this.getBlockState(),
                    Block.UPDATE_CLIENTS);

                this.tickCounter = 0;
            }
        }
    }


    @Override
    public void setRemoved()
    {
        super.setRemoved();
        this.energyCapability.invalidate();
    }


    /**
     * Invalidates capabilities for tile entities
     */
    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        this.energyCapability.invalidate();
    }


    /**
     * Sets redstone powered.
     *
     * @param redstonePowered the redstone powered
     */
    public void setRedstonePowered(boolean redstonePowered)
    {
        boolean triggered = this.redstonePowered != redstonePowered;
        this.redstonePowered = redstonePowered;

        if (triggered)
        {
            this.getLevel().sendBlockUpdated(this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(),
                Block.UPDATE_CLIENTS);
        }
    }


    /**
     * Craft and move item boolean.
     *
     * @param slotIndex the slot index
     * @param serverPlayer the server player
     * @return the boolean
     */
    public boolean craftAndMoveItem(int slotIndex, ServerPlayer serverPlayer)
    {
        ItemStack selectedBoosterPack = this.getSelectedPack();

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

        if (this.getTotalSizeInCards() < 60)
        {
            boolean moved = false;

            OverSizedInventory outputInventory = this.getOutputInventory();

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
                OverSizedInventory inputInventory = this.getInputInventory();
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
     * Move item.
     */
    public void moveItem()
    {
        // Remove item.
        OverSizedInventory inputInventory = this.getInputInventory();

        if (!inputInventory.getItem(0).isEmpty())
        {
            return;
        }

        if (this.getTotalSizeInPacks() <= 0)
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
     * Identify selected item.
     *
     * @param serverPlayer the server player
     */
    public void identifySelectedItem(@Nullable ServerPlayer serverPlayer)
    {
        ItemStack selectedBoosterPack = this.getSelectedPack();

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
                    toList());
        }

        // Trigger update on menu.
        this.updateSelectedPack(selectedBoosterPack);
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * This variable stores energy storage for current object.
     */
    private final EnergyStorage energyStorage;

    /**
     * This optional stores energy capability object.
     */
    private final LazyOptional<EnergyStorage> energyCapability;

    /**
     * This variable stores how much energy is consumed by current tile entity.
     */
    private final int energyConsumption;

    /**
     * This variable stores if table is powered by redstone.
     */
    private boolean redstonePowered;

    /**
     * This variable stores ticks since last update.
     */
    private int tickCounter;

    /**
     * This variable stores how many Cards are in the system currently.
     */
    private int totalSizeInCards = 0;

    /**
     * This variable stores output inventory that contains all card items  that are identified.
     */
    private final OverSizedInventory outputInventory = new OverSizedInventory(60, this)
    {
        /**
         * This method updates how many cards are in the system, to not allow overflows.
         */
        @Override
        public void setChanged()
        {
            super.setChanged();
            CardSelectorTableTileEntity.this.totalSizeInCards = this.countItem(ModItems.CARD);
        }
    };

    /**
     * This variable stores how many packs are still in system.
     */
    private int totalSizeInPacks = 0;

    /**
     * This variable stores pack inventory.
     */
    private final OverSizedInventory inputInventory =
        new OverSizedInventory.FilteredInsert(61, this, this::canInsertItem)
        {
            /**
             * This method updates how many card pouches are in the system
             */
            @Override
            public void setChanged()
            {
                super.setChanged();

                CardSelectorTableTileEntity.this.totalSizeInPacks = 0;

                for (int i = 1; i < 61; i++)
                {
                    if (this.getItem(i).is(ModItems.BOOSTER_PACK))
                    {
                        CardSelectorTableTileEntity.this.totalSizeInPacks++;
                    }
                }
            }
        };

    /**
     * The parameter for data storage.
     */
    private static final String ENERGY = "energy";

    /**
     * The parameter for data storage.
     */
    private static final String REDSTONE = "redstone";
}
