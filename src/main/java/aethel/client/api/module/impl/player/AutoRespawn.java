package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", "AutoRespawn", ModuleType.PLAYER, "p");
    }
    
    @Override
    public void onEnable() {
        // AutoRespawn enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AutoRespawn disable logic
    }
}