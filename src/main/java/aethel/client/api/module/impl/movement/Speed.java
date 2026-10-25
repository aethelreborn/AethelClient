package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class Speed extends Module {
    public Speed() {
        super("Speed", "Speed", ModuleCategory.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        // Speed enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // Speed disable logic
    }
}