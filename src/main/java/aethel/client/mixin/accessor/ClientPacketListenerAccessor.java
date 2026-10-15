package aethel.client.mixin.accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ClientPacketListener.class)
public interface ClientPacketListenerAccessor {
    @Accessor("level")
    void aethel$setLevel(ClientLevel level);

    @Accessor("levelData")
    void aethel$setLevelData(ClientLevel.ClientLevelData levelData);

    @Accessor("levelData")
    ClientLevel.ClientLevelData aethel$getLevelData();

    @Accessor("serverChunkRadius")
    void aethel$setServerChunkRadius(int serverChunkRadius);

    @Accessor("serverChunkRadius")
    int aethel$getServerChunkRadius();

    @Accessor("serverSimulationDistance")
    void aethel$setServerSimulationDistance(int serverSimulationDistance);

    @Accessor("serverSimulationDistance")
    int aethel$getServerSimulationDistance();

    @Accessor("levels")
    void aethel$setLevels(Set<ResourceKey<Level>> levels);
}
