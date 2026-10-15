package aethel.client.mixin.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Invoker("startAttack")
    boolean aethel$startAttack();

    @Invoker("startUseItem")
    void aethel$startUseItem();

    @Invoker("updateLevelInEngines")
    void aethel$updateLevelInEngines(ClientLevel level);

    @Accessor("rightClickDelay")
    void aethel$setRightClickDelay(int delay);
}
