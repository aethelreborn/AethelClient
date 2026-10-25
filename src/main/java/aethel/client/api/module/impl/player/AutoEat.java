package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class AutoEat extends Module {
    public AutoEat() {
        super("AutoEat", "AutoEat", ModuleType.PLAYER, "p");
    }
    
    @Override
    public void onEnable() {
        // AutoEat enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AutoEat disable logic
    }
}