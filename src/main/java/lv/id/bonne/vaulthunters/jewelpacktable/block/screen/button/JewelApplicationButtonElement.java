//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.jewelpacktable.block.screen.button;


import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import lv.id.bonne.vaulthunters.jewelpacktable.block.menu.VaultJewelApplicationStationContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


public class JewelApplicationButtonElement<E extends JewelApplicationButtonElement<E>> extends
    ButtonElement<E>
{
    public JewelApplicationButtonElement(IPosition position, Runnable onClick, VaultJewelApplicationStationContainer container) {
        super(position, ScreenTextures.BUTTON_CRAFT_TEXTURES, onClick);
        this.tooltip(Tooltips.multi(() -> {
            long window = Minecraft.getInstance().getWindow().getWindow();
            boolean var10000;
            if (!InputConstants.isKeyDown(window, 340) && !InputConstants.isKeyDown(window, 344)) {
                var10000 = false;
            } else {
                var10000 = true;
            }

            ItemStack inputItem = ItemStack.EMPTY;
            Slot inputSlot = container.getSlot(36);
            if (!inputSlot.getItem().isEmpty()) {
                inputItem = inputSlot.getItem();
            }

            boolean hasInput = !inputItem.isEmpty();
            List<Component> tooltip = new ArrayList();
            if (hasInput) {
                VaultGearData toolData = VaultGearData.read(container.getTileEntity().getToolItem());
                int capacity = (Integer)toolData.getFirstValue(ModGearAttributes.TOOL_CAPACITY).orElse(0);
                if (container.getTileEntity().getTotalSizeInJewels() > 0) {
                    if (capacity >= container.getTileEntity().getTotalSizeInJewels()) {
                        tooltip.add(new TextComponent("Apply all Jewels"));
                        tooltip.add(new TextComponent(""));
                    }

                    tooltip.add((new TextComponent("Capacity: ")).append((new TextComponent("" + capacity)).withStyle(
                        Style.EMPTY.withColor(16757593))).append((new TextComponent(" (-" + container.getTileEntity().getTotalSizeInJewels() + ")")).withStyle(Style.EMPTY.withColor(
                        ChatFormatting.RED))));
                }

                if (capacity < container.getTileEntity().getTotalSizeInJewels()) {
                    tooltip.add((new TextComponent("Requires a larger capacity")).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                }
            } else {
                tooltip.add((new TextComponent("Requires a Tool")).withStyle(ChatFormatting.RED));
            }

            if (container.getTileEntity().getTotalSizeInJewels() == 0) {
                tooltip.add((new TextComponent("Requires at least one Jewel")).withStyle(ChatFormatting.RED));
            }

            return tooltip;
        }));
    }
}

