package com.sliceclient.module.modules.movement;

import com.sliceclient.event.data.EventInfo;
import com.sliceclient.event.events.EventUpdate;
import com.sliceclient.module.Module;
import com.sliceclient.module.data.Category;
import com.sliceclient.module.data.ModuleInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@ModuleInfo(name = "Fly", description = "Let's you fly around like a bird", category = Category.MOVEMENT, key = GLFW.GLFW_KEY_G)
public class Fly extends Module {

    @EventInfo
    public void onUpdate(EventUpdate e) {
    }
}
