package aethel.client.mixin;

import net.minecraft.client.renderer.SectionOcclusionGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;
import aethel.client.api.events.impl.ChunkOcclusionEvent;
import aethel.client.manager.Manager;

@Mixin(SectionOcclusionGraph.class)
public abstract class SectionOcclusionGraphMixin {
    @ModifyVariable(method = "update", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean aethel$disableAdvancedCulling(boolean advancedCulling) {
        ChunkOcclusionEvent event = Manager.postEvent(new ChunkOcclusionEvent());
        return event.isCancelled() ? false : advancedCulling;
    }
}
