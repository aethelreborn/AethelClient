package aethel.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import aethel.client.api.module.impl.misc.Timer;

/**
 * Timer support: scales the raw millisecond delta that feeds the game-tick
 * accumulator (DeltaTracker.Timer#advanceGameTime). Real-time deltas are
 * untouched, so camera smoothing, HUD and UI animations stay vanilla while
 * game actions tick at the configured rate.
 */
@Mixin(targets = "net.minecraft.client.DeltaTracker$Timer")
public class DeltaTrackerMixin {

    @ModifyArg(
            method = "advanceTime",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/DeltaTracker$Timer;advanceGameTime(J)I"
            ),
            require = 0,
            allow = 1
    )
    private long aethel$scaleGameTickDelta(long ms) {
        float speed = Timer.speedMultiplier();
        if (speed == 1.0F) {
            return ms;
        }
        return (long) (ms * speed);
    }
}
