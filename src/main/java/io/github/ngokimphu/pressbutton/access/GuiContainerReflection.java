package io.github.ngokimphu.pressbutton.access;

import java.lang.reflect.Method;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;

public final class GuiContainerReflection {
    public static void handleMouseClick(GuiContainer guiContainer, Slot slot, int slotId, int button,
            ClickType clickType) {
        try {
            Method handleMouseClick = GuiContainer.class.getDeclaredMethod("handleMouseClick", Slot.class, int.class,
                    int.class, ClickType.class);
            handleMouseClick.setAccessible(true);
            handleMouseClick.invoke(guiContainer, slot, slotId, button, clickType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}