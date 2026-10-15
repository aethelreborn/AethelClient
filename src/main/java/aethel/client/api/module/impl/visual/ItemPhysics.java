package aethel.client.api.module.impl.visual;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.ModeSetting;

public class ItemPhysics extends Module {
    private static ItemPhysics instance;

    private final ModeSetting mode = register(new ModeSetting("Physics", "Dropped item physics mode.", "Normal", "Normal"));

    public ItemPhysics() {
        super("Item Physics", "Adds ground physics to dropped items.", ModuleCategory.VISUAL);
        instance = this;
    }

    public static ItemPhysics getInstance() {
        return instance;
    }
}
