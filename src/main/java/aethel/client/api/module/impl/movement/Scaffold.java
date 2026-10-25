package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", "Scaffold", ModuleCategory.MOVEMENT);
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