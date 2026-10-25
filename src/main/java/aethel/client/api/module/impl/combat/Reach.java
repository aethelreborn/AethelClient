package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class Reach extends Module {
    public Reach() {
        super("Reach", "Reach", ModuleType.COMBAT, "e");
    }
    
    @Override
    public void onEnable() {
        // Reach enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // Reach disable logic
    }
}