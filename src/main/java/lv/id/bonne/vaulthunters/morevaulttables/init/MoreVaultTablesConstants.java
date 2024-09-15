//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.init;



import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;


public class MoreVaultTablesConstants
{
    public static final CreativeModeTab MORE_TABLES = new CreativeModeTab("moretables")
    {
        @NotNull
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(MoreVaultTablesReferences.JEWEL_SELECTOR_TABLE_BLOCK);
        }


        @NotNull
        @Override
        public Component getDisplayName()
        {
            return new TranslatableComponent("itemGroup.moretables");
        }

        @Override
        public void fillItemList(@NotNull NonNullList<ItemStack> items)
        {
            super.fillItemList(items);  // Add your mod's items automatically

            // Check if another mod is loaded before adding its items
            if (ModList.get().isLoaded("the_vault"))
            {
                // TODO: add vault hunter tables
            }
        }
    };
}
