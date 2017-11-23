package io.github.ngokimphu.getprop.actions;

import io.github.ngokimphu.ModuleInfo;
import net.eq2online.macros.scripting.api.APIVersion;
import net.eq2online.macros.scripting.api.IMacro;
import net.eq2online.macros.scripting.api.IMacroAction;
import net.eq2online.macros.scripting.api.IReturnValue;
import net.eq2online.macros.scripting.api.IScriptActionProvider;
import net.eq2online.macros.scripting.api.ReturnValue;
import net.eq2online.macros.scripting.parser.ScriptAction;
import net.eq2online.macros.scripting.parser.ScriptContext;
import net.eq2online.macros.scripting.parser.ScriptCore;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * getprop action to get a property of hit block Syntax: getprop(x, y, z,
 * propname[, <propvalue>])
 */
@APIVersion(ModuleInfo.API_VERSION)
public class ScriptActionGetProp extends ScriptAction {

    private static final String LOG_COLOR = "§c";
    private static final String LOG_PREFIX = "§2[getprop] " + LOG_COLOR;

    public ScriptActionGetProp() {
        // Context is the context for this action, action name must be lowercase
        this(ScriptContext.MAIN);
    }

    public ScriptActionGetProp(final ScriptContext context) {
        super(context, "getprop");
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
     * Execute the getprop action
     *
     * @param params x, y, z Minecraft-style coordination propname (name of the
     *            property whose value is to be retrieved)
     *            <&propvalue> reference to a string var to store the result
     * @return the property's value
     * @see net.eq2online.macros.scripting.parser.ScriptAction#execute(
     *      net.eq2online.macros.scripting.api.IScriptActionProvider,
     *      net.eq2online.macros.scripting.api.IMacro,
     *      net.eq2online.macros.scripting.api.IMacroAction, java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public IReturnValue execute(final IScriptActionProvider provider, final IMacro macro,
            final IMacroAction instance, final String rawParams, final String[] params) {
        if (params.length < 4) {
            provider.actionAddChatMessage(
                    LOG_PREFIX + "At least 4 parameters are required: x, y, z, and propName");
            return null;
        }

        final WorldClient theWorld = this.mc.world;
        final EntityPlayerSP thePlayer = this.mc.player;
        if (theWorld != null && thePlayer != null) {
            final int xPos = this.getPosition(provider, macro, params[0], thePlayer.posX);
            final int yPos = this.getPosition(provider, macro, params[1], thePlayer.posY);
            final int zPos = this.getPosition(provider, macro, params[2], thePlayer.posZ);
            final BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
            final IBlockState blockState = theWorld.getBlockState(blockPos);

            for (final IProperty<?> property : blockState.getPropertyKeys()) {
                if (property.getName().equalsIgnoreCase(provider.expand(macro, params[3], false))) {
                    final ReturnValue retVal = new ReturnValue(blockState.getValue(property).toString());

                    if (params.length > 4) {
                        final String propValueVar = provider.expand(macro, params[4], false).toLowerCase();
                        provider.setVariable(macro, propValueVar, retVal);
                    }

                    return retVal;
                }
            }
            provider.actionAddChatMessage(
                    String.format(LOG_PREFIX + "Property §d%s" + LOG_COLOR + " not found at %d %d %d",
                            provider.expand(macro, params[3], false), xPos, yPos, zPos));

        } else {
            provider.actionAddChatMessage(LOG_PREFIX + "Unexpected null world/player!");
        }
        return null;
    }

    private int getPosition(final IScriptActionProvider provider, final IMacro macro, final String param,
            final double currentPos) {
        final String sPos = provider.expand(macro, param, false);
        final boolean isRelative = sPos.length() > 0 && sPos.charAt(0) == '~';
        return (isRelative ? MathHelper.floor(currentPos) : 0)
                + ScriptCore.tryParseInt(isRelative ? sPos.substring(1) : sPos, 0);
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
