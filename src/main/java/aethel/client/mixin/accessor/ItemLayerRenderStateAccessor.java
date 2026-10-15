package aethel.client.mixin.accessor;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface ItemLayerRenderStateAccessor {
    @Accessor("foilType")
    ItemStackRenderState.FoilType aethel$getFoilType();

    @Accessor("transform")
    ItemTransform aethel$getItemTransform();

    @Accessor("usesBlockLight")
    boolean aethel$getUsesBlockLight();

    @Accessor("specialRenderer")
    SpecialModelRenderer<Object> aethel$getSpecialRenderer();

    @Accessor("tintLayers")
    int[] aethel$getTintLayers();
}
