package com.sliceclient.mixin;

import com.sliceclient.event.events.EventUpdate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class PlayerMixin {

    @Shadow @Final
    protected MinecraftClient client;

    @Shadow
    private boolean lastSprinting, lastSneaking, autoJumpEnabled;
    @Shadow
    private double lastX, lastBaseY, lastZ;
    @Shadow
    private float lastYaw, lastPitch;
    @Shadow
    private boolean lastOnGround;
    @Shadow
    private int ticksSinceLastPositionPacketSent;

    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    @SuppressWarnings("all")

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    public void sendMovementPackets(CallbackInfo ci) {

        EventUpdate event = new EventUpdate(client.player.getX(), client.player.getY(), client.player.getZ(), client.player.getYaw(), client.player.getPitch(), client.player.isOnGround(), true);
        event.call();

        boolean bl2;
        boolean bl = client.player.isSprinting();
        if (bl != this.lastSprinting) {
            ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(client.player, mode));
            this.lastSprinting = bl;
        }
        if ((bl2 = client.player.isSneaking()) != this.lastSneaking) {
            ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(client.player, mode2));
            this.lastSneaking = bl2;
        }
        if (this.isCamera()) {
            boolean bl4;
            double d = event.getX() - this.lastX;
            double e = event.getY() - this.lastBaseY;
            double f = event.getZ() - this.lastZ;
            double g = event.getYaw() - this.lastYaw;
            double h = event.getPitch() - this.lastPitch;
            ++this.ticksSinceLastPositionPacketSent;
            boolean bl3 = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl5 = bl4 = g != 0.0 || h != 0.0;
            if (client.player.hasVehicle()) {
                Vec3d vec3d = client.player.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, event.getYaw(), event.getPitch(), event.isOnGround()));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.isOnGround()));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(event.getX(), event.getY(), event.getZ(), event.isOnGround()));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(event.getYaw(), event.getPitch(), event.isOnGround()));
            } else if (this.lastOnGround != event.isOnGround()) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(event.isOnGround()));
            }
            if (bl3) {
                this.lastX = event.getX();
                this.lastBaseY = event.getY();
                this.lastZ = event.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (bl4) {
                this.lastYaw = event.getYaw();
                this.lastPitch = event.getPitch();
            }
            this.lastOnGround = client.player.isOnGround();
            this.autoJumpEnabled = this.client.options.getAutoJump().getValue();
        }
        event = new EventUpdate(client.player.getX(), client.player.getY(), client.player.getZ(), client.player.getYaw(), client.player.getPitch(), client.player.isOnGround(), false);
        event.call();

        ci.cancel();
    }

    @Shadow
    protected boolean isCamera() {
        return client.getCameraEntity() == client.player;
    }

}
