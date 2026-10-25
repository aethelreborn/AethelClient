package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "AutoWalk", ModuleType.PLAYER, "p");
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