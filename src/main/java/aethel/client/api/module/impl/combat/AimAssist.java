package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.api.settings.impl.ModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.api.settings.impl.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class AimAssist extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Aim assist mode", "Smooth", "Smooth", "Instant", "Legit"));
    private final NumberSetting fov = register(new NumberSetting("FOV", "Field of view", 360.0, 1.0, 720.0, 1.0));
    private final NumberSetting smoothness = register(new NumberSetting("Smoothness", "Aim smoothing", 1.0, 0.1, 10.0, 0.1));
    private final BooleanSetting autoFire = register(new BooleanSetting("AutoFire", "Auto-fire when aiming", false));
    private final BooleanSetting visCheck = register(new BooleanSetting("VisCheck", "Check if target is visible", false));

    private long lastAim = 0;

    public AimAssist() {
        super("AimAssist", "Assists aiming at nearby targets", ModuleCategory.COMBAT);
    }

    @Override
    public void onTick() {
        Minecraft mc = mc();
        if (mc.world == null || mc.player == null) return;

        if (systemTime() - lastAim < 50) return; // Debounce

        Entity target = findTarget();
        if (target == null) return;

        // Calculate aim direction
        double deltaX = target.getX() - mc.player.getX();
        double deltaY = target.getYEye() - mc.player.getYEye();
        double deltaZ = target.getZ() - mc.player.getZ();

        float targetYaw = (float) (Math.atan2(deltaZ, deltaX) * (double) (180 / Math.PI)) - 90.0F;
        float targetPitch = (float) (-Math.atan2(deltaY, (double) MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * (double) (180 / Math.PI));

        if (mode.is("Smooth")) {
            // Smooth aiming - interpolate toward target
            float currentYaw = mc.player.getYaw();
            float currentPitch = mc.player.getPitch();

            // Interpolate with smoothness factor
            float newYaw = currentYaw + (targetYaw - currentYaw) / smoothness.getValue();
            float newPitch = currentPitch + (targetPitch - currentPitch) / smoothness.getValue();

            // Apply rotations
            mc.player.setYaw(newYaw);
            mc.player.setPitch(MathHelper.clamp(newPitch, -90.0F, 90.0F));
        } else if (mode.is("Instant")) {
            // Instant aiming (less detectable in some contexts)
            mc.player.setYaw(targetYaw);
            mc.player.setPitch(MathHelper.clamp(targetPitch, -90.0F, 90.0F));
        } else if (mode.is("Legit")) {
            // Legit aiming with more natural variation
            float noise = (float) (Math.random() * 2.0 - 1.0);
            mc.player.setYaw(targetYaw + noise);
            mc.player.setPitch(MathHelper.clamp(targetPitch + noise, -90.0F, 90.0F));
        }

        if (autoFire.getValue() && mc.options.keyAttack.isPressed()) {
            // Auto-fire logic
            if (systemTime() - lastAim > 100) {
                // Simulate attack
                lastAim = systemTime();
            }
        }
    }

    private Entity findTarget() {
        // Find nearest entity within FOV
        Entity nearest = null;
        double bestDist = fov.getValue();

        for (Entity entity : mc.world.getEntitiesOfClass(Entity.class, mc.getBoundingBox().expand(fov.getValue()))) {
            if (entity == mc.player || entity.invulnerable) continue;
            
            // Simple check - in a full implementation would check visibility
            double dist = entity.squaredDistanceTo(mc.player);
            if (dist < bestDist * bestDist && entity instanceof LivingEntity) {
                bestDist = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

    private long systemTime() {
        return System.currentTimeMillis();
    }
}