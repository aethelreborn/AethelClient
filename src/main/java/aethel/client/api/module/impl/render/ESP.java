package aethel.client.api.module.impl.render;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class ESP extends Module {
    public ESP() {
        super("ESP", "ESP", ModuleCategory.VISUAL);
    }
    
    @Override
    public void onEnable() {
        // ESP enable logic with anti-detection
    }
    
    @Override
    public void onDisable() {
        // ESP disable logic
    }
}