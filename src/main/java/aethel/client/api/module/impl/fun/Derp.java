package aethel.client.api.module.impl.fun;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.api.settings.impl.BooleanSetting;

public class Derp extends Module {
    private final BooleanSetting derpMode = register(new BooleanSetting("DerpMode", "Enables derp mode", false));

    public Derp() {
        super("Derp", "Makes the client act derpy", ModuleCategory.FUN);
    }

    @Override
    public void onTick() {
        if (derpMode.getValue()) {
            // Derp mode logic - random movements/animations
        }
    }
}