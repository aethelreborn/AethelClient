package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class AutoInteract extends Module {
    public AutoInteract() {
        super("AutoInteract", "AutoInteract", ModuleCategory.PLAYER);
    }
    
    @Override
    public void onEnable() {
        // AutoInteract enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AutoInteract disable logic
    }
}