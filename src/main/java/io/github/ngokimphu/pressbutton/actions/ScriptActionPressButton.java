package io.github.ngokimphu.pressbutton.actions;

import io.github.ngokimphu.pressbutton.ModuleInfo;
import io.github.ngokimphu.pressbutton.access.GuiContainerReflection;
import net.eq2online.macros.scripting.api.APIVersion;
import net.eq2online.macros.scripting.api.IMacro;
import net.eq2online.macros.scripting.api.IMacroAction;
import net.eq2online.macros.scripting.api.IReturnValue;
import net.eq2online.macros.scripting.api.IScriptActionProvider;
import net.eq2online.macros.scripting.parser.ScriptAction;
import net.eq2online.macros.scripting.parser.ScriptContext;
import net.eq2online.macros.scripting.parser.ScriptCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;

/**
 * This is an example script action. It registers itself in the MAIN context and
 * simply emits the text string "Hello World" when invoked.
 * 
 * <p>
 * The {@link APIVersion} annotation must match the target API version for this
 * module, we provide a central location to update the version by storing it in
 * the {@link ModuleInfo} class.
 * </p>
 */
@APIVersion(ModuleInfo.API_VERSION)
public class ScriptActionPressButton extends ScriptAction {

    /**
     * Initialization
     */
    public ScriptActionPressButton() {
        // Context is the context for this action, action name must be lowercase
        super(ScriptContext.MAIN, "pressbutton");
    }

    /**
     * This action cannot run on the network thread, eg. it is invalid in chat
     * filter scripts or onSendChatMessage handlers
     *
     * @see net.eq2online.macros.scripting.parser.ScriptAction#isThreadSafe()
     */
    @Override
    public boolean isThreadSafe() {
        return false;
    }

    /**
     * Execute the action
     * 
     * @see net.eq2online.macros.scripting.parser.ScriptAction#execute(
     *      net.eq2online.macros.scripting.api.IScriptActionProvider,
     *      net.eq2online.macros.scripting.api.IMacro,
     *      net.eq2online.macros.scripting.api.IMacroAction, java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public IReturnValue execute(IScriptActionProvider provider, IMacro macro, IMacroAction instance, String rawParams,
            String[] params) {

        int slotId = ScriptCore.tryParseInt(provider.expand(macro, params[0], false), 0);
        GuiContainer guiContainer = (GuiContainer) Minecraft.getMinecraft().currentScreen;
        GuiContainerReflection.handleMouseClick(guiContainer, guiContainer.inventorySlots.getSlot(slotId), slotId, 0,
                ClickType.QUICK_MOVE);

        if (params.length < 1) {
            return null;
        }
        return null;
    }

    /**
     * Called after this action is initialised, the action should register with
     * the script core.
     * 
     * @see net.eq2online.macros.scripting.parser.ScriptAction#onInit()
     */
    @Override
    public void onInit() {
        this.context.getCore().registerScriptAction(this);
    }

}
