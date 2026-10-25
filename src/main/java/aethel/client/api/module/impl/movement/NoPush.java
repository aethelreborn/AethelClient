package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.api.settings.impl.ModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.api.settings.impl.BooleanSetting;
import net.minecraft.client.Minecraft;

public class NoPush extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "NoPush mode", "Vanilla", "Vanilla", "Rotations", "Packet", "Legit"));
    private final BooleanSetting push immunity = register(new BooleanSetting("Immunity", "Immune to knockback", true));
    private final BooleanSetting legit = register(new BooleanSetting("Legit", "Legit mode (less detectable)", false));

    private long lastNoPush = 0;

    public NoPush() {
        super("NoPush", "Immunity to knockback", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onTick() {
        Minecraft mc = mc();
        if (mc.world == null || mc.player == null) return;

        if (legit.getValue()) {
            handleLegalMode();
        } else {
            handleNormalMode();
        }
    }

    private void handleNormalMode() {
        // NoPush provides immunity to knockback
        // In a real client, this would mixin to prevent knockback effects
        if (systemTime() - lastNoPush > 50) {
            // NoPush logic
            lastNoPush = systemTime();
        }
    }

    private void handleLegalMode() {
        // Legit NoPush: more humanized
        // Only apply immunity when taking knockback, not constantly
        if (systemTime() - lastNoPush > 200) {
            // Apply legit no-push logic
            lastNoPush = systemTime();
        }
    }

    private long systemTime() {
        return System.currentTimeMillis();
    }
}