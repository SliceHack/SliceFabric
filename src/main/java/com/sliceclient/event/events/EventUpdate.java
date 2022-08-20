package com.sliceclient.event.events;

import com.sliceclient.event.Event;
import com.sliceclient.util.RotationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;

@Getter @Setter
@AllArgsConstructor
public class EventUpdate extends Event {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround, pre;

    public boolean isPost() {
        return !pre;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        if(MinecraftClient.getInstance().player == null) return;

        MinecraftClient.getInstance().player.headYaw = yaw;
        MinecraftClient.getInstance().player.bodyYaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;

        if(MinecraftClient.getInstance().player == null) return;

        RotationUtil.headPitch = pitch;
    }
}
