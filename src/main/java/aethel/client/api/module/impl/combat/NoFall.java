package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "NoFall", ModuleType.COMBAT, "e");
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