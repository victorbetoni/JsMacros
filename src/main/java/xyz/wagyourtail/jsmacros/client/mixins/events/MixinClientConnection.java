package xyz.wagyourtail.jsmacros.client.mixins.events;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventRecvPacket;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventSendPacket;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Shadow
    private Channel channel;
    @Unique
    private EventRecvPacket eventRecvPacket;
    @Unique
    private EventSendPacket eventSendPacket;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (!channel.isOpen()) {
            return;
        }
        EventRecvPacket event = new EventRecvPacket(packet);
        if (event.isCanceled() || event.packet == null) {
            ci.cancel();
            return;
        }
        event.trigger();
        eventRecvPacket = event;
    }

    @ModifyArg(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V"), index = 0)
    public Packet<?> modifyReceivedPacket(Packet<?> packet) {
        return eventRecvPacket.packet;
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        EventSendPacket event = new EventSendPacket(packet);
        event.trigger();
        if (event.isCanceled() || event.packet == null) {
            ci.cancel();
            return;
        }
        eventSendPacket = event;
    }

    @ModifyVariable(method = "sendImmediately", at = @At(value = "LOAD"), ordinal = 0, argsOnly = true)
    public Packet<?> modifySendPacket(Packet<?> packet) {
        return eventSendPacket.packet;
    }

}
