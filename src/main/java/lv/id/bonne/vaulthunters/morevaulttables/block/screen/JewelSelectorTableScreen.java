package lv.id.bonne.vaulthunters.morevaulttables.block.screen;


import com.mojang.blaze3d.platform.InputConstants;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
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
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesTextureAtlases;
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


/**
 * This class manages Jewel Selector Table screen.
 */
public class JewelSelectorTableScreen extends AbstractElementContainerScreen<JewelSelectorTableContainer>
{
    /**
     * Instantiates a new Jewel selector table screen.
     *
     * @param container the container
     * @param inventory the inventory
     * @param title the title
     */
    public JewelSelectorTableScreen(JewelSelectorTableContainer container,
        Inventory inventory,
        Component title)
    {
        super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);

        // Initialize GUI size.
        this.setGuiSize(Spatials.size(276, 170));

        // Do not know what is this.
        this.addElement(new NineSliceElement<>(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND).
            layout((screen, gui, parent, world) ->
                world.translateXY(gui).translateX(50).translateY(100).height(90).width(176)));

        // Init slots for this menu
        this.addElement(new SlotsElement<>(this)).layout((screen, gui, parent, world) -> world.positionXY(gui));

        // Pouches slots
        this.addElement(this.pouchesElement =
                new ScrollableClickableItemStackSelectorElement<>(Spatials.positionXY(-3, 16).height(74),
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

        // Pouches background
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
        this.addElement(this.jewelsElement =
            new ScrollableClickableItemStackSelectorElement<>(Spatials.positionXY(172, 16).height(74),
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

        // Jewel background
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
            Spatials.positionXY(
                139 - Minecraft.getInstance().font.width(this.getMenu().getTileEntity().getDisplayName().copy()) / 2,
                -6),
            this.getMenu().getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
            LabelTextStyle.left()).
            layout((screen, gui, parent, world) -> world.translateXY(gui)));
        this.addElement(new NineSliceElement<>(titleElement.getWorldSpatial(),
                ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
            layout((screen, gui, parent, world) -> world.
                translateXY(gui).
                translateX(-6).
                translateY(-6).
                translateZ(-10).
                width(titleElement.width() + 10).
                height(titleElement.height() + 10));

        // The elements that are displayed in the GUI.
        this.elementList = new ArrayList<>(4);

        // Middle screen element
        if (container.getTileEntity() != null)
        {
            ItemStack stack = container.getTileEntity().getSelectedPouch();

            int numberOfSlots = 3;

            if (!stack.isEmpty() && stack.getOrCreateTag().isEmpty())
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

                    numberOfSlots += additionalIdentifiedJewels;
                    JewelPouchItemInvoker.invokeGenerateJewels(stack, vaultLevel, additionalIdentifiedJewels);
                }
            }

            this.addElement((new NineSliceElement<>(Spatials.positionXY(this.pouchesBackgroundElement.right() + 5, 16).
                width(this.jewelsBackgroundElement.left() - this.pouchesBackgroundElement.right() - 10).
                height(84),
                ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
                layout((screen, gui, parent, world) -> world.translateXY(gui).translateY(-5).translateZ(-10)));

            // Get rolled jewels
            List<JewelPouchItem.RolledJewel> rolledJewels = JewelPouchItem.getJewels(stack);

            for (int i = 0; i < numberOfSlots; i++)
            {
                final int finalI = i;

                SelectableFakeItemSlotElement<?> slot = new SelectableFakeItemSlotElement<>(Spatials.positionXY(130, 18 * (i + 1)),
                    () -> rolledJewels.isEmpty() ? ItemStack.EMPTY : rolledJewels.get(finalI).stack(),
                    () -> rolledJewels.isEmpty() || this.getMenu().getTileEntity().getTotalSizeInJewels() >= 60).
                    setLabelStackCount().
                    layout((screen, gui, parent, world) -> world.translateXY(gui));

                slot.whenClicked(JewelSelectorTableScreen.this.new MouseClickRunnable(121 + i));
                this.addElement(slot);
                this.elementList.add(slot);
            }
        }
    }


    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton)
    {
        if (this.skipRelease ||
            this.pouchesBackgroundElement.contains(pMouseX, pMouseY) ||
            this.jewelsBackgroundElement.contains(pMouseX, pMouseY))
        {
            this.skipRelease = false;
            this.isQuickCrafting = false;
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }


    /**
     * Custom mouse click handling. This targets clicks on items inside scrollbars.
     * @param scrollMenu The scrollbar type.
     * @param buttonIndex Button in scrollbar.
     * @param slotClicked Clicked slot.
     */
    public void mouseClicked(ScrollMenu scrollMenu, int buttonIndex, int slotClicked)
    {
        InputConstants.Key mouseKey = MOUSE.getOrCreate(buttonIndex);
        Slot slot = this.menu.getSlot(slotClicked);
        this.skipRelease = false;

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


    protected void containerTick()
    {
        super.containerTick();
        if (this.menu.getTileEntity() != null)
        {
            if (!this.menu.getTileEntity().getSelectedPouch().isEmpty())
            {
                this.elementList.forEach(element -> {
                    element.tooltip(Tooltips.shift(
                        Tooltips.multi(() -> element.getDisplayStack().getTooltipLines(Minecraft.getInstance().player,
                            TooltipFlag.Default.NORMAL)),
                        Tooltips.multi(() -> element.getDisplayStack().getTooltipLines(Minecraft.getInstance().player,
                            TooltipFlag.Default.ADVANCED))));
                });
            }

            this.pouchesElement.refreshElements(this.getMenu());
            this.jewelsElement.refreshElements(this.getMenu());
        }
    }


    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        InputConstants.Key key = InputConstants.getKey(pKeyCode, pScanCode);
        if (pKeyCode != GLFW.GLFW_KEY_ESCAPE && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key))
        {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        else
        {
            this.onClose();
            return true;
        }
    }


    public class ScrollableClickableItemStackSelectorElement<E extends ScrollableClickableItemStackSelectorElement<E, S>, S extends ScrollableClickableItemStackSelectorElement.ItemSelectorEntry>
        extends VerticalScrollClipContainer<E>
    {
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
                        if (scrollMenuType == ScrollMenu.JEWEL &&
                            !JewelSelectorTableScreen.this.jewelsElement.contains(x, y))
                        {
                            return false;
                        }
                        else if (scrollMenuType == ScrollMenu.POUCH &&
                            !JewelSelectorTableScreen.this.pouchesElement.contains(x, y))
                        {
                            return false;
                        }
                        else
                        {
                            return x < (double) this.right() && x >= (double) this.left() && y >= (double) this.top() &&
                                y < (double) this.bottom();
                        }
                    }
                }).overlayTexture(scrollMenuType == ScrollMenu.JEWEL ? ScreenTextures.JEWEL_NO_ITEM :
                MoreVaultTablesTextureAtlases.POUCH_NO_ITEM);
        }


