package aethel.client.api.module.impl.player;

import net.minecraft.client.Minecraft;
import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.impl.DeathScreenEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.ModeSetting;

public final class AutoRespawn extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Respawn mode.", "Default", "Default"));

    public AutoRespawn() {
        super("Auto Respawn", "Automatically respawns after death.", ModuleCategory.PLAYER);
    }

    @SubscribeEvent
    private void onDeathScreen(DeathScreenEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (mode.is("Default") && client.player != null) {
            client.player.respawn();
            client.setScreen(null);
        }
    }
}
