package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class AutoInteract extends Module {
    public AutoInteract() {
        super("AutoInteract", "AutoInteract", ModuleType.PLAYER, "p");
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