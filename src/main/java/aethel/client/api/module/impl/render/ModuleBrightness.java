package aethel.client.api.module.impl.render;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class ModuleBrightness extends Module {
    public ModuleBrightness() {
        super("ModuleBrightness", "ModuleBrightness", ModuleCategory.VISUAL);
    }
    
    @Override
    public void onEnable() {
        // ModuleBrightness enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // ModuleBrightness disable logic
    }
}