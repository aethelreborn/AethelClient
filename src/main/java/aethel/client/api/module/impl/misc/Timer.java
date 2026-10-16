package aethel.client.api.module.impl.misc;

import aethel.client.manager.Manager;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.module.ModuleManager;
import aethel.client.api.settings.impl.NumberSetting;

/**
 * Vape-style Timer: makes the client tick faster (or slower) than the
 * server by scaling the game-tick delta. Movement, mining and projectile
 * simulation accelerate while the camera and UI animations stay normal.
 * The scale clamps to a sane range so the delta accumulator can never
 * spiral into an unstable state.
 */
public final class Timer extends Module {
    private final NumberSetting speed = register(new NumberSetting("Speed", "Tick speed multiplier.", 2.0D, 0.5D, 4.0D, 0.25D));

    public Timer() {
        super("Timer", "Speeds up or slows down the game tick rate.", ModuleCategory.MISC);
    }

    /**
     * Called from DeltaTrackerMixin on every frame; must never throw or
     * block from the render thread.
     */
    public static float speedMultiplier() {
        ModuleManager modules = Manager.getModules();
        if (modules == null) {
            return 1.0F;
        }
        Timer timer = modules.getByType(Timer.class).filter(Module::isEnabled).orElse(null);
        if (timer == null) {
            return 1.0F;
        }
        double value = Math.max(0.5D, Math.min(4.0D, timer.speed.getValue()));
        return (float) value;
    }
}
