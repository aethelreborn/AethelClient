package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class Reach extends Module {
    public Reach() {
        super("Reach", "Reach", ModuleCategory.COMBAT);
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