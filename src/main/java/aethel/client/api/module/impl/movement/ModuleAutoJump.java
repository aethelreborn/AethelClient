package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class ModuleAutoJump extends Module {
    public ModuleAutoJump() {
        super("AutoJump", "AutoJump", ModuleCategory.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        // ModuleAutoJump enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // ModuleAutoJump disable logic
    }
}