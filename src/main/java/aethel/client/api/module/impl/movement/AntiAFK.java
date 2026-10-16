package aethel.client.api.module.impl.movement;

import net.minecraft.client.Minecraft;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.api.settings.impl.MultiModeSetting;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Anti-AFK keeps the session active by periodically performing human-like
 * idle actions (jumping / swinging) so server AFK kickers stay asleep.
 */
public final class AntiAFK extends Module {
    private final MultiModeSetting actions = register(new MultiModeSetting("Actions", "Idle actions to perform.", new String[]{"Jump", "Swing"}, "Jump", "Swing"));
    private final NumberSetting minDelay = register(new NumberSetting("Min Delay", "Minimum seconds between actions.", 4.0D, 1.0D, 30.0D, 1.0D));
    private final NumberSetting maxDelay = register(new NumberSetting("Max Delay", "Maximum seconds between actions.", 9.0D, 2.0D, 60.0D, 1.0D));
    private final BooleanSetting onlyOwnMotion = register(new BooleanSetting("Pause On Input", "Pause when the player is moving themselves.", false));

    private int ticksUntilAction;

    public AntiAFK() {
        super("AntiAFK", "Prevents AFK kicks with idle jump and swing actions.", ModuleCategory.MISC);
    }

    @Override
    public void onTick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null) {
            return;
        }
        if (onlyOwnMotion.getValue() && client.input.keyUp.isDown()) {
            return;
        }

        if (ticksUntilAction <= 0) {
            ticksUntilAction = secondsToTicks(minDelay.getValue(), maxDelay.getValue());
            perform(client);
            return;
        }
        ticksUntilAction--;
    }

    private void perform(Minecraft client) {
        if (actions.isSelected("Jump")) {
            client.player.jumpFromGround();
        }
        if (actions.isSelected("Swing")) {
            client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
    }

    private int secondsToTicks(double min, double max) {
        double low = Math.min(min, max);
        double high = Math.max(min, max);
        int minTicks = Math.max(1, (int) Math.round(low * 20.0D));
        int maxTicks = Math.max(minTicks, (int) Math.round(high * 20.0D));
        return ThreadLocalRandom.current().nextInt(minTicks, maxTicks + 1);
    }
}
