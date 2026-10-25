package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class AutoPotion extends Module {
    public AutoPotion() {
        super("AutoPotion", "AutoPotion", ModuleCategory.PLAYER);
    }
    
    @Override
    public void onEnable() {
        // AutoPotion enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AutoPotion disable logic
    }
}