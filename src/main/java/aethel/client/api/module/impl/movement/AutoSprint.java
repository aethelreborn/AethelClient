package aethel.client.api.module.impl.movement;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.api.settings.impl.ModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.api.settings.impl.BooleanSetting;
import net.minecraft.client.Minecraft;

public class AutoSprint extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "AutoSprint mode", "Vanilla", "Vanilla", "Sprint", "Packet", "Rotations"));
    private final BooleanSetting sprint = register(new BooleanSetting("Sprint", "Auto-sprint", true));
    private final BooleanSetting legit = register(new BooleanSetting("Legit", "Legit mode (less detectable)", false));

    private long lastSprint = 0;

    public AutoSprint() {
        super("AutoSprint", "Auto-sprint for you", ModuleCategory.MOVEMENT);
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
        // AutoSprint automatically sprints
        if (sprint.getValue() && !mc.player.isSprinting() && !mc.player.isShiftKeyDown()) {
            // Sprint logic
        }
    }

    private void handleLegalMode() {
        // Legit AutoSprint: more humanized
        // Only sprint when moving, not constantly
        if (systemTime() - lastSprint > 100) {
            // Apply legit auto-sprint logic
            lastSprint = systemTime();
        }
    }

    private long systemTime() {
        return System.currentTimeMillis();
    }
}