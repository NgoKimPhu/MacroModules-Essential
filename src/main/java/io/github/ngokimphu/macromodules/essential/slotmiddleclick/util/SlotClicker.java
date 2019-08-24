package io.github.ngokimphu.macromodules.essential.slotmiddleclick.util;

import net.eq2online.macros.core.Macros;
import net.eq2online.macros.core.mixin.IContainerCreative;
import net.eq2online.macros.core.mixin.IGuiContainer;
import net.eq2online.macros.core.mixin.IGuiContainerCreative;
import net.eq2online.macros.gui.helpers.SlotHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SlotClicker extends SlotHelper {
    private final Minecraft mc;

    public SlotClicker(final Macros macros, final Minecraft mc) {
        super(macros, mc);
        this.mc = mc;
    }

    public void containerSlotMiddleClick(int slotNumber) {
        final int middleButton = this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
        try {
            if (!this.currentScreenIsContainer()) {
                if (this.noScreenInGame() && slotNumber >= 1 && slotNumber <= 9) {
                    this.mc.player.inventory.currentItem = slotNumber - 1;
                }
                return;
            }

            final GuiContainer ex = this.getGuiContainer();
            final Container slots = ex.inventorySlots;
            Slot slot;
            if (ex instanceof GuiContainerCreative) {
                if (slotNumber < 45) {
                    ((IGuiContainerCreative) ex).setCreativeTab(CreativeTabs.INVENTORY);
                    slot = slotNumber < 5 ? ((IGuiContainerCreative) ex).getBinSlot()
                            : slots.getSlot(slotNumber);
                } else if (slotNumber < 54) {
                    slot = slots.getSlot(slotNumber);
                } else {
                    if (slotNumber > 599 && slotNumber < 700) {
                        return;
                    }

                    final int slot2 = slotNumber / 100 - 1;
                    ((IGuiContainerCreative) ex).setCreativeTab(CreativeTabs.CREATIVE_TAB_ARRAY[slot2]);
                    slotNumber -= (slot2 + 1) * 100;
                    if (!scrollContainerTo((GuiContainerCreative) ex, slotNumber)) {
                        return;
                    }
                    slotNumber -= getCreativeInventoryScroll((GuiContainerCreative) ex);
                    slot = slots.getSlot(slotNumber);
                }
                ((IGuiContainerCreative) ex).mouseClick(slot, slotNumber, middleButton, ClickType.CLONE);
            } else if (slotNumber >= 0 && slotNumber < slots.inventorySlots.size() || slotNumber == -999) {
                slot = slotNumber == -999 ? null : slots.getSlot(slotNumber);
                ((IGuiContainer) ex).mouseClick(slot, slotNumber, middleButton, ClickType.CLONE);
            }
        } catch (final Exception arg7) {
            arg7.printStackTrace();
        }

    }

    private boolean noScreenInGame() {
        return this.mc.currentScreen == null && this.mc.player != null && this.mc.player.inventory != null
                && this.mc.player.inventory.mainInventory != null;
    }

    private static boolean scrollContainerTo(final GuiContainerCreative containerGui, final int slotNumber)
            throws IllegalArgumentException, SecurityException {
        float currentScroll = 0.0F;
        final int lastInventoryScroll = setScrollPosition(containerGui, currentScroll);
        final NonNullList<?> itemsList = ((IContainerCreative) containerGui.inventorySlots).getItemsList();
        final float scrollIncrement = (float) (1.0D / (itemsList.size() / 9 - 5 + 1));

        int inventoryScroll;
        do {
            if (isInRange(slotNumber, getCreativeInventoryScroll(containerGui), 45)) {
                return true;
            }

            currentScroll += scrollIncrement;
            inventoryScroll = setScrollPosition(containerGui, currentScroll);
        } while (inventoryScroll != lastInventoryScroll);

        return isInRange(slotNumber, inventoryScroll, 45);
    }

    private static boolean isInRange(final int value, final int start, final int rangeLength) {
        return value >= start && value < start + rangeLength;
    }

    private static int getCreativeInventoryScroll(final GuiContainerCreative containerGui) {
        try {
            final InventoryBasic ex = IGuiContainerCreative.getCreativeInventory();
            final ItemStack firstSlotStack = ex.getStackInSlot(0);
            final NonNullList<?> itemsList = ((IContainerCreative) containerGui.inventorySlots)
                    .getItemsList();
            return itemsList.indexOf(firstSlotStack);
        } catch (final Exception arg3) {
            arg3.printStackTrace();
            return 0;
        }
    }

}
