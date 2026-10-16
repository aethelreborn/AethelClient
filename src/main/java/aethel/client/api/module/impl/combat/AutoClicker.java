package aethel.client.api.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.impl.TickEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.mixin.accessor.MinecraftAccessor;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.NumberSetting;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Legit-style auto clicker: simulates vanilla attack presses at a jittered
 * CPS rate and only fires when a target is actually under the crosshair.
 */
public final class AutoClicker extends Module {
    private static final int MAX_TICK_CPS = 20;

    private final NumberSetting cps = register(new NumberSetting("CPS", "Clicks per second.", 10.0D, 1.0D, 20.0D, 1.0D));
    private final BooleanSetting jitter = register(new BooleanSetting("Jitter", "Randomizes click timing for a human cadence.", true));
    private final BooleanSetting onlyTarget = register(new BooleanSetting("Only Target", "Only click when a crosshair hit is available.", false));
    private final BooleanSetting onlyWeapon = register(new BooleanSetting("Only Weapon", "Only click while holding a sword, axe, mace or trident.", false));
    private final BooleanSetting pauseUsing = register(new BooleanSetting("Pause While Using", "Pauses clicking while eating, drinking or blocking.", true));

    public AutoClicker() {
        super("AutoClicker", "Clicks for you at a jittered CPS rate.", ModuleCategory.COMBAT);
    }

    @SubscribeEvent
    private void onPostTick(TickEvent.Post event) {
        Minecraft client = event.getClient();
        if (skipTick(client)) {
            return;
        }

        double baseCps = Math.max(1.0D, Math.min(MAX_TICK_CPS, cps.getValue()));
        if (jitter.getValue()) {
            baseCps *= 0.85D + ThreadLocalRandom.current().nextDouble(0.3D);
        }
        // One tick equals up to 20 CPS; the chance of pressing this tick scales with cps.
        if (ThreadLocalRandom.current().nextDouble(MAX_TICK_CPS) > baseCps) {
            return;
        }
        if (onlyTarget.getValue() && client.crosshairPickEntity == null && client.hitResult == null) {
            return;
        }
        if (onlyWeapon.getValue() && !isWeapon(client.player.getMainHandItem().getItem())) {
            return;
        }

        MinecraftAccessor accessor = (MinecraftAccessor) client;
        if (accessor.aethel$startAttack()) {
            client.player.swing(InteractionHand.MAIN_HAND);
        }
    }

    private boolean skipTick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null) {
            return true;
        }
        if (pauseUsing.getValue() && client.player.isUsingItem()) {
            return true;
        }
        return !client.mouseHandler.isMouseGrabbed();
    }

    private boolean isWeapon(Item item) {
        return item instanceof SwordItem
                || item instanceof AxeItem
                || item instanceof MaceItem
                || item instanceof TridentItem;
    }
}
