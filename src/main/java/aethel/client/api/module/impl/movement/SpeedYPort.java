package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class SpeedYPort extends Module {
    public SpeedYPort() {
        super("SpeedYPort", "SpeedYPort", ModuleCategory.MOVEMENT);
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