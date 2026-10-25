package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class AutoCrystal extends Module {
    public AutoCrystal() {
        super("AutoCrystal", "AutoCrystal", ModuleCategory.COMBAT);
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