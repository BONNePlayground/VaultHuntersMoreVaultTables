package lv.id.bonne.vaulthunters.morevaulttables.block.entity;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

import iskallia.vault.block.entity.base.FilteredInputInventoryTileEntity;
import iskallia.vault.container.oversized.OverSizedInvWrapper;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.gear.GearScoreHelper;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.IntegrationRefinedStorage;
import iskallia.vault.item.JewelPouchItem;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import lv.id.bonne.vaulthunters.morevaulttables.mixin.JewelPouchItemInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
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
 * The Jewel selector table allows to unpack Jewel Packs in single workstation without using right-click menu.
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

        // Energy related stuff.
        int energyStorage = MoreVaultTablesMod.CONFIGURATION.getPouchOpenerEnergyStorage();
        int energyTransfer = MoreVaultTablesMod.CONFIGURATION.getPouchOpenerEnergyTransfer();

        this.energyConsumption = MoreVaultTablesMod.CONFIGURATION.getPouchOpenerEnergyConsumption();
        this.energyStorage = new EnergyStorage(energyStorage, energyTransfer, energyTransfer);

        this.energyCapability = LazyOptional.of(() -> this.energyStorage);
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
     *
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
     *
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
     * Gets pouch item. +1 is because first slot is for `unpacked` pouch.
     *
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
     *
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

        // Load energy data from NBT
        this.energyStorage.receiveEnergy(tag.getInt(JewelSelectorTableTileEntity.ENERGY), false);
        this.redstonePowered = tag.getBoolean(JewelSelectorTableTileEntity.REDSTONE);

        this.ownerUUID = tag.contains(JewelSelectorTableTileEntity.UUID) ?
            tag.getUUID(JewelSelectorTableTileEntity.UUID) : null;
    }


    /**
     * This method saves table content into NBT data.
     *
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

        // Put energy storing in tile entity
        tag.putInt(JewelSelectorTableTileEntity.ENERGY, this.energyStorage.getEnergyStored());
        tag.putBoolean(JewelSelectorTableTileEntity.REDSTONE, this.redstonePowered);

        if (this.ownerUUID != null)
        {
            tag.putUUID(JewelSelectorTableTileEntity.UUID, this.ownerUUID);
        }
    }


    /**
     * This method updates NBT tag.
     *
     * @return Method that updates NBT tag.
     */
    @NotNull
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
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv, @NotNull Player player)
    {
        return this.getLevel() == null ? null :
            new JewelSelectorTableContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
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
        return itemStack.is(ModItems.JEWEL_POUCH);
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
            !this.getSelectedPouch().isEmpty() &&
            this.totalSizeInJewels < this.getOutputInventory().getContainerSize() &&
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
            if (this.canOperate() && this.tickCounter++ > MoreVaultTablesMod.CONFIGURATION.getPouchOpenerSpeed())
            {
                List<JewelPouchItem.RolledJewel> jewels = JewelPouchItem.getJewels(this.getSelectedPouch());

                if (jewels.isEmpty())
                {
                    this.identifySelectedItem(null);
                    JewelPouchItem.getJewels(this.getSelectedPouch());
                }

                this.craftAndMoveItem(0, null);

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
        ItemStack selectedPouch = this.getSelectedPouch();

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

        if (this.getTotalSizeInJewels() < 59)
        {
            boolean moved = false;

            OverSizedInventory outputInventory = this.getOutputInventory();

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
                            UUID target = serverPlayer != null ? serverPlayer.getUUID() : this.ownerUUID;

                            int vaultLevel = JewelPouchItem.getStoredLevel(selectedPouch).
                                orElseGet(() ->
                                    target != null ?
                                        PlayerVaultStatsData.get((ServerLevel) this.getLevel()).
                                            getVaultStats(target).getVaultLevel() :
                                        0);

                            levelItem.initializeVaultLoot(vaultLevel, result, null, null);
                        }

                        result = DataTransferItem.doConvertStack(result);
                        DataInitializationItem.doInitialize(result);
                    }

                    outputInventory.setItem(i, result);
                    moved = jewels.size() == 1 || slotIndex++ == 1;
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

        if (this.getTotalSizeInPouches() <= 0)
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
        ItemStack selectedPouch = this.getSelectedPouch();

        if (selectedPouch.isEmpty() || !JewelPouchItem.getJewels(selectedPouch).isEmpty())
        {
            // Not a jewel pouch or pouch is identified already.
            return;
        }

        UUID target = serverPlayer != null ? serverPlayer.getUUID() : this.ownerUUID;

        int vaultLevel = JewelPouchItem.getStoredLevel(selectedPouch).orElseGet(() ->
            target != null ?
                PlayerVaultStatsData.get((ServerLevel) this.getLevel()).getVaultStats(target).getVaultLevel() :
                0);

        int additionalIdentifiedJewels = 0;
        ExpertiseTree expertises = target != null ?
            PlayerExpertisesData.get((ServerLevel) this.getLevel()).getExpertises(target) :
            new ExpertiseTree();

        JewelExpertise expertise;

        for (Iterator<?> var10 = expertises.getAll(JewelExpertise.class, Skill::isUnlocked).iterator();
            var10.hasNext(); additionalIdentifiedJewels += expertise.getAdditionalIdentifiedJewels())
        {
            expertise = (JewelExpertise) var10.next();
        }

        JewelPouchItemInvoker.invokeGenerateJewels(selectedPouch, vaultLevel, additionalIdentifiedJewels);

        // Trigger update on menu.
        this.updateSelectedPouch(selectedPouch);
    }


    /**
     * Sets owner.
     *
     * @param player the player
     */
    public void setOwner(Player player)
    {
        this.ownerUUID = player.getUUID();
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
     * The block owner uuid.
     */
    private UUID ownerUUID;

    /**
     * This variable stores if table is powered by redstone.
     */
    private boolean redstonePowered;

    /**
     * This variable stores ticks since last update.
     */
    private int tickCounter;

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
    private final OverSizedInventory inputInventory =
        new OverSizedInventory.FilteredInsert(61, this, this::canInsertItem)
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

    /**
     * The parameter for data storage.
     */
    private static final String ENERGY = "energy";

    /**
     * The parameter for data storage.
     */
    private static final String REDSTONE = "redstone";

    /**
     * The parameter for data storage.
     */
    private static final String UUID = "uuid";
}
