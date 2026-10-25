package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class SpeedYPort extends Module {
    public SpeedYPort() {
        super("SpeedYPort", "SpeedYPort", ModuleType.MOVEMENT, "m");
    }
    
    @Override
    public void onEnable() {
        // SpeedYPort enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // SpeedYPort disable logic
    }
}