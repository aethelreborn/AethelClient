package aethel.client.api.module.impl.movement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.impl.PlayerTravelEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;

/**
 * Vape-style SafeWalk: stops you from stepping off edges without forcing
 * the sneak key. Works by trimming raw travel input whenever every landing
 * corner of the next step would be unsupported.
 */
public final class SafeWalk extends Module {
    private final BooleanSetting onlyOnGround = register(new BooleanSetting("Only On Ground", "Only protect on-ground walking.", true));
    private final BooleanSetting keepSneak = register(new BooleanSetting("Keep Sneak", "Never interfere while sneaking.", true));

    private static final double LOOKAHEAD = 0.55D;
    private static final double HALF_WIDTH = 0.3D;

    public SafeWalk() {
        super("SafeWalk", "Stops you from walking off block edges.", ModuleCategory.MOVEMENT);
    }

    @SubscribeEvent
    private void onTravelPre(PlayerTravelEvent event) {
        if (!event.isPre() || mc.player == null || mc.level == null) {
            return;
        }
        if (keepSneak.getValue() && mc.player.isShiftKeyDown()) {
            return;
        }
        if (onlyOnGround.getValue() && !mc.player.onGround()) {
            return;
        }

        Vec3 input = event.getMotion();
        if (Math.abs(input.x) < 0.010D && Math.abs(input.z) < 0.010D) {
            return;
        }

        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();
        double stepX = input.x * LOOKAHEAD;
        double stepZ = input.z * LOOKAHEAD;

        if (!wouldFallOff(px + stepX, py, pz + stepZ)) {
            return;
        }

        // Slide along the edge: drop only the axis that actually leaves support.
        boolean xUnsupported = wouldFallOff(px + stepX, py, pz);
        boolean zUnsupported = wouldFallOff(px, py, pz + stepZ);
        double safeX = xUnsupported ? 0.0D : input.x;
        double safeZ = !xUnsupported && zUnsupported ? 0.0D : input.z;
        event.setMotion(new Vec3(safeX, input.y, safeZ));
    }

    /**
     * True when all four AABB corners of a position sit over air / liquid,
     * meaning stepping there would leave solid ground.
     */
    private boolean wouldFallOff(double x, double y, double z) {
        if (mc.player == null || mc.level == null) {
            return false;
        }
        return !supportedCorner(x - HALF_WIDTH, z - HALF_WIDTH, y)
                && !supportedCorner(x + HALF_WIDTH, z - HALF_WIDTH, y)
                && !supportedCorner(x - HALF_WIDTH, z + HALF_WIDTH, y)
                && !supportedCorner(x + HALF_WIDTH, z + HALF_WIDTH, y);
    }

    private boolean supportedCorner(double x, double z, double y) {
        if (mc.player == null || mc.level == null) {
            return true;
        }
        BlockPos pos = BlockPos.containing(x, y - 0.6D, z);
        BlockState state = mc.level.getBlockState(pos);
        return !state.isAir() && state.getFluidState().isEmpty();
    }
}
