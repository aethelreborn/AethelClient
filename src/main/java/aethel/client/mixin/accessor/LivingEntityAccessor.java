package aethel.client.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("jumping")
    boolean aethel$isJumping();

    @Accessor("noJumpDelay")
    int aethel$getJumpingCooldown();

    @Accessor("noJumpDelay")
    void aethel$setJumpingCooldown(int value);
}
