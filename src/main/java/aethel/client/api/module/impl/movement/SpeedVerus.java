package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class SpeedVerus extends Module {
    public SpeedVerus() {
        super("SpeedVerus", "SpeedVerus", ModuleType.MOVEMENT, "m");
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