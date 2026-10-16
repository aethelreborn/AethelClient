package aethel.client.api.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TridentItem;
import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.impl.TickEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.ModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.mixin.accessor.MultiPlayerGameModeAccessor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Ghost AutoClicker built to the Vape docs' behavior spec:
 * - CPS min/max band with three randomization tiers (Normal / Extra /
 *   Extra+): drift, micro-pauses and burst comp that emulate human cadence
 * - Hold to Click: only acts while the attack button is held
 * - Break Blocks: pauses with a randomized switch delay while mining
 * - Trigger Mode: only fires when something is under the crosshair
 */
public final class AutoClicker extends Module {
    private static final int MAX_TICK_CPS = 20;
    private static final TieredGenerator CADENCE = new TieredGenerator();

    private final NumberSetting cps = register(new NumberSetting("CPS", "Clicks per second.", 10.0D, 1.0D, 20.0D, 1.0D));
    private final ModeSetting randomization = register(new ModeSetting("Randomization", "Click cadence emulation to resist statistical detection.", "Extra", "Normal", "Extra", "Extra+"));
    private final BooleanSetting holdToClick = register(new BooleanSetting("Hold to Click", "Only click while the attack button is held.", false));
    private final BooleanSetting breakBlocks = register(new BooleanSetting("Break Blocks", "Pause while mining with a randomized switch delay.", true));
    private final NumberSetting breakDelay = register(new NumberSetting("Break Delay", "Random delay before switching to mining support.", 120.0D, 0.0D, 600.0D, 10.0D));
    private final BooleanSetting onlyTarget = register(new BooleanSetting("Trigger Mode", "Only click when a crosshair hit is available.", false));
    private final BooleanSetting onlyWeapon = register(new BooleanSetting("Limit Weapon", "Only click while holding a sword, axe, mace or trident.", false));
    private final BooleanSetting pauseUsing = register(new BooleanSetting("Pause While Using", "Pauses clicking while eating, drinking or blocking.", true));

    public AutoClicker() {
        super("AutoClicker", "Clicks for you with human-like cadence emulation.", ModuleCategory.COMBAT);
    }

    @SubscribeEvent
    private void onPostTick(TickEvent.Post event) {
        Minecraft client = event.getClient();
        long now = System.currentTimeMillis();

        if (skipTick(client)) {
            CADENCE.invalidate();
            return;
        }
        // Break Blocks support: mining has priority, with a randomized
        // switch delay so the transition is never machine-precise.
        if (breakBlocks.getValue()
                && !client.player.getMainHandItem().isEmpty()
                && isDestroying(client)) {
            CADENCE.invalidate();
            CADENCE.pauseUntil(now + breakDelay.getValue().longValue()
                    + ThreadLocalRandom.current().nextLong(0L, Math.max(1L, breakDelay.getValue().longValue() / 2L)));
            return;
        }
        if (CADENCE.pausedUntil(now)) {
            return;
        }
        if (holdToClick.getValue() && !client.options.keyAttack.isDown()) {
            CADENCE.invalidate();
            return;
        }

        double baseCps = Math.max(1.0D, Math.min(MAX_TICK_CPS, cps.getValue()));
        double effectiveCps = CADENCE.tick(baseCps, randomization.is("Extra+"));
        if (ThreadLocalRandom.current().nextDouble(MAX_TICK_CPS) > effectiveCps) {
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

    private boolean isDestroying(Minecraft client) {
        MultiPlayerGameModeAccessor accessor = (MultiPlayerGameModeAccessor) client.gameMode;
        return accessor != null && accessor.aethel$isDestroying();
    }

    private boolean skipTick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null || client.gameMode == null) {
            return true;
        }
        if (pauseUsing.getValue() && client.player.isUsingItem()) {
            return true;
        }
        return !client.mouseHandler.isMouseGrabbed();
    }

    private boolean isWeapon(Item item) {
        return item == Items.WOODEN_SWORD
                || item == Items.STONE_SWORD
                || item == Items.IRON_SWORD
                || item == Items.GOLDEN_SWORD
                || item == Items.DIAMOND_SWORD
                || item == Items.NETHERITE_SWORD
                || item instanceof AxeItem
                || item instanceof MaceItem
                || item instanceof TridentItem;
    }

    /**
     * Cadence emulation. Normal = plain randomization band; Extra = adds
     * micro-pauses; Extra+ = bursts with compensating slowdowns, i.e. the
     * acceleration/deceleration shape of a real hand clicking a fight.
     */
    private static final class TieredGenerator {
        private double driftedCps = Double.NaN;
        private int driftTimer;
        private int burstLeft;
        private int calmLeft;
        private long pauseEnd;

        boolean pausedUntil(long now) {
            return now < pauseEnd;
        }

        void pauseUntil(long timestamp) {
            pauseEnd = Math.max(pauseEnd, timestamp);
        }

        void invalidate() {
            pauseEnd = 0L;
        }

        double tick(double baseCps, boolean fullEmulation) {
            if (--driftTimer <= 0) {
                driftTimer = 10 + ThreadLocalRandom.current().nextInt(20);
                double spread = fullEmulation ? 0.40D : 0.30D;
                driftedCps = baseCps * (1.0D - spread / 2.0D + ThreadLocalRandom.current().nextDouble(spread));
            }
            double effective = driftedCps;

            if (fullEmulation) {
                if (burstLeft > 0) {
                    burstLeft--;
                    effective = driftedCps * 1.3D;   // burst phase
                    if (burstLeft == 0) {
                        calmLeft = 4 + ThreadLocalRandom.current().nextInt(8);
                    }
                } else if (calmLeft > 0) {
                    calmLeft--;                      // compensating slowdown
                    effective = driftedCps * 0.65D;
                } else if (ThreadLocalRandom.current().nextDouble() < 0.02D && effective > 4.0D) {
                    burstLeft = 3 + ThreadLocalRandom.current().nextInt(5);
                } else if (ThreadLocalRandom.current().nextDouble() < 0.015D) {
                    // micro-pause, like a hand adjusting grip mid-fight
                    pausedUntil(System.currentTimeMillis() + 80L + ThreadLocalRandom.current().nextLong(170L));
                }
            } else if (ThreadLocalRandom.current().nextDouble() < 0.012D) {
                pausedUntil(System.currentTimeMillis() + 60L + ThreadLocalRandom.current().nextLong(120L));
            }

            return effective;
        }
    }
}
