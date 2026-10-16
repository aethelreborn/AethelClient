package aethel.client.api.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.utils.inventory.interaction.PlayerInteractionHelper;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Vape-style AutoFish: casts the rod, then reels purely on the real
 * server bite event (entity event 0) with a randomized human-like delay —
 * no timing prediction, no packet spam.
 */
public final class AutoFish extends Module {
    private static AutoFish instance;

    private final NumberSetting reelDelay = register(new NumberSetting("Reel Delay", "Milliseconds between the bite and the reel.", 250.0D, 50.0D, 800.0D, 10.0D));
    private final NumberSetting recastDelay = register(new NumberSetting("Recast Delay", "Milliseconds before the next cast.", 900.0D, 200.0D, 3000.0D, 50.0D));
    private final NumberSetting biteTimeout = register(new NumberSetting("Bite Timeout", "Seconds until an idle cast is reeled and re-cast.", 15.0D, 5.0D, 60.0D, 1.0D));
    private final BooleanSetting openWaterCheck = register(new BooleanSetting("Only Open Water", "Wait for the vanilla open-water bite table only.", false));

    private long castAtMs = -1L;
    private long reelAtMs = -1L;

    public AutoFish() {
        super("AutoFish", "Automatic fishing with real bite detection.", ModuleCategory.MISC);
        instance = this;
    }

    public static void onBite() {
        AutoFish module = instance;
        if (module == null || !module.isEnabled() || module.reelAtMs > 0L) {
            return;
        }
        long base = module.reelDelay.getValue().longValue();
        long jitter = ThreadLocalRandom.current().nextLong(0L, 201L);
        module.reelAtMs = System.currentTimeMillis() + base + jitter;
    }

    @Override
    protected void onDisable() {
        castAtMs = -1L;
        reelAtMs = -1L;
    }

    @Override
    public void onTick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null) {
            return;
        }
        if (!(client.player.getMainHandItem().getItem() instanceof FishingRodItem)) {
            return;
        }

        FishingHook hook = findOwnHook(client);
        long now = System.currentTimeMillis();

        if (hook != null) {
            if (openWaterCheck.getValue() && !hook.isOpenWaterFishing()) {
                // Fish never bites outside open water; wait silently.
                return;
            }
            if (reelAtMs > 0L && now >= reelAtMs) {
                reel(client);
                return;
            }
            if (reelAtMs < 0L && castAtMs > 0L && now - castAtMs > biteTimeout.getValue() * 1000.0D) {
                // Nothing ever bit: reel in and start over.
                reel(client);
            }
            return;
        }

        // No bobber in the world: either we just reeled (fish gone) or we are fresh.
        if (reelAtMs > 0L) {
            reelAtMs = -1L;
            castAtMs = now + randomizedRecast();
            return;
        }
        if (castAtMs < 0L || now >= castAtMs) {
            cast(client);
        }
    }

    private void cast(Minecraft client) {
        PlayerInteractionHelper.interactItem(InteractionHand.MAIN_HAND);
        long timeout = (long) (recastDelay.getValue() * 2.0D + biteTimeout.getValue() * 1000.0D);
        castAtMs = System.currentTimeMillis() + timeout;
    }

    private void reel(Minecraft client) {
        PlayerInteractionHelper.interactItem(InteractionHand.MAIN_HAND);
        reelAtMs = -1L;
        castAtMs = System.currentTimeMillis() + randomizedRecast();
    }

    private long randomizedRecast() {
        long base = recastDelay.getValue().longValue();
        long jitter = ThreadLocalRandom.current().nextLong(0L, Math.max(1L, base / 4L));
        return base + jitter;
    }

    private FishingHook findOwnHook(Minecraft client) {
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity instanceof FishingHook hook && hook.getPlayerOwner() == client.player) {
                return hook;
            }
        }
        return null;
    }
}
