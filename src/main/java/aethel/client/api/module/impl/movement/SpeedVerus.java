package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class SpeedVerus extends Module {
    public SpeedVerus() {
        super("SpeedVerus", "SpeedVerus", ModuleCategory.MOVEMENT);
    }
    
    @Override
    public void onEnable() {
        // SpeedVerus enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // SpeedVerus disable logic
    }
}