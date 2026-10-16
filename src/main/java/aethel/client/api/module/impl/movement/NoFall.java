package aethel.client.api.module.impl.movement;

import net.minecraft.client.Minecraft;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

/**
 * NoFall neutralizes all fall damage for the local player.
 * The actual damage skip lives in PlayerEntityMixin.handleFallDamage.
 */
public final class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Removes fall damage until you land safely.", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onTick(Minecraft client) {
        // Hook is passive; ticking only keeps config polling consistent.
    }
}
