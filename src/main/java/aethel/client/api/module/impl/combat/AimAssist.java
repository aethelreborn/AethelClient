package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.api.settings.impl.ModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.api.settings.impl.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;

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
    public void onTick(Minecraft client) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        if (systemTime() - lastAim < 50) return; // Debounce

        Entity target = findTarget();
        if (target == null) return;

        // Calculate aim direction
        double deltaX = target.getX() - mc.player.getX();
        double deltaY = target.getEyeY() - mc.player.getEyeY();
        double deltaZ = target.getZ() - mc.player.getZ();

        float targetYaw = (float) (Math.atan2(deltaZ, deltaX) * (double) (180 / Math.PI)) - 90.0F;
        float targetPitch = (float) (-Math.atan2(deltaY, (double) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * (double) (180 / Math.PI));

        if (mode.is("Smooth")) {
            // Smooth aiming - interpolate toward target
            float currentYaw = mc.player.getYRot();
            float currentPitch = mc.player.getXRot();

            // Interpolate with smoothness factor
            float newYaw = (float) (currentYaw + (targetYaw - currentYaw) / smoothness.getValue());
            float newPitch = (float) (currentPitch + (targetPitch - currentPitch) / smoothness.getValue());

            // Apply rotations
            mc.player.setYRot(newYaw);
            mc.player.setXRot(Mth.clamp(newPitch, -90.0F, 90.0F));
        } else if (mode.is("Instant")) {
            // Instant aiming (less detectable in some contexts)
            mc.player.setYRot(targetYaw);
            mc.player.setXRot(Mth.clamp(targetPitch, -90.0F, 90.0F));
        } else if (mode.is("Legit")) {
            // Legit aiming with more natural variation
            float noise = (float) (Math.random() * 2.0 - 1.0);
            mc.player.setYRot(targetYaw + noise);
            mc.player.setXRot(Mth.clamp(targetPitch + noise, -90.0F, 90.0F));
        }

        if (autoFire.getValue() && mc.options.keyAttack.isDown()) {
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

        for (Entity entity : mc.level.getEntitiesOfClass(Entity.class, mc.player.getBoundingBox().inflate(fov.getValue()))) {
            if (entity == mc.player) continue;
            
            // Simple check - in a full implementation would check visibility
            double dist = entity.distanceToSqr(mc.player);
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