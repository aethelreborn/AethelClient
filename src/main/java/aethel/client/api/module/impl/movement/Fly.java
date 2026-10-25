package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.api.settings.impl.ModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.api.settings.impl.BooleanSetting;
import net.minecraft.client.Minecraft;

public class Fly extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Fly mode", "Vanilla", "Vanilla", "Rotations", "Packet", "Scaffold"));
    private final BooleanSetting flight = register(new BooleanSetting("Flight", "Enable flight", true));
    private final NumberSetting speed = register(new NumberSetting("Speed", "Flight speed", 0.1, 0.01, 1.0, 0.01));
    private final BooleanSetting legit = register(new BooleanSetting("Legit", "Legit mode (less detectable)", false));

    private long lastFly = 0;

    public Fly() {
        super("Fly", "Enable flight", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onTick(Minecraft client) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        if (legit.getValue()) {
            handleLegalMode();
        } else {
            handleNormalMode();
        }
    }

    private void handleNormalMode() {
        // Fly enables flight
        // In a real client, this would mixin to enable creative/fly mode
        if (flight.getValue() && systemTime() - lastFly > 50) {
            // Enable flight
            lastFly = systemTime();
        }
    }

    private void handleLegalMode() {
        // Legit Fly: more humanized
        // Only enable flight when jumping, not constantly
        if (systemTime() - lastFly > 200 + (int) (Math.random() * 100)) {
            // Apply legit fly logic
            lastFly = systemTime();
        }
    }

    private long systemTime() {
        return System.currentTimeMillis();
    }
}