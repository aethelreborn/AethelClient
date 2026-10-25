package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class AntiAfk extends Module {
    public AntiAfk() {
        super("AntiAfk", "AntiAfk", ModuleCategory.COMBAT);
    }
    
    @Override
    public void onEnable() {
        // AntiAfk enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AntiAfk disable logic
    }
}