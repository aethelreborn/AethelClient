package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class Sneak extends Module {
    public Sneak() {
        super("Sneak", "Sneak", ModuleCategory.MOVEMENT);
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