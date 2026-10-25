package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "AutoWalk", ModuleCategory.PLAYER);
    }
    
    @Override
    public void onEnable() {
        // AutoWalk enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AutoWalk disable logic
    }
}