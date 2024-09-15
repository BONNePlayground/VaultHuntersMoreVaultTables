package lv.id.bonne.vaulthunters.morevaulttables.block.screen;


import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
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
import iskallia.vault.init.ModItems;
import iskallia.vault.item.JewelPouchItem;
import lv.id.bonne.vaulthunters.morevaulttables.block.menu.JewelSelectorTableContainer;
import lv.id.bonne.vaulthunters.morevaulttables.init.MoreVaultTablesTextureAtlases;
import lv.id.bonne.vaulthunters.morevaulttables.network.MoreVaultTablesNetwork;
import lv.id.bonne.vaulthunters.morevaulttables.network.packets.MoveAndOpenObjectPacket;
import lv.id.bonne.vaulthunters.morevaulttables.network.packets.SelectCraftingObjectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
        this.rolledJewelList = new ArrayList<>();

        // Middle screen element
        if (container.getTileEntity() != null)
        {
            ItemStack stack = container.getTileEntity().getSelectedPouch();

            if (!stack.isEmpty() && stack.getOrCreateTag().isEmpty())
            {
                MoreVaultTablesNetwork.sendToServer(new MoveAndOpenObjectPacket(false));
            }

            this.addElement((new NineSliceElement<>(Spatials.positionXY(this.pouchesBackgroundElement.right() + 5, 16).
                width(this.jewelsBackgroundElement.left() - this.pouchesBackgroundElement.right() - 10).
                height(84),
                ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).
                layout((screen, gui, parent, world) -> world.translateXY(gui).translateY(-5).translateZ(-10)));

            // Get rolled jewels
            this.collectJewels();

            for (int i = 0; i < 3; i++)
            {
                final int finalI = i;

                SelectableFakeItemSlotElement<?> slot = new SelectableFakeItemSlotElement<>(Spatials.positionXY(
                    121,
                    9 + 18 * (i + 1)),
                    () -> this.rolledJewelList.size() > finalI ? this.rolledJewelList.get(finalI).stack() : ItemStack.EMPTY,
                    () -> this.menu.getTileEntity().getSelectedPouch().isEmpty() ||
                        this.rolledJewelList.size() <= finalI ||
                        this.menu.getTileEntity().getTotalSizeInJewels() >= 60).
                    setLabelStackCount().
                    layout((screen, gui, parent, world) -> world.translateXY(gui));

                slot.whenClicked(new MouseClickRunnable(i, ScrollMenu.RESULT));
                this.addElement(slot);
                this.elementList.add(slot);
            }

            SelectableFakeItemSlotElement<?> slot = new SelectableFakeItemSlotElement<>(Spatials.positionXY(
                139,
                9 + 18 * 2),
                () -> this.rolledJewelList.size() > 3 ? this.rolledJewelList.get(3).stack() : ItemStack.EMPTY,
                () -> this.menu.getTileEntity().getSelectedPouch().isEmpty() ||
                    this.rolledJewelList.size() <= 3 ||
                    this.getMenu().getTileEntity().getTotalSizeInJewels() >= 60).
                setLabelStackCount().
                layout((screen, gui, parent, world) -> world.translateXY(gui));

            slot.whenClicked(new MouseClickRunnable(3, ScrollMenu.RESULT));
            this.addElement(slot);
            this.elementList.add(slot);
        }
    }


    /**
     * This method manages container ticking.
     */
    @Override
    protected void containerTick()
    {
        super.containerTick();

        if (this.menu.getTileEntity() != null)
        {
            // Redraw all jewels from the pouch
            if (this.regenerate || !this.menu.getTileEntity().getSelectedPouch().isEmpty() && this.rolledJewelList.isEmpty())
            {
                this.collectJewels();
                this.regenerate = false;
            }

            this.elementList.forEach(element -> {
                if (!element.getDisplayStack().isEmpty())
                {
                    element.tooltip(Tooltips.shift(
                        Tooltips.multi(() -> element.getDisplayStack().getTooltipLines(Minecraft.getInstance().player,
                            TooltipFlag.Default.NORMAL)),
                        Tooltips.multi(() -> element.getDisplayStack().getTooltipLines(Minecraft.getInstance().player,
                            TooltipFlag.Default.ADVANCED))));
                }
            });

            // Refresh other elements.
            this.pouchesElement.refreshElements(this.getMenu());
            this.jewelsElement.refreshElements(this.getMenu());
        }
    }


    /**
     * Mouse release detections.
     * @param mouseX Mouse X location.
     * @param mouseY Mouse Y location.
     * @param button Clicked button.
     * @return {@code true} if mouse released, {@code false} otherwise
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (this.skipRelease ||
            this.pouchesBackgroundElement.contains(mouseX, mouseY) ||
            this.jewelsBackgroundElement.contains(mouseX, mouseY))
        {
            this.skipRelease = false;
            this.isQuickCrafting = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }


    /**
     * Key release detections.
     * @param keyCode Released key code.
     * @param scanCode Scan code.
     * @param modifiers Modifier.
     * @return {@code true} if key released, {@code false} otherwise
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);

        if (keyCode != GLFW.GLFW_KEY_ESCAPE && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key))
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        else
        {
            this.onClose();
            return true;
        }
    }


    /**
     * This method performs quick move from fake slots.
     * @param clickedSlot clicked slot.
     * @param scrollMenu Menu type.
     */
    private void mouseClickedMoved(int clickedSlot, ScrollMenu scrollMenu)
    {
        InputConstants.Key mouseKey = MOUSE.getOrCreate(0);
        Slot slot = this.menu.getSlot(clickedSlot);

        this.skipRelease = false;
        int slotIndex = slot.index;

        if (slotIndex != -1 && !this.isQuickCrafting)
        {
            boolean canQuickMove;
            ClickType clicktype;

            if (this.menu.getCarried().isEmpty())
            {
                if (this.minecraft != null &&
                    this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey))
                {
                    this.slotClicked(slot, slotIndex, 0, ClickType.CLONE);
                }
                else
                {
                    canQuickMove = scrollMenu != ScrollMenu.RESULT && (
                        InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) ||
                            InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));

                    clicktype = ClickType.PICKUP;

                    if (canQuickMove)
                    {
                        clicktype = ClickType.QUICK_MOVE;
                    }

                    this.slotClicked(slot, slotIndex, 0, clicktype);
                }
            }
            else if (this.minecraft == null ||
                !this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey))
            {
                canQuickMove = scrollMenu != ScrollMenu.RESULT && (
                    InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) ||
                        InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));

                clicktype = ClickType.PICKUP;

                if (canQuickMove)
                {
                    clicktype = ClickType.QUICK_MOVE;
                }

                this.slotClicked(slot, slotIndex, 0, clicktype);
            }

            this.skipRelease = true;
        }

        if (scrollMenu == ScrollMenu.POUCH)
        {
            if (this.menu.getTileEntity().getSelectedPouch().isEmpty())
            {
                MoreVaultTablesNetwork.sendToServer(new MoveAndOpenObjectPacket(true));
            }
        }
    }


    /**
     * This method performs crafted jewel click.
     * @param clickedSlot Clicked jewel
     */
    private void mouseClickedCraft(int clickedSlot)
    {
        MoreVaultTablesNetwork.sendToServer(new SelectCraftingObjectPacket(clickedSlot));
    }


    /**
     * This method forces GUI to refresh crafting Jewel items.
     */
    public void setRegenerate()
    {
        this.regenerate = true;
    }


    /**
     * This method collects jewels and generates if they are not yet.
     */
    private void collectJewels()
    {
        this.rolledJewelList.clear();

        if (this.menu.getTileEntity().getSelectedPouch().is(ModItems.JEWEL_POUCH))
        {
            List<JewelPouchItem.RolledJewel> rolledJewels = JewelPouchItem.getJewels(this.menu.getTileEntity().getSelectedPouch());

            if (rolledJewels.isEmpty())
            {
                MoreVaultTablesNetwork.sendToServer(new MoveAndOpenObjectPacket(false));
                return;
            }

            this.rolledJewelList.addAll(rolledJewels);
        }
    }


    /**
     * This custom vertical scroll container allows to link slots that are out of screen with actual elements in them.
     * @param <E>
     * @param <S>
     */
    private class ScrollableClickableItemStackSelectorElement<E extends ScrollableClickableItemStackSelectorElement<E, S>, S extends ScrollableClickableItemStackSelectorElement.ItemSelectorEntry>
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
            this.addElement(this.elementCt = new SelectorContainer<>(spatial.width()));
        }


        public void refreshElements(JewelSelectorTableContainer container)
        {
            this.elementCt.refresh(container);
        }


        protected ClickableItemSlotElement<?> makeElementSlot(ISpatial spatial,
            Supplier<ItemStack> itemStack,
            TextureAtlasRegion slotTexture,
            TextureAtlasRegion disabledSlotTexture,
            Supplier<Boolean> disabled)
        {
            return new ClickableItemSlotElement(spatial, itemStack, disabled, slotTexture, disabledSlotTexture)
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
            }.overlayTexture(scrollMenuType == ScrollMenu.JEWEL ? ScreenTextures.JEWEL_NO_ITEM :
                MoreVaultTablesTextureAtlases.POUCH_NO_ITEM);
        }


        private class SelectorContainer<T extends SelectorContainer<T>>
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
                        ScrollableClickableItemStackSelectorElement.this.makeElementSlot(Spatials.positionXY(0, 0).
                                translateX(column * ScrollableClickableItemStackSelectorElement.this.slotTexture.width()).
                                translateY(row * ScrollableClickableItemStackSelectorElement.this.slotTexture.height()),
                            () -> stack,
                            ScrollableClickableItemStackSelectorElement.this.slotTexture,
                            ScrollableClickableItemStackSelectorElement.this.disabledSlotTexture,
                            () -> disabled);
                    clickableSlot.whenClicked(new MouseClickRunnable(i, scrollMenuType));
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


            public abstract List<E> getEntries();
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

        private final SelectorContainer<?> elementCt;

        private final ScrollMenu scrollMenuType;
    }


    /**
     * This class manages custom tooltip generation for items in scroll bars.
     */
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
     * This runnable is triggered on each mouse click.
     */
    private class MouseClickRunnable implements Runnable
    {
        /**
         * Inits new runnable with specific clicked slot.
         * @param slot slot index.
         */
        public MouseClickRunnable(int slot, ScrollMenu menu)
        {
            this.slot = slot + switch (menu) {
                case JEWEL -> 36 + 1 + 60;
                case POUCH -> 36 + 1;
                case RESULT -> 0;
            };

            this.scrollMenu = menu;
        }


        /**
         * The run instance.
         */
        public void run()
        {
            if (this.scrollMenu != ScrollMenu.RESULT)
            {
                JewelSelectorTableScreen.this.mouseClickedMoved(this.slot, this.scrollMenu);
            }
            else
            {
                JewelSelectorTableScreen.this.mouseClickedCraft(this.slot);
            }
        }


        /**
         * This variable stores which menu is clicked.
         */
        ScrollMenu scrollMenu;

        /**
         * This variable stores clicked slot.
         */
        int slot;
    }


    /**
     * This enum stores possible scroll bar menu types.
     */
    enum ScrollMenu
    {
        JEWEL,
        POUCH,
        RESULT
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

    /**
     * The list of rolled jewels.
     */
    private final List<JewelPouchItem.RolledJewel> rolledJewelList;

    /**
     * Indicates if pouch list need to be regenerated.
     */
    boolean regenerate = true;

    boolean skipRelease = false;
}