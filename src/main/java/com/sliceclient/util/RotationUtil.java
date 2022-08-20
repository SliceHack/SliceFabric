package com.sliceclient.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

/**
 * Easier rotation methods without doing alot of math
 *
 * @author Nick
 * */
@UtilityClass
@SuppressWarnings("all")
public class RotationUtil {

    public static float prevHeadPitch, headPitch;

    /**
     * Gets the direction the player is facing
     * */
    public static double getDirection() {
        float rotationYaw = MinecraftClient.getInstance().player.getYaw();

        if (MinecraftClient.getInstance().player.forwardSpeed < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (MinecraftClient.getInstance().player.forwardSpeed < 0F) forward = -0.5F;
        else if (MinecraftClient.getInstance().player.forwardSpeed > 0F) forward = 0.5F;

        if (MinecraftClient.getInstance().player.sidewaysSpeed > 0F) rotationYaw -= 90F * forward;
        if (MinecraftClient.getInstance().player.sidewaysSpeed < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public boolean isInFov(Entity entity, double fov) {
        if(MinecraftClient.getInstance().player == null) return false;

        double xDiff = entity.getX() - MinecraftClient.getInstance().player.getX();
        double yDiff = entity.getY() - MinecraftClient.getInstance().player.getY();
        double zDiff = entity.getZ() - MinecraftClient.getInstance().player.getZ();
        double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
        double angle = Math.toDegrees(Math.acos(xDiff / distance));
        return angle < fov;
    }
}
