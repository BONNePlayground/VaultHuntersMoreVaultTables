package lv.id.bonne.vaulthunters.morevaulttables.block.entity;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import iskallia.vault.entity.entity.DollMiniMeEntity;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.VaultDollItem;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.world.data.DollLootData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
import lv.id.bonne.vaulthunters.morevaulttables.registries.MoreVaultTablesSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;


/**
 * The Doll dissecting table allows to unpack vault dolls without crashing the server.
 */
public class DollDismantlingTileEntity extends BlockEntity
{
    /**
     * Instantiates a new Doll Dissecting table.
     *
     * @param pos the pos
     * @param state the state
     */
    public DollDismantlingTileEntity(BlockPos pos, BlockState state)
    {
        super(MoreVaultTablesReferences.DOLL_DISMANTLING_TILE_ENTITY, pos, state);
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
            this.level.getBlockEntity(this.worldPosition) == this;
    }


    /**
     * Gets selected Doll.
     *
     * @return the selected Doll
     */
    public ItemStack getDoll()
    {
        return this.inventory.getStackInSlot(0);
    }


    /**
     * This method updates selected Doll item.
     * @param itemStack The selected Doll item.
     */
    public void updateDoll(ItemStack itemStack, ServerPlayer player)
    {
        this.inventory.setStackInSlot(0, itemStack);
        this.extractionHandler = new ItemStackHandler(0);

        // Handle doll XP
        CompoundTag dollTag = itemStack.getOrCreateTag();

        if (!dollTag.contains("rewarded"))
        {
            // Assign number of items for juicer.
            VaultDollItem.getDollUUID(dollTag).ifPresent(dollUuid ->
            {
                PlayerVaultStatsData statsData = PlayerVaultStatsData.get(player.getLevel());
                statsData.addVaultExp(player, VaultDollItem.getExperience(dollTag));

                dollTag.putBoolean("rewarded", true);

                DollLootData dollLootData = DollLootData.get(player.getLevel(), dollUuid);
                List<ItemStack> loot = dollLootData.getLoot();
                InventoryUtil.makeItemsRotten(loot);
                dollLootData.setDirty();

                DollDismantlingTileEntity.this.totalItemsInDoll = loot.stream().mapToInt(ItemStack::getCount).sum();
                itemStack.getOrCreateTag().putLong("amount", DollDismantlingTileEntity.this.totalItemsInDoll);

                this.extractionHandler = new DollItemExtractor(dollLootData);
            });
        }
    }


    /**
     * This method returns if player can insert doll into table.
     * @param stack Doll itemStack
     * @param player Player entity.
     * @return {@code true} if player can insert doll, {@code false} otherwise.
     */
    public boolean playerCannotInsertDoll(ItemStack stack, Player player)
    {
        return !player.isCreative() &&
            VaultDollItem.getPlayerGameProfile(stack).
                flatMap(gp -> Optional.of(gp.getId())).
                map(uuid -> !uuid.equals(player.getUUID())).
                orElse(true);
    }


    /**
     * This method loads table content from NBT data.
     * @param tag The NBT data that contains table content.
     */
    @Override
    public void load(@NotNull CompoundTag tag)
    {
        super.load(tag);

        if (tag.contains("doll"))
        {
            CompoundTag doll = tag.getCompound("doll");
            this.totalItemsInDoll = doll.getInt("size");
            this.inventory.deserializeNBT(doll.getCompound("item"));

            if (doll.contains("mini"))
            {
                this.miniMeEntity = new DollMiniMeEntity(ModEntities.DOLL_MINI_ME, this.level);
                this.miniMeEntity.deserializeNBT(doll.getCompound("mini"));
            }
            else
            {
                this.miniMeEntity = null;
            }
        }
    }


