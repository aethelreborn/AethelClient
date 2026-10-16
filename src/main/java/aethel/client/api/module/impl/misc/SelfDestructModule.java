package aethel.client.api.module.impl.misc;

import aethel.client.manager.Manager;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

/**
 * Vape-style self destruct: instantly hides Aethel — disables every
 * module, clears binds handling, kills the HUD and mutes all client
 * chat output.
 */
public final class SelfDestructModule extends Module {
    public SelfDestructModule() {
        super("Self Destruct", "Hides Aethel instantly — state restores after a game restart.", ModuleCategory.MISC);
    }

    @Override
    protected void onEnable() {
        Manager manager = Manager.getInstance();
        if (manager != null) {
            manager.selfDestruct();
        }
    }
}
