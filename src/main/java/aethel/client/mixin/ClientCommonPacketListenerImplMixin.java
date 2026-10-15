package aethel.client.mixin;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aethel.client.api.events.impl.PacketEvent;
import aethel.client.api.module.ModuleManager;
import aethel.client.manager.Manager;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ClientCommonPacketListenerImplMixin {
    @Inject(method = "handleResourcePackPush", at = @At("HEAD"), cancellable = true)
    private void aethel$onResourcePackPush(ClientboundResourcePackPushPacket packet, CallbackInfo ci) {
        PacketEvent event = Manager.postEvent(new PacketEvent(PacketEvent.Type.RECEIVE, packet));
        ModuleManager modules = Manager.getModules();
        boolean spoofed = modules != null
                && modules.getServerRPSpoofer()
                .map(spoofer -> spoofer.handleResourcePackPush((ClientCommonPacketListenerImpl) (Object) this, packet))
                .orElse(false);
        if (event.isCancelled() || spoofed) {
            ci.cancel();
        }
    }

    @Inject(method = "handleDisconnect", at = @At("HEAD"), cancellable = true)
    private void aethel$onDisconnect(ClientboundDisconnectPacket packet, CallbackInfo ci) {
        PacketEvent event = Manager.postEvent(new PacketEvent(PacketEvent.Type.RECEIVE, packet));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleKeepAlive", at = @At("HEAD"), cancellable = true)
    private void aethel$onKeepAlive(ClientboundKeepAlivePacket packet, CallbackInfo ci) {
        PacketEvent event = Manager.postEvent(new PacketEvent(PacketEvent.Type.RECEIVE, packet));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "handlePing", at = @At("HEAD"), cancellable = true)
    private void aethel$onPing(ClientboundPingPacket packet, CallbackInfo ci) {
        PacketEvent event = Manager.postEvent(new PacketEvent(PacketEvent.Type.RECEIVE, packet));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