    /**
       * This method saves table content into NBT data.
       * @param tag The NBT data where table content need to be saved.
      */
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag)
    {
        super.saveAdditional(tag);

        CompoundTag doll = new CompoundTag();
        doll.putInt("size", this.totalItemsInDoll);
        doll.put("item", this.inventory.serializeNBT());

        if (this.miniMeEntity != null)
        {
            doll.put("mini", this.miniMeEntity.serializeNBT());
        }

        tag.put("doll", doll);
    }


    /**
     * This method updates NBT tag.
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
     * @return Packet that is sent to client
     */
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }



    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        if (tag.contains("doll"))
        {
            CompoundTag doll = tag.getCompound("doll");
            this.totalItemsInDoll = doll.getInt("size");
            this.inventory.deserializeNBT(doll.getCompound("item"));

            if (doll.contains("mini"))
            {
                if (this.miniMeEntity == null)
                {
                    this.miniMeEntity = new DollMiniMeEntity(ModEntities.DOLL_MINI_ME, this.level);
                }

                this.miniMeEntity.deserializeNBT(doll.getCompound("mini"));
            }
            else
            {
                this.miniMeEntity = null;
            }
        }
    }


    public static void tick(Level level, BlockPos pos, BlockState blockState, DollDismantlingTileEntity tileEntity)
    {
        if (!level.isClientSide) {
            // If doll is present and not air, trigger the sound loop
            if (!tileEntity.getDoll().isEmpty()) {
                // Play sound every 2 seconds
                if (tileEntity.soundTickCooldown <= 0) {
                    tileEntity.playDollSound(level, pos);
                    tileEntity.soundTickCooldown = 40;
                } else {
                    tileEntity.soundTickCooldown--;
                }

                tileEntity.autoEjectItems(level, pos);
            } else {
                // Reset the cooldown when no doll is present
                tileEntity.soundTickCooldown = 0;
            }
        }
    }

    private int soundTickCooldown = 0;

    private void playDollSound(Level level, BlockPos pos)
    {
        level.playSound(null,
            pos,
            MoreVaultTablesSoundRegistry.BLENDER.get(),
            SoundSource.BLOCKS,
            0.3F,
            1.0F);
    }


    private void autoEjectItems(Level level, BlockPos pos) {
        BlockPos belowPos = pos.below(); // Get the position below this block

        // Get the BlockEntity below (if any)
        BlockEntity belowBlockEntity = level.getBlockEntity(belowPos);

        if (belowBlockEntity != null) {
            // Check if the block below has an IItemHandler capability (i.e., it can accept items)
            belowBlockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).
                ifPresent(belowHandler -> {
                // Try to transfer items from this block's inventory to the inventory below
                for (int i = 0; i < extractionHandler.getSlots(); i++) {
                    ItemStack stackInSlot = extractionHandler.getStackInSlot(i);

                    if (!stackInSlot.isEmpty()) {
                        // Attempt to move the stack to the below inventory
                        ItemStack remainingStack = transferStack(belowHandler, stackInSlot);

                        if (remainingStack.isEmpty())
                        {
                            ((DollItemExtractor) extractionHandler).dollLootData.getLoot().remove(i);
                            ((DollItemExtractor) extractionHandler).dollLootData.setDirty();
                            this.totalItemsInDoll -= stackInSlot.getCount();

                            this.triggerUpdate();

                            break;
                        }
                        else if (remainingStack.getCount() != stackInSlot.getCount())
                        {
                            ((DollItemExtractor) extractionHandler).dollLootData.getLoot().set(0, remainingStack);
                            ((DollItemExtractor) extractionHandler).dollLootData.setDirty();
                            this.totalItemsInDoll = this.totalItemsInDoll - stackInSlot.getCount() + remainingStack.getCount();

                            this.triggerUpdate();

                            break;
                        }
                    }
                }
                setChanged(); // Mark the block entity as changed to save its state
            });
        }
    }

    private static boolean isEmpty(IItemHandler itemHandler) {
        for(int slot = 0; slot < itemHandler.getSlots(); ++slot) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0) {
                return false;
            }
        }

        return true;
    }


    private static ItemStack insertStack(IItemHandler destInventory, ItemStack stack, int slot) {
        ItemStack itemstack = destInventory.getStackInSlot(slot);
        if (destInventory.insertItem(slot, stack, true).getCount() != 64) {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);

            if (itemstack.isEmpty()) {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            } else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack)) {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                boolean var10000 = originalSize < stack.getCount();
            }
        }

        return stack;
    }



    private ItemStack transferStack(IItemHandler targetHandler, ItemStack stack) {
        for (int i = 0; i < targetHandler.getSlots(); i++) {
            stack = insertStack(targetHandler, stack, i); // Try to insert the stack into the target inventory
            if (stack.isEmpty()) {
                break; // If the stack is fully inserted, break out of the loop
            }
        }
        return stack; // Return the remaining stack (if any)
    }


    @Override
    public void setRemoved()
    {
        super.setRemoved();
        LazyOptional.of(() -> this.extractionHandler).invalidate();
    }


    /**
     * This method returns requested capability for given side direction.
     * @param capability The capability to check
     * @param side The Side to check from,
     *   <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
     * @return LazyOptional with capability.
     * @param <T> Capability Type.
     */
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (side == Direction.DOWN)
            {
                return LazyOptional.of(() -> this.extractionHandler).cast();
            }
        }
        return super.getCapability(capability, side);
    }


    /**
     * Gets inventory.
     *
     * @return the inventory
     */
    public ItemStackHandler getInventory()
    {
        return this.inventory;
    }


    /**
     * This method returns number of items currently stored inside doll.
     * @return Number of items inside doll.
     */
    public int getTotalItemsInDoll()
    {
        return this.totalItemsInDoll;
    }


    /**
     * This method returns the entity that is inside block.
     * @return The mini me entity.
     */
    @Nullable
    public DollMiniMeEntity getMiniMeEntity()
    {
        return this.miniMeEntity;
    }


    /**
     * This method sets mini me entity for this tile.
     */
    public void updateMiniMe()
    {
        if (!this.getDoll().is(ModItems.VAULT_DOLL))
        {
            this.miniMeEntity = null;
        }
        else
        {
            this.miniMeEntity = new DollMiniMeEntity(ModEntities.DOLL_MINI_ME,
                DollDismantlingTileEntity.this.getLevel());

            CompoundTag dataTag = this.getDoll().getOrCreateTag();
            VaultDollItem.getPlayerGameProfile(dataTag).
                ifPresent(this.miniMeEntity::setGameProfile);
        }

        this.triggerUpdate();
    }


    public void triggerUpdate()
    {
        this.setChanged();

        if (this.level instanceof ServerLevel)
        {
            this.level.sendBlockUpdated(this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(),
                Block.UPDATE_CLIENTS);
        }
    }


    /**
     * This variable stores how many items are in the doll currently.
     */
    private int totalItemsInDoll = 0;

    /**
     * The entity that is displayed in the dismantler.
     */
    @Nullable
    private DollMiniMeEntity miniMeEntity;


    /**
     * This variable stores output inventory that contains all card items  that are identified.
     */
    private final ItemStackHandler inventory = new ItemStackHandler(1)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            DollDismantlingTileEntity.this.updateMiniMe();
        }
    };


    private IItemHandler extractionHandler;


    private class DollItemExtractor extends ItemStackHandler
    {
        DollItemExtractor(DollLootData dollLootData)
        {
            super(dollLootData.getLoot().size());
            this.dollLootData = dollLootData;
        }


        @Override
        public int getSlots()
        {
            return this.dollLootData.getLoot().size();
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot)
        {
            return this.dollLootData.getLoot().get(slot);
        }


        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (amount == 0)
            {
                return ItemStack.EMPTY;
            }

            if (!inventory.getStackInSlot(0).is(ModItems.VAULT_DOLL))
            {
                // Not a Vault Doll.
                return ItemStack.EMPTY;
            }

            if (!(level instanceof ServerLevel))
            {
                // Only server side
                return ItemStack.EMPTY;
            }

            if (this.dollLootData == null)
            {
                return ItemStack.EMPTY;
            }

            List<ItemStack> loot = this.dollLootData.getLoot();

            if (loot.size() <= slot)
            {
                // Outside range.
                return ItemStack.EMPTY;
            }

            ItemStack stackInSlot = loot.get(slot);

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
                if (stackInSlot.getCount() < amount)
                {
                    loot.remove(slot);
                    this.dollLootData.setDirty();

                    if (loot.isEmpty())
                    {
                        // Remove doll item itself.
                        inventory.setStackInSlot(0, ItemStack.EMPTY);
                    }

                    DollDismantlingTileEntity.this.totalItemsInDoll -= stackInSlot.getCount();
                    DollDismantlingTileEntity.this.triggerUpdate();
                    return stackInSlot;
                }
                else
                {
                    ItemStack copy = stackInSlot.copy();
                    copy.setCount(amount);

                    stackInSlot.shrink(amount);

                    if (stackInSlot.isEmpty())
                    {
                        loot.remove(slot);
                    }

                    this.dollLootData.setDirty();

                    if (loot.isEmpty())
                    {
                        // Remove doll item itself.
                        inventory.setStackInSlot(0, ItemStack.EMPTY);
                    }

                    DollDismantlingTileEntity.this.totalItemsInDoll -= amount;
                    DollDismantlingTileEntity.this.triggerUpdate();
                    return copy;
                }
            }
        }


        protected final DollLootData dollLootData;
    }
}
