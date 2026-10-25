package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class SpeedNCP extends Module {
    public SpeedNCP() {
        super("SpeedNCP", "SpeedNCP", ModuleType.MOVEMENT, "m");
    }
    
    @Override
    public void onEnable() {
        // SpeedNCP enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // SpeedNCP disable logic
    }
}