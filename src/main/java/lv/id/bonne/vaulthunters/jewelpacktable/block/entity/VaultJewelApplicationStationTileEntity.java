package lv.id.bonne.vaulthunters.jewelpacktable.block.entity;


import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

import iskallia.vault.container.oversized.OverSizedInventory;
import javax.annotation.Nullable;
import lv.id.bonne.vaulthunters.jewelpacktable.block.menu.VaultJewelApplicationStationContainer;
import lv.id.bonne.vaulthunters.jewelpacktable.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class VaultJewelApplicationStationTileEntity extends BlockEntity implements MenuProvider {
    private int totalSizeInJewels = 0;
    private int totalSizeInPouches = 0;

    private final OverSizedInventory inputInventory = new OverSizedInventory(60, this) {
        public void setChanged() {
            super.setChanged();

            VaultJewelApplicationStationTileEntity.this.totalSizeInJewels = 0;

            for(int i = 0; i < 60; ++i) {
                // slot change ???
            }
        }
    };

    private final OverSizedInventory outputInventory = new OverSizedInventory(60, this) {
        public void setChanged() {
            super.setChanged();

            VaultJewelApplicationStationTileEntity.this.totalSizeInJewels = 0;

            for(int i = 0; i < 60; ++i) {
                // slot change ???
            }
        }
    };

    public int getTotalSizeInJewels() {
        return this.totalSizeInJewels;
    }

    public boolean stillValid(Player player) {
        return this.level != null &&
            this.level.getBlockEntity(this.worldPosition) == this &&
            this.inputInventory.stillValid(player);
    }

    public VaultJewelApplicationStationTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.VAULT_JEWEL_APPLICATION_STATION_ENTITY, pos, state);
    }

    public OverSizedInventory getInputInventory() {
        return this.inputInventory;
    }

    public OverSizedInventory getOutputInventory() {
        return this.outputInventory;
    }

    public ItemStack getJewelItem(int i) {
        i = Mth.clamp(i, 0, 60);
        return this.outputInventory.getItem(i);
    }

    public ItemStack getPouchItem(int i) {
        i = Mth.clamp(i, 0, 60);
        return this.inputInventory.getItem(i);
    }


    public void setJewelItem(int i, ItemStack stack) {
        i = Mth.clamp(i, 0, 60);
        this.outputInventory.setItem(i, stack);
    }

    public List<ItemStack> getJewels() {
        List<ItemStack> stacks = new ArrayList<>();

        for(int i = 0; i < 60; ++i) {
            stacks.add(this.outputInventory.getItem(i));
        }

        return stacks;
    }



    public List<ItemStack> getPouches() {
        List<ItemStack> stacks = new ArrayList<>();

        for(int i = 0; i < 60; ++i) {
            stacks.add(this.inputInventory.getItem(i));
        }

        return stacks;
    }


    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if (tag.contains("jewels"))
        {
            CompoundTag jewels = tag.getCompound("jewels");
            this.inputInventory.load(jewels);
            this.totalSizeInJewels = jewels.getInt("size");
        }

        if (tag.contains("jewels"))
        {
            CompoundTag pouches = tag.getCompound("pouches");
            this.outputInventory.load(pouches);
            this.totalSizeInPouches = pouches.getInt("size");
        }
    }

    protected void saveAdditional(@NotNull CompoundTag tag) {
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

    @NotNull
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nullable
    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return this.getLevel() == null ? null : new VaultJewelApplicationStationContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
    }
}
