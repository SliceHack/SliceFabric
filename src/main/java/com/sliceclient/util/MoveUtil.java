package com.sliceclient.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Player movement utility class
 *
 * @author Nick
 * */
@UtilityClass
@SuppressWarnings("all")
public class MoveUtil {

    /**
     * Positions
     */
    public static double x, y, z, lastX, lastY, lastZ;
    public static float yaw, pitch, lastYaw, lastPitch;

    /**
     * Checks if the player is moving
     */
    public boolean isMoving() {
        return MinecraftClient.getInstance().player != null && (MinecraftClient.getInstance().player.forwardSpeed != 0.0F || MinecraftClient.getInstance().player.upwardSpeed != 0.0F || MinecraftClient.getInstance().player.sidewaysSpeed != 0.0F);
    }

    /**
     * Stops the player from moving
     */
    public void stop() {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.setVelocity(0.0D, MinecraftClient.getInstance().player.getVelocity().getY(), 0.0D);

            for (KeyBinding key : KeyUtil.moveKeys()) key.setPressed(false);
        }
    }

    /**
     * Resets the player's motion to 0
     */
    public void resetMotion(boolean yPosition) {
        if (MinecraftClient.getInstance().player == null) return;

        MinecraftClient.getInstance().player.setVelocity(0.0D, !yPosition ? MinecraftClient.getInstance().player.getVelocity().getY() : 0, 0.0D);
    }

    /**
     * Sets a player's speed
     *
     * @parma speed - the speed and friction to apply to the player
     **/
    public void strafe(final double speed) {
        if (MinecraftClient.getInstance().player == null) return;

        if (!isMoving())
            return;

        final double yaw = RotationUtil.getDirection(), y = MinecraftClient.getInstance().player.getVelocity().getY();
        double x = -Math.sin(yaw) * speed, z = Math.cos(yaw) * speed;

        MinecraftClient.getInstance().player.setVelocity(x, y, z);
    }

    /**
     * Gets the player's bps
     * */
    public double getBPS() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return MathUtil.round(((Math.hypot(x - lastX, z - lastZ) * TimerUtil.timer.tickDelta) * 20), 2);
    }

    /**
     * Jumps the player
     * without Minecraft accelerating to high
     */
    public void jump() {
        if(MinecraftClient.getInstance().player == null) return;

        MinecraftClient.getInstance().player.addVelocity(0.0D, 0.24D, 0.0D);
    }

    /**
     * @see #strafe(double)
     */
    public void strafe() {
        if(MinecraftClient.getInstance().player == null) return;
        if(MinecraftClient.getInstance().player.hurtTime > 5) return;

        strafe(getSpeed());
    }

    /**
     * Gets how fast a player is moving
     * */
    public double getSpeed() {
        if(MinecraftClient.getInstance().player == null) return -1;

        double motionX = MinecraftClient.getInstance().player.getVelocity().getX();
        double motionZ = MinecraftClient.getInstance().player.getVelocity().getZ();
        return Math.hypot(motionX, motionZ);
    }

    /**
     * Checks if the player is collied
     * */
    public static boolean isCollided() {
        if(MinecraftClient.getInstance().player == null) return false;

        return MinecraftClient.getInstance().player.collidedSoftly;
    }
}
