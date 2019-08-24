package io.github.ngokimphu.macromodules.essential.getslotnbt.actions;

import io.github.ngokimphu.macromodules.essential.ModuleInfo;
import net.eq2online.macros.scripting.api.APIVersion;
import net.eq2online.macros.scripting.api.IMacro;
import net.eq2online.macros.scripting.api.IMacroAction;
import net.eq2online.macros.scripting.api.IReturnValue;
import net.eq2online.macros.scripting.api.IScriptActionProvider;
import net.eq2online.macros.scripting.api.ReturnValue;
import net.eq2online.macros.scripting.parser.ScriptAction;
import net.eq2online.macros.scripting.parser.ScriptContext;
import net.eq2online.macros.scripting.parser.ScriptCore;
import net.eq2online.util.Game;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;

/**
 * getslotnbt action to get an nbt tag of an item
 * <p>
 * Syntax: getslotnbt(slotid, path[, <&itemId>[, <#stackSize>[, <#damage>]]])
 */
@APIVersion(ModuleInfo.API_VERSION)
public class ScriptActionGetSlotNbt extends ScriptAction {

    private static final String LOG_COLOR = "§c";
    private static final String LOG_PREFIX = "§2[getslotnbt] " + LOG_COLOR;

    public ScriptActionGetSlotNbt() {
        // Context is the context for this action, action name must be lowercase
        this(ScriptContext.MAIN);
    }

    public ScriptActionGetSlotNbt(final ScriptContext context) {
        super(context, "getslotnbt");
    }

    /**
     * This action cannot run on the network thread, e.g. it is invalid in chat
     * filter scripts or onSendChatMessage handlers
     *
     * @see net.eq2online.macros.scripting.parser.ScriptAction#isThreadSafe()
     */
    @Override
    public boolean isThreadSafe() {
        return false;
    }

    /**
     * Execute the getslotnbt action
     *
     * @param params slotid slot id <br>
     *            path nbt property path (delimited by ., /, or \) <br>
     *            <&itemId> reference to a string var to store the item id <br>
     *            <#stackSize> reference to a numeric var to store the stack
     *            size <br>
     *            <#damage> reference to a numeric var to store the item damage
     *            <br>
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
        String itemID = "unknown";
        int stackSize = 0;
        int damage = 0;
        String nbtValue = "";

        if (params.length > 0) {
            final int retVal = Math.max(0,
                    ScriptCore.tryParseInt(provider.expand(macro, params[0], false), 0));
            final ItemStack slotStack = this.slotHelper.getSlotStack(retVal);
            if (slotStack == null) {
                itemID = Game.getItemName((Item) null);
            } else {
                itemID = Game.getItemName(slotStack.getItem());
                stackSize = slotStack.getCount();
                damage = slotStack.getMetadata();
                NBTBase tag = slotStack.getTagCompound();
                final String nbtPath = params.length < 2 ? "" : provider.expand(macro, params[1], false);
                final String[] nbtSubpaths = nbtPath.isEmpty() ? new String[0] : nbtPath.split("[./\\\\]");

                boolean nbtValueFetched = false;
                for (final String path : nbtSubpaths) {
                    final NBTBase prevTag = tag;

                    try {
                        int idx = Integer.parseInt(path);

                        if (tag instanceof NBTTagList) {
                            final int tagCount = ((NBTTagList) tag).tagCount();
                            idx = (idx % tagCount + tagCount) % tagCount;
                            tag = ((NBTTagList) tag).get(idx);
                        } else {
                            if (tag instanceof NBTTagByteArray) {
                                final byte[] byteArray = ((NBTTagByteArray) tag).getByteArray();
                                idx = (idx % byteArray.length + byteArray.length) % byteArray.length;
                                nbtValue = String.valueOf(byteArray[idx]);
                            } else if (tag instanceof NBTTagIntArray) {
                                final int[] intArray = ((NBTTagIntArray) tag).getIntArray();
                                idx = (idx % intArray.length + intArray.length) % intArray.length;
                                nbtValue = String.valueOf(intArray[idx]);
                            } else if (tag instanceof NBTTagLongArray) {
                                final String[] longStringArray = ((NBTTagLongArray) tag).toString()
                                        .replace("[L;", "").replace("L]", "").split("L,");
                                idx = (idx % longStringArray.length + longStringArray.length)
                                        % longStringArray.length;
                                nbtValue = longStringArray[idx];
                            } else {
                                throw new IllegalStateException();
                            }
                            nbtValueFetched = true;
                            break;
                        }
                    } catch (NumberFormatException | IllegalStateException e) {
                        if (tag instanceof NBTTagCompound) {
                            tag = ((NBTTagCompound) tag).getTag(path);
                        } else {
                            tag = null;
                        }
                    }

                    if (tag == null) {
                        provider.actionAddChatMessage(
                                String.format(LOG_PREFIX + "Sub-path §d%s" + LOG_COLOR + " not found in %s",
                                        path, prevTag));
                    }
                }

                if (!nbtValueFetched) {
                    nbtValue = String.valueOf(tag);
                }
            }
        }

        final ReturnValue retVal1 = new ReturnValue(nbtValue);
        if (params.length > 2) {
            provider.setVariable(macro, provider.expand(macro, params[2], false), itemID);
        }

        if (params.length > 3) {
            provider.setVariable(macro, provider.expand(macro, params[3], false), stackSize);
        }

        if (params.length > 4) {
            provider.setVariable(macro, provider.expand(macro, params[4], false), damage);
        }

        return retVal1;
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
