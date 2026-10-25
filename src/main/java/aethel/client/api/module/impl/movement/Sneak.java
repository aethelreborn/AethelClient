package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class Sneak extends Module {
    public Sneak() {
        super("Sneak", "Sneak", ModuleType.MOVEMENT, "m");
    }
    
    @Override
    public void onEnable() {
        // Sneak enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // Sneak disable logic
    }
}