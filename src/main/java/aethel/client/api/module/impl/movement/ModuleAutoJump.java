package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class ModuleAutoJump extends Module {
    public ModuleAutoJump() {
        super("AutoJump", "AutoJump", ModuleType.MOVEMENT, "m");
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