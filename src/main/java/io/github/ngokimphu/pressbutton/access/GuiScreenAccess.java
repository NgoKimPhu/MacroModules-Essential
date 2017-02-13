package io.github.ngokimphu.pressbutton.access;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.mumfrey.liteloader.util.ObfuscationUtilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

public final class GuiScreenAccess {
    private static Field buttonList;
    private static Method actionPerformed;

    static {
        try {
            buttonList = GuiScreen.class
                    .getDeclaredField(ObfuscationUtilities.getObfuscatedFieldName("buttonList", "n", "field_146292_n"));
            buttonList.setAccessible(true);
            actionPerformed = GuiScreen.class.getDeclaredMethod(
                    ObfuscationUtilities.getObfuscatedFieldName("actionPerformed", "a", "func_146284_a"),
                    GuiButton.class);
            actionPerformed.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int screenButtonClick(int buttonNumber, int mouse) {
        try {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen == null) {
                return -1;
            }
            if (screen instanceof GuiEnchantment) {
                if (buttonNumber > 2) {
                    return Integer.MAX_VALUE;
                }
                Container enchantment = ((GuiEnchantment) screen).inventorySlots;
                if (enchantment.enchantItem(Minecraft.getMinecraft().player, buttonNumber)) {
                    Minecraft.getMinecraft().playerController.sendEnchantPacket(enchantment.windowId, buttonNumber);
                    return 0;
                }
                return -1;
            }
            List<GuiButton> buttonList = getButtonList(screen);
            if (buttonNumber >= buttonList.size()) {
                return Integer.MAX_VALUE;
            }
            GuiButton button = buttonList.get(buttonNumber);
            if (!button.mousePressed(Minecraft.getMinecraft(), button.xPosition, button.yPosition)) {
                return -1;
            }
            actionPerformed.invoke(screen, button);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<GuiButton> getButtonList(GuiScreen screen) throws Exception {
        return (List<GuiButton>) buttonList.get(screen);
    }

}
