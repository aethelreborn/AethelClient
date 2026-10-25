package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", "Scaffold", ModuleType.MOVEMENT, "m");
    }
    
    @Override
    public void onEnable() {
        // Scaffold enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // Scaffold disable logic
    }
}