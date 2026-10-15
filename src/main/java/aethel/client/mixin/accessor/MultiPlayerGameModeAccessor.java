package aethel.client.mixin.accessor;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {
    @Accessor("isDestroying")
    void aethel$setDestroying(boolean isDestroying);

    @Accessor("isDestroying")
    boolean aethel$isDestroying();

    @Accessor("destroyBlockPos")
    BlockPos aethel$getDestroyBlockPos();

    @Invoker("ensureHasSentCarriedItem")
    void aethel$ensureHasSentCarriedItem();

    @Accessor("destroyDelay")
    void aethel$setDestroyDelay(int destroyDelay);

    @Accessor("destroyProgress")
    float aethel$getDestroyProgress();

    @Accessor("destroyProgress")
    void aethel$setDestroyProgress(float destroyProgress);
}
