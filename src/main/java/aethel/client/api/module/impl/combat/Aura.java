package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class Aura extends Module {
    public Aura() {
        super("Aura", "Aura", ModuleCategory.COMBAT);
    }
    
    @Override
    public void onEnable() {
        // Aura enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // Aura disable logic
    }
}