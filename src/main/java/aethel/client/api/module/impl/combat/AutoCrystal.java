package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class AutoCrystal extends Module {
    public AutoCrystal() {
        super("AutoCrystal", "AutoCrystal", ModuleType.COMBAT, "e");
    }
    
    @Override
    public void onEnable() {
        // AutoCrystal enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AutoCrystal disable logic
    }
}