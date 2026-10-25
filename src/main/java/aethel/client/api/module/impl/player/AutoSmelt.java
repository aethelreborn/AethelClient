package aethel.client.api.module.impl.player;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class AutoSmelt extends Module {
    public AutoSmelt() {
        super("AutoSmelt", "AutoSmelt", ModuleCategory.PLAYER);
    }
    
    @Override
    public void onEnable() {
        // AutoSmelt enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // AutoSmelt disable logic
    }
}