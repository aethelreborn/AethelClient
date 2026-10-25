package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "NoFall", ModuleCategory.COMBAT);
    }
    
    @Override
    public void onEnable() {
        // NoFall enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // NoFall disable logic
    }
}