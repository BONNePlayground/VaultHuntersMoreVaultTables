package lv.id.bonne.vaulthunters.jewelpacktable.block.menu;

import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.ToolItem;
import lv.id.bonne.vaulthunters.jewelpacktable.block.entity.VaultJewelApplicationStationTileEntity;
import lv.id.bonne.vaulthunters.jewelpacktable.init.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


public class VaultJewelApplicationStationContainer extends OverSizedSlotContainer {
  private final VaultJewelApplicationStationTileEntity tileEntity;
  private final BlockPos tilePos;

  public VaultJewelApplicationStationContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
    super(ModContainers.VAULT_JEWEL_APPLICATION_STATION_CONTAINER, windowId, playerInventory.player);
    this.tilePos = pos;
    BlockEntity tile = world.getBlockEntity(this.tilePos);
    if (tile instanceof VaultJewelApplicationStationTileEntity craftingStationTileEntity) {
      this.tileEntity = craftingStationTileEntity;
      this.initSlots(playerInventory);
    } else {
      this.tileEntity = null;
    }

  }

  public void broadcastChanges() {
    super.broadcastChanges();
  }

  private void initSlots(Inventory playerInventory) {
    int hotbarSlot;
    int i;
    for(hotbarSlot = 0; hotbarSlot < 3; ++hotbarSlot) {
      for(i = 0; i < 9; ++i) {
        this.addSlot(new TabSlot(playerInventory, i + hotbarSlot * 9 + 9, 58 + i * 18, 108 + hotbarSlot * 18));
      }
    }

    for(hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
      this.addSlot(new TabSlot(playerInventory, hotbarSlot, 58 + hotbarSlot * 18, 166));
    }

    Container invContainer = this.tileEntity.getInventory();
    this.addSlot((new TabSlot(invContainer, 0, 120, 73) {
      public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
      }
    }).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.TOOL_NO_ITEM));

    for(i = 0; i < 20; ++i) {
      for(int j = 0; j < 3; ++j) {
        this.addSlot(new TabSlot(invContainer, i * 3 + j + 1, -999 + j * 18, 50 + i * 18) {
          public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof JewelItem;
          }
        });
      }
    }

  }

  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = (Slot)this.slots.get(index);
    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      itemstack = slotStack.copy();
      if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, this.slots.size(), false)) {
        return itemstack;
      }

      if (index >= 0 && index < 27) {
        if (!this.moveOverSizedItemStackTo(slotStack, slot, 27, 36, false)) {
          return ItemStack.EMPTY;
        }
      } else if (index >= 27 && index < 36) {
        if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 27, false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 36, false)) {
        return ItemStack.EMPTY;
      }

      if (slotStack.getCount() == 0) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }

      if (slotStack.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(player, slotStack);
    }

    return itemstack;
  }

  public BlockPos getTilePos() {
    return this.tilePos;
  }

  public VaultJewelApplicationStationTileEntity getTileEntity() {
    return this.tileEntity;
  }

  public boolean stillValid(Player player) {
    return this.tileEntity == null ? false : this.tileEntity.stillValid(this.player);
  }
}