        private class SelectorContainer<T extends ScrollableClickableItemStackSelectorElement<E, S>.SelectorContainer<T>>
            extends ElasticContainerElement<T>
        {
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
                                        getTooltipLines(Minecraft.getInstance().player,
                                            TooltipFlag.Default.ADVANCED))));
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
                    clickableSlot.whenClicked(JewelSelectorTableScreen.this.new MouseClickRunnable(
                        37 + i + (scrollMenuType == ScrollMenu.JEWEL ? 59 : 0)));
                    entry.adjustSlot(clickableSlot);
                    this.addElement(clickableSlot);
                    this.slots.add(clickableSlot);
                }
            }

            private final List<ClickableItemSlotElement<?>> slots = new ArrayList<>();
        }


        public abstract static class SelectorModel<E extends ItemSelectorEntry>
        {
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

            private Consumer<ClickableItemSlotElement<?>> onSlotSelect = (slot) ->
            {
            };

            private E selectedElement = null;
        }


        public static class ItemSelectorEntry
        {
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

            private final ItemStack displayStack;

            private final boolean isDisabled;
        }

        protected final SelectorModel<S> selectorModel;

        protected final TextureAtlasRegion slotTexture;

        protected final TextureAtlasRegion disabledSlotTexture;

        private final int slotColumns;

        private final ScrollableClickableItemStackSelectorElement<E, S>.SelectorContainer<?> elementCt;

        private final ScrollMenu scrollMenuType;
    }


    public class MouseClickRunnable implements Runnable
    {
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
            JewelSelectorTableScreen.this.mouseClicked(this.scrollMenu, this.type, this.slot);
        }

        ScrollMenu scrollMenu;

        int type = 0;

        int slot = 0;
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


    /**
     * This enum stores possible scroll bar menu types.
     */
    private enum ScrollMenu
    {
        JEWEL(999),
        POUCH(-999);

        ScrollMenu(int location)
        {
            this.location = location;
        }


        /**
         * This method returns if selected menu is with given value.
         * @param value the value that need to be checked.
         * @return true is that is selected menu.
         */
        boolean isMenu(int value)
        {
            return this.location == value;
        }

        /**
         * The location of scrollbar.
         */
        private final int location;
    }

    /**
     * The pouches container element.
     */
    private final ScrollableClickableItemStackSelectorElement<?, ?> pouchesElement;

    /**
     * The pouches container background.
     */
    private final NineSliceElement<?> pouchesBackgroundElement;

    /**
     * The jewel container element.
     */
    private final ScrollableClickableItemStackSelectorElement<?, ?> jewelsElement;

    /**
     * The jewel container background.
     */
    private final NineSliceElement<?> jewelsBackgroundElement;

    /**
     * The opened jewel list.
     */
    private final List<SelectableFakeItemSlotElement<?>> elementList;

    boolean skipRelease = false;
}