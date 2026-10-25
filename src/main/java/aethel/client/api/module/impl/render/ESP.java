package aethel.client.api.module.impl.render;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleType;

public class ESP extends Module {
    public ESP() {
        super("ESP", "ESP", ModuleType.RENDER, "r");
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