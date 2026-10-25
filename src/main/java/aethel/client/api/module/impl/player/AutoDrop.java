package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class AutoDrop extends Module {
    public AutoDrop() {
        super("AutoDrop", "AutoDrop", ModuleCategory.PLAYER);
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