package aethel.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aethel.client.api.module.impl.misc.AutoFish;

/**
 * AutoFish detection: server event 0 on a FishingHook is the bobber-bite
 * notification. The mixin only listens for it and hands the event to the
 * module when the bobber belongs to the local player.
 */
@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Inject(method = "handleEntityEvent", at = @At("HEAD"), require = 0)
    private void aethel$fishBite(byte id, CallbackInfo ci) {
        if (id != 0) {
            return;
        }
        FishingHook hook = (FishingHook) (Object) this;
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && hook.getPlayerOwner() == client.player) {
            AutoFish.onBite();
        }
    }
}
