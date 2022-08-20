package com.sliceclient.module.modules.movement;

import com.sliceclient.event.data.EventInfo;
import com.sliceclient.event.events.EventUpdate;
import com.sliceclient.module.Module;
import com.sliceclient.module.data.Category;
import com.sliceclient.module.data.ModuleInfo;
import com.sliceclient.util.LoggerUtil;
import com.sliceclient.util.MoveUtil;
import com.sliceclient.util.TimerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@ModuleInfo(name = "Fly", description = "Let's you fly around like a bird", category = Category.MOVEMENT, key = GLFW.GLFW_KEY_G)
public class Fly extends Module {

    @EventInfo
    public void onUpdate(EventUpdate e) {
        if(mc.player == null) return;

        if(mc.options.jumpKey.isPressed()) mc.player.setVelocity(mc.player.getVelocity().getX(), mc.player.getVelocity().getY() + 4, mc.player.getVelocity().getZ());
        else if(mc.options.sneakKey.isPressed()) mc.player.setVelocity(mc.player.getVelocity().getX(), mc.player.getVelocity().getY() - 4, mc.player.getVelocity().getZ());
        else mc.player.setVelocity(mc.player.getVelocity().getX(), 0, mc.player.getVelocity().getZ());
        MoveUtil.strafe(4);
    }
}
