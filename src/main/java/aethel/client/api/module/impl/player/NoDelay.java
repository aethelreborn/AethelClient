package aethel.client.api.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.MultiModeSetting;
import aethel.client.mixin.accessor.LivingEntityAccessor;
import aethel.client.mixin.accessor.MinecraftAccessor;
import aethel.client.mixin.accessor.MultiPlayerGameModeAccessor;

public final class NoDelay extends Module {
    private static NoDelay instance;
    private final MultiModeSetting ignoreSetting = register(new MultiModeSetting("Type", "Client delays to remove.",
            new String[]{"Jump", "Right Click", "Break Delay", "Experience Bottle"},
            "Jump", "Experience Bottle"));

    public NoDelay() {
        super("No Delay", "Removes selected client delays.", ModuleCategory.PLAYER);
        instance = this;
    }

    public static NoDelay getInstance() {
        return instance;
    }

    @Override
    public void onTick(Minecraft client) {
        if (client.player == null) {
            return;
        }
        if (ignoreSetting.isSelected("Break Delay") && client.gameMode != null) {
            ((MultiPlayerGameModeAccessor) client.gameMode).aethel$setDestroyDelay(0);
        }
        if (ignoreSetting.isSelected("Jump")) {
            ((LivingEntityAccessor) client.player).aethel$setJumpingCooldown(0);
        }
        if (shouldBypassItemUseCooldown()) {
            ((MinecraftAccessor) client).aethel$setRightClickDelay(0);
        }
    }

    public boolean shouldBypassItemUseCooldown() {
        Minecraft client = Minecraft.getInstance();
        if (!isEnabled() || client.player == null) {
            return false;
        }
        if (ignoreSetting.isSelected("Right Click")) {
            return true;
        }
        return shouldBypassExperienceBottleUse();
    }

    public boolean shouldBypassExperienceBottleUse() {
        Minecraft client = Minecraft.getInstance();
        return isEnabled()
                && client.player != null
                && ignoreSetting.isSelected("Experience Bottle")
                && (client.player.getMainHandItem().getItem() == Items.EXPERIENCE_BOTTLE
                || client.player.getOffhandItem().getItem() == Items.EXPERIENCE_BOTTLE);
    }
}
