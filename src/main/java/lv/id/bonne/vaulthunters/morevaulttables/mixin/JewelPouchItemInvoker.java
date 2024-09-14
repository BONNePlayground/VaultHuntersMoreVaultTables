//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import iskallia.vault.item.JewelPouchItem;
import net.minecraft.world.item.ItemStack;


@Mixin(value = JewelPouchItem.class, remap = false)
public interface JewelPouchItemInvoker
{
    @Invoker("generateJewels")
    static void invokeGenerateJewels(ItemStack stack, int vaultLevel, int additionalIdentifiedJewels)
    {
        throw new AssertionError();
    }
}
