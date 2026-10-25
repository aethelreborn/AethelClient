package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class Speed extends Module {
    public Speed() {
        super("Speed", "Speed", ModuleType.MOVEMENT, "m");
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