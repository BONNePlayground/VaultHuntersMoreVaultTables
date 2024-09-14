//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.registries;


import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


/**
 * The More vault tables item registry.
 */
public class MoreVaultTablesItemRegistry
{
    /**
     * The Items registry
     */
    public static final DeferredRegister<Item> REGISTRY =
        DeferredRegister.create(ForgeRegistries.ITEMS, MoreVaultTablesMod.MODID);
}
