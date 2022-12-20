package com.sliceclient.command.commands;

import com.sliceclient.Slice;
import com.sliceclient.command.Command;
import com.sliceclient.command.data.CommandInfo;
import com.sliceclient.module.Module;
import org.lwjgl.glfw.GLFW;

@CommandInfo(name = "bind", description = "Binds a command to a key")
public class CommandBind extends Command {

    @Override
    public boolean handle(String name, String[] args) {
        if(args.length == 0) {
            addMessage("&cUsage: .bind <module> <key>");
            return true;
        }
        if(args.length == 1) {
            addMessage("&cUsage: .bind <module> <key>");
            return true;
        }

        Module module = Slice.INSTANCE.getModuleManager().getModule(args[0]);
        if(module == null) {
            addMessage("&cModule not found");
            return true;
        }
        int key = getGLFWKeyByName(args[1].toUpperCase());

        if(key == -1) {
            addMessage("&cInvalid key");
            return true;
        }

        args[1] = args[1].toUpperCase();
        addMessage("&aBound " + module.getName() + " to " + args[1]);
        module.setKey(key);
        return true;
    }

    public int getGLFWKeyByName(String name) {
        try {
            return GLFW.class.getField("GLFW_KEY_" + name).getInt(null);
        } catch (Exception e) {
            return -1;
        }

    }
}
