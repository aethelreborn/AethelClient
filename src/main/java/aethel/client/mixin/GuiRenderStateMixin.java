package aethel.client.mixin;

import aethel.client.access.GuiRenderStateLayerAccessor;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderState.class)
public abstract class GuiRenderStateMixin implements GuiRenderStateLayerAccessor {
    @Unique
    private int aethel$layerSerial;

    @Override
    public int aethel$getLayerSerial() {
        return aethel$layerSerial;
    }

    @Inject(method = "nextStratum", at = @At("RETURN"))
    private void aethel$trackNextStratum(CallbackInfo ci) {
        aethel$layerSerial++;
    }

    @Inject(method = "up", at = @At("RETURN"))
    private void aethel$trackUpLayer(CallbackInfo ci) {
        aethel$layerSerial++;
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void aethel$resetLayerSerial(CallbackInfo ci) {
        aethel$layerSerial = 0;
    }
}
