package aethel.client.api.module.impl.misc;

import org.lwjgl.glfw.GLFW;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.bind.KeyBind;

/**
 * Special module that only exists for its bind: pressing the configured
 * key opens / closes the Aethel ClickGui. It is hidden from the module
 * list so it never looks like a toggleable feature.
 */
public final class ClickGuiModule extends Module {
    public static final String NAME = "ClickGui";

    public ClickGuiModule() {
        super(NAME, "Opens the Aethel ClickGui with its configured key.", ModuleCategory.MISC);
        setHidden(true);
        setBind(KeyBind.keyboard(GLFW.GLFW_KEY_RIGHT_SHIFT));
    }

    public void setDefaultBindIfUnbound() {
        if (!getBind().isBound()) {
            setBind(KeyBind.keyboard(GLFW.GLFW_KEY_RIGHT_SHIFT));
        }
    }
}
