package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.ModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;

public class Killaura extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Killaura mode", "Vanilla", "Vanilla", "Rotations", "Packet"));
    private final NumberSetting range = register(new NumberSetting("Range", "Attack range", 4.0, 1.0, 6.0, 0.1));
    private final NumberSetting rotationsPerSecond = register(new NumberSetting("Rotations", "Rotations per second", 6.0, 1.0, 20.0, 0.5));
    private final BooleanSetting motion = register(new BooleanSetting("Motion", "Apply motion", true));
    private final BooleanSetting legit = register(new BooleanSetting("Legit", "Legit mode (less detectable)", false));

    private long lastAttack = 0;
    private long lastRotation = 0;

    public Killaura() {
        super("Killaura", "Automatically attacks nearby entities", ModuleCategory.COMBAT);
    }

    @Override
    public void onTick(Minecraft client) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        if (legit.getValue()) {
            // Legit mode: slower, more humanized targeting
            handleLegitMode();
        } else {
            // Normal mode
            handleNormalMode();
        }
    }

    private void handleNormalMode() {
        if (systemTime() - lastAttack < 50) return; // Debounce

        Entity target = findTarget();
        if (target == null) return;

        // Calculate rotations
        double deltaX = target.getX() - mc.player.getX();
        double deltaY = target.getYEye() - mc.player.getYEye();
        double deltaZ = target.getZ() - mc.player.getZ();

        float yaw = (float) (Math.atan2(deltaZ, deltaX) * (double) (180 / Math.PI)) - 90.0F;
        float pitch = (float) (-Math.atan2(deltaY, (double) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * (double) (180 / Math.PI));

        // Apply with some randomness to avoid detection
        if (mc.options.keyAttack.isDown()) {
            // Set rotations with slight variation
            mc.player.setYRot(yaw + (float) (Math.random() * 2.0 - 1.0));
            mc.player.setXRot(pitch + (float) (Math.random() * 2.0 - 1.0));
            
            if (systemTime() - lastAttack > 200) {
                // Simulated attack (stub)
                lastAttack = systemTime();
            }
        }
    }

    private void handleLegitMode() {
        // More sophisticated legit killaura with humanized patterns
        // This would include random delays, variable rotations, etc.
        if (systemTime() - lastAttack > 200 + (int) (Math.random() * 100)) {
            Entity target = findTarget();
            if (target != null) {
                // In legit mode, we just set rotations smoothly
                // Actual attack would be handled by the player's click
                lastAttack = systemTime();
            }
        }
    }

    private Entity findTarget() {
        // Simple target selection - nearest living entity within range
        Entity nearest = null;
        double bestDist = range.getValue();

        for (Entity entity : mc.level.getEntitiesOfClass(Entity.class, mc.player.getBoundingBox().inflate(range.getValue()))) {
            if (entity == mc.player || entity.invulnerable) continue;
            if (entity instanceof LivingEntity) {
                double dist = entity.squaredDistanceTo(mc.player);
                if (dist < bestDist * bestDist) {
                    bestDist = dist;
                    nearest = entity;
                }
            }
        }
        return nearest;
    }

    private long systemTime() {
        return System.currentTimeMillis();
    }
}