package lv.id.bonne.vaulthunters.morevaulttables.block.entity;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModItems;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.CardSelectorTableContainer;
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
 * The Card selector table allows to unpack Card Packs in single workstation without
 * using right-click menu.
 */
public class CardSelectorTableTileEntity extends BlockEntity implements MenuProvider
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
     * Gets pack item.
     * +1 is because first slot is for `unpacked` pack.
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
     * @param tag The NBT data that contains table content.
     */
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
    }


    /**
       * This method saves table content into NBT data.
       * @param tag The NBT data where table content need to be saved.
      */
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
            new CardSelectorTableContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
    }


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
    private final OverSizedInventory inputInventory = new OverSizedInventory(61, this)
    {
        /**
         * This method updates how many card pouches are in the system
         */
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
}
