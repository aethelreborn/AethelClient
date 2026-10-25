package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class AutoDrop extends Module {
    public AutoDrop() {
        super("AutoDrop", "AutoDrop", ModuleType.PLAYER, "p");
    }
    
    @Override
    public void onEnable() {
        // AutoDrop enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AutoDrop disable logic
    }
}