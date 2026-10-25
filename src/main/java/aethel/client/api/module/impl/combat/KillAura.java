package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", "KillAura", ModuleCategory.COMBAT);
    }
    
    @Override
    public void onEnable() {
        // KillAura enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // KillAura disable logic
    }
}