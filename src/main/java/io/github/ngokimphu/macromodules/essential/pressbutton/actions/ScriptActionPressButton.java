package io.github.ngokimphu.macromodules.essential.pressbutton.actions;

import io.github.ngokimphu.macromodules.essential.ModuleInfo;
import io.github.ngokimphu.macromodules.essential.pressbutton.access.GuiScreenAccess;
import net.eq2online.macros.scripting.api.APIVersion;
import net.eq2online.macros.scripting.api.IMacro;
import net.eq2online.macros.scripting.api.IMacroAction;
import net.eq2online.macros.scripting.api.IReturnValue;
import net.eq2online.macros.scripting.api.IScriptActionProvider;
import net.eq2online.macros.scripting.api.ReturnValue;
import net.eq2online.macros.scripting.parser.ScriptAction;
import net.eq2online.macros.scripting.parser.ScriptContext;
import net.eq2online.macros.scripting.parser.ScriptCore;

/**
 * pressbutton action to press a button in a GuiScreen (enchant, villager...)
 * Syntax: pressbutton(buttonId, mouseButton) buttonId: id of the button in the
 * GUI mouseButton: any string not beginning with l for right, left otherwise
 */
@APIVersion(ModuleInfo.API_VERSION)
public class ScriptActionPressButton extends ScriptAction {

    public ScriptActionPressButton() {
        // Context is the context for this action, action name must be lowercase
        this(ScriptContext.MAIN);
    }

    public ScriptActionPressButton(final ScriptContext context) {
        super(context, "pressbutton");
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
     * Execute the pressbutton action
     *
     * @param params buttonId (id of the button in the GUI) and mouseButton (any
     *            string not beginning with l for right, left otherwise)
     * @see net.eq2online.macros.scripting.parser.ScriptAction#execute(
     *      net.eq2online.macros.scripting.api.IScriptActionProvider,
     *      net.eq2online.macros.scripting.api.IMacro,
     *      net.eq2online.macros.scripting.api.IMacroAction, java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public IReturnValue execute(final IScriptActionProvider provider, final IMacro macro,
            final IMacroAction instance, final String rawParams, final String[] params) {
        if (params.length < 1) {
            return null;
        }

        final int id = ScriptCore.tryParseInt(provider.expand(macro, params[0], false), 0),
                button = params.length < 2 || provider.expand(macro, params[1], false).startsWith("l") ? 0
                        : 1;
        switch (GuiScreenAccess.screenButtonClick(id, button)) {
        case Integer.MAX_VALUE:
            provider.actionAddChatMessage("Invalid button ID " + id);
            break;
        case -2:
            provider.actionAddChatMessage("Exception thrown");
            break;
        case 0:
            return new ReturnValue(true);
        default:
            return new ReturnValue(false);
        }
        return null;
    }

    /**
     * Called after this action is initialized, the action should register with
     * the script core.
     *
     * @see net.eq2online.macros.scripting.parser.ScriptAction#onInit()
     */
    @Override
    public void onInit() {
        this.context.getCore().registerScriptAction(this);
    }

}
