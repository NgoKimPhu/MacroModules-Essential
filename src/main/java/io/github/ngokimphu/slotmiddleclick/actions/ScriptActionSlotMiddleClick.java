package io.github.ngokimphu.slotmiddleclick.actions;

import io.github.ngokimphu.ModuleInfo;
import io.github.ngokimphu.slotmiddleclick.util.SlotClicker;
import net.eq2online.macros.scripting.api.APIVersion;
import net.eq2online.macros.scripting.api.IMacro;
import net.eq2online.macros.scripting.api.IMacroAction;
import net.eq2online.macros.scripting.api.IReturnValue;
import net.eq2online.macros.scripting.api.IScriptActionProvider;
import net.eq2online.macros.scripting.parser.ScriptAction;
import net.eq2online.macros.scripting.parser.ScriptContext;
import net.eq2online.macros.scripting.parser.ScriptCore;

/**
 * slotmiddleclick action to middle click a slot. Syntax:
 * slotmiddleclick(slotnumber)
 */
@APIVersion(ModuleInfo.API_VERSION)
public class ScriptActionSlotMiddleClick extends ScriptAction {
    protected final SlotClicker slotClicker;

    public ScriptActionSlotMiddleClick() {
        // Context is the context for this action, action name must be lowercase
        this(ScriptContext.MAIN);
    }

    public ScriptActionSlotMiddleClick(final ScriptContext context) {
        super(context, "slotmiddleclick");
        this.slotClicker = new SlotClicker(this.macros, this.mc);
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
     * Execute the slotmiddleclick action
     *
     * @param params 1 only parameter of slot number
     * @see net.eq2online.macros.scripting.actions.game.ScriptActionSlotClick#execute
     * @see net.eq2online.macros.scripting.parser.ScriptAction#execute
     */
    @Override
    public IReturnValue execute(final IScriptActionProvider provider, final IMacro macro,
            final IMacroAction instance, final String rawParams, final String[] params) {
        if (params.length > 0) {
            final int slotNumber = ScriptCore.tryParseInt(provider.expand(macro, params[0], false), 0);
            this.slotClicker.containerSlotMiddleClick(slotNumber);
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
