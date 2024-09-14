package lv.id.bonne.vaulthunters.morevaulttables.block.screen;


import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.*;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderFunction;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.item.JewelPouchItem;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesTextureAtlases;
import javax.annotation.Nullable;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.mixin.JewelPouchItemInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import static com.mojang.blaze3d.platform.InputConstants.Type.MOUSE;


public class JewelSelectorTableScreen extends AbstractElementContainerScreen<JewelSelectorTableContainer>
{
    ScrollableClickableItemStackSelectorElement pouchesElement;

    ScrollableClickableItemStackSelectorElement jewelsElement;

    SelectableFakeItemSlotElement<?> jewel1;
    SelectableFakeItemSlotElement<?> jewel2;
    SelectableFakeItemSlotElement<?> jewel3;
    SelectableFakeItemSlotElement<?> jewel4;

    NineSliceElement<?> pouchesBackgroundElement;

    NineSliceElement<?> jewelsBackgroundElement;

    boolean skipRelease = false;

    public JewelSelectorTableScreen(JewelSelectorTableContainer container,
        Inventory inventory,
        Component title)
    {
        super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
        this.setGuiSize(Spatials.size(276, 170));
        this.addElement(new NineSliceElement<>(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND).
            layout((screen, gui, parent, world) ->
                world.translateXY(gui).translateX(50).translateY(100).height(90).width(176)));

        this.addElement(new SlotsElement<>(this)).layout((screen, gui, parent, world) -> world.positionXY(gui));

        // Pouches slots
        this.addElement(this.pouchesElement = new ScrollableClickableItemStackSelectorElement<>(Spatials.positionXY(-3, 16).height(74),
                5,
                new ScrollableClickableItemStackSelectorElement.SelectorModel<>()
                {
                    public List<ScrollableClickableItemStackSelectorElement.ItemSelectorEntry> getEntries()
                    {
                        return JewelSelectorTableScreen.this.getMenu().getTileEntity().getPouches().stream().
                            map(ItemSelectorWithTooltipEntry::new).
                            collect(Collectors.toList());
                    }
                },
                ScrollMenu.POUCH)).
            layout((screen, gui, parent, world) -> world.translateXY(gui));

        this.addElement(this.pouchesBackgroundElement = new NineSliceElement<>(this.pouchesElement.getWorldSpatial(),
                ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
            layout((screen, gui, parent, world) -> world.
                translateXY(gui).
                translateX(-5).
                translateY(-5).
                translateZ(-10).
                width(this.pouchesElement.width() + 10).
                height(this.pouchesElement.height() + 10));

        // Jewel slots

        this.addElement(this.jewelsElement = new ScrollableClickableItemStackSelectorElement<>(Spatials.positionXY(172, 16).height(74),
            5,
            new ScrollableClickableItemStackSelectorElement.SelectorModel<>()
            {
                public List<ScrollableClickableItemStackSelectorElement.ItemSelectorEntry> getEntries()
                {
                    return JewelSelectorTableScreen.this.getMenu().getTileEntity().getJewels().stream().
                        map(ItemSelectorWithTooltipEntry::new).
                        collect(Collectors.toList());
                }
            },
            ScrollMenu.JEWEL)).layout((screen, gui, parent, world) -> world.translateXY(gui));

        this.addElement(this.jewelsBackgroundElement = new NineSliceElement<>(this.jewelsElement.getWorldSpatial(),
                ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
            layout((screen, gui, parent, world) -> world.
                translateXY(gui).
                translateX(-5).
                translateY(-5).
                translateZ(-10).
                width(this.jewelsElement.width() + 10).
                height(this.jewelsElement.height() + 10));

        // Title for Menu
        LabelElement<?> titleElement = this.addElement(new LabelElement<>(
            Spatials.positionXY(139 - Minecraft.getInstance().font.width(this.getMenu().getTileEntity().getDisplayName().copy()) / 2, -6),
            this.getMenu().getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
            LabelTextStyle.left()).
            layout((screen, gui, parent, world) -> world.translateXY(gui)));
        this.addElement(new NineSliceElement<>(titleElement.getWorldSpatial(),
                ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
            layout((screen, gui, parent, world) -> world.
                translateXY(gui).
                translateZ(-1).
                translateX(-6).
                translateY(-6).
                translateZ(-10).
                width(titleElement.width() + 10).
                height(titleElement.height() + 10));

        // Middle screen element
        if (container.getTileEntity() != null)
        {
            ItemStack stack = container.getTileEntity().getSelectedPouch();

            if (stack.getOrCreateTag().isEmpty())
            {
                if (container.getPlayer() instanceof ServerPlayer serverPlayer)
                {
                    int vaultLevel = JewelPouchItem.getStoredLevel(stack).orElseGet(() ->
                        PlayerVaultStatsData.get(serverPlayer.getLevel()).getVaultStats(serverPlayer).getVaultLevel());

                    int additionalIdentifiedJewels = 0;
                    ExpertiseTree expertises =
                        PlayerExpertisesData.get(serverPlayer.getLevel()).getExpertises(serverPlayer);

                    JewelExpertise expertise;
                    for (Iterator<?> var10 = expertises.getAll(JewelExpertise.class, Skill::isUnlocked).iterator();
                        var10.hasNext(); additionalIdentifiedJewels += expertise.getAdditionalIdentifiedJewels())
                    {
                        expertise = (JewelExpertise) var10.next();
                    }

                    JewelPouchItemInvoker.invokeGenerateJewels(stack, vaultLevel, additionalIdentifiedJewels);
                }
            }

            List<JewelPouchItem.RolledJewel> rolledJewels = JewelPouchItem.getJewels(stack);

//            if (!rolledJewels.isEmpty())
            {
                this.jewel1 = new SelectableFakeItemSlotElement<>(Spatials.positionXY(125, 16),
                    () -> rolledJewels.isEmpty() ? ItemStack.EMPTY : rolledJewels.get(0).stack(),
                    () -> true).
                    setLabelStackCount().
                    layout((screen, gui, parent, world) -> world.translateXY(gui));
                this.jewel1.whenClicked(JewelSelectorTableScreen.this.new MouseClickRunnable(121));
                this.addElement(this.jewel1);
                this.jewel1.setDisabled(() -> false);

                this.addElement(new NineSliceElement<>(this.jewel1.getWorldSpatial(),
                        ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
                    layout((screen, gui, parent, world) ->
                    {
                        world.translateXY(gui).
                            translateZ(-1).
                            translateX(-5).
                            translateY(-5).
                            translateZ(-10).
                            width(this.jewel1.width() + 10).
                            height(this.jewel1.height() + 10);
                    });


                this.jewel2 = new SelectableFakeItemSlotElement<>(Spatials.positionXY(125, 32),
                    () -> rolledJewels.isEmpty() ? ItemStack.EMPTY : rolledJewels.get(1).stack(),
                    () -> true).
                    setLabelStackCount().
                    layout((screen, gui, parent, world) -> world.translateXY(gui));
                this.jewel2.whenClicked(JewelSelectorTableScreen.this.new MouseClickRunnable(122));
                this.addElement(this.jewel2);
                this.jewel2.setDisabled(() -> false);


                this.addElement(new NineSliceElement<>(this.jewel2.getWorldSpatial(),
                        ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
                    layout((screen, gui, parent, world) ->
                    {
                        world.translateXY(gui).
                            translateZ(-1).
                            translateX(-5).
                            translateY(-5).
                            translateZ(-10).
                            width(this.jewel2.width() + 10).
                            height(this.jewel2.height() + 10);
                    });

                this.jewel3 = new SelectableFakeItemSlotElement<>(Spatials.positionXY(125, 48),
                    () -> rolledJewels.isEmpty() ? ItemStack.EMPTY : rolledJewels.get(2).stack(),
                    () -> true).
                    setLabelStackCount().
                    layout((screen, gui, parent, world) -> world.translateXY(gui));
                this.jewel3.whenClicked(JewelSelectorTableScreen.this.new MouseClickRunnable(123));
                this.addElement(this.jewel3);
                this.jewel3.setDisabled(() -> false);

                this.addElement(new NineSliceElement<>(this.jewel3.getWorldSpatial(),
                        ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
                    layout((screen, gui, parent, world) ->
                    {
                        world.translateXY(gui).
                            translateZ(-1).
                            translateX(-5).
                            translateY(-5).
                            translateZ(-10).
                            width(this.jewel3.width() + 10).
                            height(this.jewel3.height() + 10);
                    });


                int a = 0;
//            this.stackElement = new ToolItemSlotElement(Spatials.positionXY(122, 24), () -> {
//                return stack;
//            }, () -> {
//                return false;
//            }, 32, 32).setLabelStackCount().layout((screen, gui, parent, world) -> {
//                world.translateXY(gui);
//            });
//            this.addElement(this.stackElement);
            }
        }
    }


    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }


    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton)
    {
        if (this.skipRelease || this.pouchesBackgroundElement.contains(pMouseX, pMouseY))
        {
            this.skipRelease = false;
            this.isQuickCrafting = false;
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }


    public void mouseClicked(int buttonIndex, int slotClicked)
    {
        InputConstants.Key mouseKey = MOUSE.getOrCreate(buttonIndex);
        Slot slot = this.menu.getSlot(slotClicked);
        this.skipRelease = false;

        if (slot != null)
        {
            int l = slot.index;
            if (l != -1 && !this.isQuickCrafting)
            {
                boolean flag2;
                ClickType clicktype;
                if (this.menu.getCarried().isEmpty())
                {
                    if (this.minecraft != null && this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey))
                    {
                        this.slotClicked(slot, l, buttonIndex, ClickType.CLONE);
                    }
                    else
                    {
                        flag2 = (l != -999 && l != 999) && (
                            InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) ||
                                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                        clicktype = ClickType.PICKUP;
                        if (flag2)
                        {
                            clicktype = ClickType.QUICK_MOVE;
                        }
                        else if (l == -999 || l == 999)
                        {
                            clicktype = ClickType.THROW;
                        }

                        this.slotClicked(slot, l, buttonIndex, clicktype);
                    }
                }
                else if (this.minecraft == null || !this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey))
                {
                    flag2 = (l != -999 && l != 999) && (
                        InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) ||
                            InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                    clicktype = ClickType.PICKUP;
                    if (flag2)
                    {
                        clicktype = ClickType.QUICK_MOVE;
                    }

                    this.slotClicked(slot, l, buttonIndex, clicktype);
                }

                this.skipRelease = true;
            }
        }
    }


    protected void containerTick()
    {
        super.containerTick();
        if (this.menu.getTileEntity() != null)
        {
            if (!this.menu.getTileEntity().getSelectedPouch().isEmpty()) {
                this.jewel1.tooltip(Tooltips.shift(Tooltips.multi(() -> {
                    return this.jewel1.getDisplayStack().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
                }), Tooltips.multi(() -> {
                    return this.jewel1.getDisplayStack().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.ADVANCED);
                })));

                this.jewel2.tooltip(Tooltips.shift(Tooltips.multi(() -> {
                    return this.jewel2.getDisplayStack().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
                }), Tooltips.multi(() -> {
                    return this.jewel2.getDisplayStack().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.ADVANCED);
                })));

                this.jewel3.tooltip(Tooltips.shift(Tooltips.multi(() -> {
                    return this.jewel3.getDisplayStack().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
                }), Tooltips.multi(() -> {
                    return this.jewel3.getDisplayStack().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.ADVANCED);
                })));

            } else {
                this.jewel1.tooltip(ITooltipRenderFunction.NONE);
                this.jewel2.tooltip(ITooltipRenderFunction.NONE);
                this.jewel3.tooltip(ITooltipRenderFunction.NONE);
            }

            this.pouchesElement.refreshElements(this.getMenu());
            this.jewelsElement.refreshElements(this.getMenu());
//            this.tooltipContainerElement.refresh(((VaultJewelApplicationStationContainer)this.menu).getTileEntity().getRenderedTool());
        }
    }


    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        InputConstants.Key key = InputConstants.getKey(pKeyCode, pScanCode);
        if (pKeyCode != 256 && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key))
        {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        else
        {
            this.onClose();
            return true;
        }
    }


    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        super.render(poseStack, mouseX, mouseY, partialTick);
    }


    public class ScrollableClickableItemStackSelectorElement<E extends ScrollableClickableItemStackSelectorElement<E, S>, S extends ScrollableClickableItemStackSelectorElement.ItemSelectorEntry>
        extends VerticalScrollClipContainer<E>
    {
        private final int slotColumns;

        protected final SelectorModel<S> selectorModel;

        protected final TextureAtlasRegion slotTexture;

        protected final TextureAtlasRegion disabledSlotTexture;

        private final ScrollableClickableItemStackSelectorElement<E, S>.SelectorContainer<?> elementCt;

        private final ScrollMenu scrollMenuType;

        public ScrollableClickableItemStackSelectorElement(ISpatial spatial,
            int slotColumns,
            SelectorModel<S> selectorModel,
            ScrollMenu scrollMenuType)
        {
            this(spatial,
                slotColumns,
                selectorModel,
                ScreenTextures.INSET_ITEM_SLOT_BACKGROUND,
                ScreenTextures.INSET_DISABLED_ITEM_SLOT_BACKGROUND,
                scrollMenuType);
        }


        private ScrollableClickableItemStackSelectorElement(ISpatial spatial,
            int slotColumns,
            SelectorModel<S> selectorModel,
            TextureAtlasRegion slotTexture,
            TextureAtlasRegion disabledSlotTexture,
            ScrollMenu scrollMenuType)
        {
            super(Spatials.copy(spatial).width(slotColumns * slotTexture.width() + 17));
            this.scrollMenuType = scrollMenuType;
            this.slotColumns = slotColumns;
            this.selectorModel = selectorModel;
            this.slotTexture = slotTexture;
            this.disabledSlotTexture = disabledSlotTexture;
            this.addElement(this.elementCt = new SelectorContainer(spatial.width()));
        }


        public void refreshElements(JewelSelectorTableContainer container)
        {
            this.elementCt.refresh(container);
        }


        protected SelectorModel<S> getSelectorModel()
        {
            return this.selectorModel;
        }


        protected List<ClickableItemSlotElement<?>> getSelectorElements()
        {
            return Collections.unmodifiableList(this.elementCt.slots);
        }


        protected ClickableItemSlotElement<?> makeElementSlot(ISpatial spatial,
            Supplier<ItemStack> itemStack,
            TextureAtlasRegion slotTexture,
            TextureAtlasRegion disabledSlotTexture,
            Supplier<Boolean> disabled)
        {
            return (
                new ClickableItemSlotElement(spatial, itemStack, disabled, slotTexture, disabledSlotTexture)
                {
                    public boolean containsMouse(double x, double y)
                    {
                        if (scrollMenuType == ScrollMenu.JEWEL && !JewelSelectorTableScreen.this.jewelsElement.contains(x, y))
                        {
                            return false;
                        }
                        else if (scrollMenuType == ScrollMenu.POUCH && !JewelSelectorTableScreen.this.pouchesElement.contains(x, y))
                        {
                            return false;
                        }
                        else
                        {
                            return x < (double) this.right() && x >= (double) this.left() && y >= (double) this.top() &&
                                y < (double) this.bottom();
                        }
                    }
                }).overlayTexture(scrollMenuType == ScrollMenu.JEWEL ? ScreenTextures.JEWEL_NO_ITEM : MoreVaultTablesTextureAtlases.POUCH_NO_ITEM);
        }


        public abstract static class SelectorModel<E extends ItemSelectorEntry>
        {
            private Consumer<ClickableItemSlotElement<?>> onSlotSelect = (slot) ->
            {
            };

            private E selectedElement = null;


            public SelectorModel()
            {
            }


            protected void onSlotSelect(Consumer<ClickableItemSlotElement<?>> onSlotSelect)
            {
                this.onSlotSelect = onSlotSelect;
            }


            public abstract List<E> getEntries();


            public void onSelect(ClickableItemSlotElement<?> slot, E entry)
            {
                this.selectedElement = entry;
                this.onSlotSelect.accept(slot);
            }


            @Nullable
            public E getSelectedElement()
            {
                return this.selectedElement;
            }
        }


        private class SelectorContainer<T extends ScrollableClickableItemStackSelectorElement<E, S>.SelectorContainer<T>>
            extends ElasticContainerElement<T>
        {
            private final List<ClickableItemSlotElement<?>> slots = new ArrayList<>();


            private SelectorContainer(int inheritedWidth)
            {
                super(Spatials.positionXY(0, 0).width(inheritedWidth));
                this.buildElements();
            }


            public void refresh(JewelSelectorTableContainer container)
            {
                for (int i = 0; i < this.slots.size(); ++i)
                {
                    int finalI = i;

                    this.slots.get(i).setItemStack(() ->
                    {
                        if (scrollMenuType == ScrollMenu.JEWEL)
                        {
                            return container.getTileEntity().getJewelItem(finalI);
                        }
                        else
                        {
                            return container.getTileEntity().getPouchItem(finalI);
                        }
                    });

                    if (!this.slots.get(finalI).getDisplayStack().isEmpty())
                    {
                        this.slots.get(i).tooltip(
                            this.slots.get(finalI).getDisplayStack().isEmpty() ?
                                ITooltipRenderFunction.NONE :
                                Tooltips.shift(
                                    Tooltips.multi(() -> this.slots.get(finalI).getDisplayStack().
                                        getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL)),
                                    Tooltips.multi(() -> this.slots.get(finalI).getDisplayStack().
                                        getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.ADVANCED))));
                    }
                    else
                    {
                        this.slots.get(i).tooltip(ITooltipRenderFunction.NONE);
                    }
                }
            }


            private void buildElements()
            {
                this.slots.clear();
                List<S> entries = ScrollableClickableItemStackSelectorElement.this.selectorModel.getEntries();

                for (int i = 0; i < entries.size(); ++i)
                {
                    S entry = entries.get(i);
                    int column = i % ScrollableClickableItemStackSelectorElement.this.slotColumns;
                    int row = i / ScrollableClickableItemStackSelectorElement.this.slotColumns;
                    ItemStack stack = entry.getDisplayStack();
                    boolean disabled = entry.isDisabled();
                    ClickableItemSlotElement<?> clickableSlot =
                        ScrollableClickableItemStackSelectorElement.this.makeElementSlot(Spatials.positionXY(0, 0)
                                .translateX(column * ScrollableClickableItemStackSelectorElement.this.slotTexture.width())
                                .translateY(row * ScrollableClickableItemStackSelectorElement.this.slotTexture.height()),
                            () ->
                            {
                                return stack;
                            },
                            ScrollableClickableItemStackSelectorElement.this.slotTexture,
                            ScrollableClickableItemStackSelectorElement.this.disabledSlotTexture,
                            () ->
                            {
                                return disabled;
                            });
                    clickableSlot.whenClicked(JewelSelectorTableScreen.this.new MouseClickRunnable(37 + i + (scrollMenuType == ScrollMenu.JEWEL ? 59 : 0)));
                    entry.adjustSlot(clickableSlot);
                    this.addElement(clickableSlot);
                    this.slots.add(clickableSlot);
                }
            }
        }


        public static class ItemSelectorEntry
        {
            private final ItemStack displayStack;

            private final boolean isDisabled;


            public ItemSelectorEntry(ItemStack displayStack, boolean isDisabled)
            {
                this.displayStack = displayStack;
                this.isDisabled = isDisabled;
            }


            public ItemStack getDisplayStack()
            {
                return this.displayStack;
            }


            public boolean isDisabled()
            {
                return this.isDisabled;
            }


            public void adjustSlot(ClickableItemSlotElement<?> slot)
            {
            }
        }
    }


    public class MouseClickRunnable implements Runnable
    {
        int type = 0;

        int slot = 0;


        public MouseClickRunnable(int s)
        {
            this.slot = s;
        }


        public void setType(int type)
        {
            this.type = type;
        }


        public void run()
        {
            JewelSelectorTableScreen.this.mouseClicked(this.type, this.slot);
        }
    }


    private static class ItemSelectorWithTooltipEntry
        extends ScrollableClickableItemStackSelectorElement.ItemSelectorEntry
    {
        public ItemSelectorWithTooltipEntry(ItemStack displayStack)
        {
            super(displayStack, false);
        }


        public void adjustSlot(ClickableItemSlotElement<?> slot)
        {
            slot.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) ->
            {
                if (!this.getDisplayStack().isEmpty())
                {
                    tooltipRenderer.renderTooltip(poseStack,
                        this.getDisplayStack(),
                        mouseX,
                        mouseY,
                        TooltipDirection.RIGHT);
                }

                return true;
            });
            slot.setLabelStackCount();
        }
    }


    enum ScrollMenu
    {
        JEWEL,
        POUCH
    }
}