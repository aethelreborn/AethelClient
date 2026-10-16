package aethel.client.api.module.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.NumberSetting;

/**
 * Vape-style Step: hops up single blocks automatically when running into
 * them. Only boosts when the blocking column is short enough for one
 * vanilla jump, so the appearance stays legit.
 */
public final class Step extends Module {
    private final NumberSetting maxBlockHeight = register(new NumberSetting("Max Height", "Tallest auto-step in blocks.", 1.0D, 0.5D, 2.0D, 0.25D));
    private final BooleanSetting onlyRunning = register(new BooleanSetting("Only Moving", "Only step while actively moving.", true));

    private int cooldown;

    public Step() {
        super("Step", "Automatically steps up single blocks.", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onTick(Minecraft client) {
        if (cooldown > 0) {
            cooldown--;
        }
        if (client.player == null || !client.player.onGround() || client.options == null) {
            return;
        }
        if (cooldown > 0 || client.screen != null || client.player.isInWater()
                || client.level.getBlockState(client.player.blockPosition()).is(BlockTags.CLIMBABLE)) {
            return;
        }
        if (onlyRunning.getValue() && !isMoving(client)) {
            return;
        }
        if (!client.player.horizontalCollision) {
            return;
        }

        double stepHeight = blockingColumnHeight(client);
        if (stepHeight <= 0.0D || stepHeight > maxBlockHeight.getValue() * 1.001D) {
            return;
        }
        if (!headroom(client, Math.ceil(stepHeight))) {
            return;
        }

        // Vanilla jump impulse — uses the full vanilla physics pipeline.
        client.player.setDeltaMovement(client.player.getDeltaMovement().x, 0.42D, client.player.getDeltaMovement().z);
        cooldown = 8;
    }

    private boolean isMoving(Minecraft client) {
        return client.player.getDeltaMovement().horizontalDistanceSqr() > 0.0030D
                || client.options.keyUp.isDown()
                || client.options.keyDown.isDown()
                || client.options.keyLeft.isDown()
                || client.options.keyRight.isDown();
    }

    /**
     * Height of the solid column directly in the direction of travel,
     * measured from the player's feet level.
     */
    private double blockingColumnHeight(Minecraft client) {
        var movement = client.player.getDeltaMovement();
        Direction direction = movement.x >= movement.z
                ? (movement.x >= 0.0D ? Direction.EAST : Direction.WEST)
                : (movement.z >= 0.0D ? Direction.SOUTH : Direction.NORTH);

        BlockPos feet = client.player.blockPosition().relative(direction);
        double feetY = client.player.getY();
        double height = 0.0D;
        for (int dy = 0; dy <= 2; dy++) {
            BlockPos pos = feet.above(dy);
            BlockState state = client.level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(client.level, pos);
            if (shape.isEmpty()) {
                continue;
            }
            double top = feetY + dy + shape.max(Direction.Axis.Y);
            if (top > feetY + 2.0D) {
                return -1.0D; // wall too tall for stepping
            }
            height = Math.max(height, top - feetY);
        }
        return height <= 0.001D ? -1.0D : height;
    }

    private boolean headroom(Minecraft client, double steps) {
        BlockPos above = client.player.blockPosition().above((int) Math.ceil(steps) + 1);
        return client.level.getBlockState(above).getCollisionShape(client.level, above).isEmpty();
    }
}
