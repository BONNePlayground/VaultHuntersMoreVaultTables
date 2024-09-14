package lv.id.bonne.vaulthunters.morevaulttables.block.entity;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.init.ModItems;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesReferences;
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


/**
 * The Jewel selector table allows to unpack Jewel Packs in single workstation without
 * using right-click menu.
 */
public class JewelSelectorTableTileEntity extends BlockEntity implements MenuProvider
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

        if (tag.contains("jewels"))
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
