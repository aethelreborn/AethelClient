package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", "KillAura", ModuleType.COMBAT, "e");
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