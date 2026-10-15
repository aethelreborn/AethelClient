package aethel.client.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import io.netty.channel.ChannelFutureListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aethel.client.api.events.impl.PacketEvent;
import aethel.client.manager.Manager;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void aethel$onSendPacket(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        PacketEvent event = Manager.postEvent(new PacketEvent(PacketEvent.Type.SEND, packet));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
