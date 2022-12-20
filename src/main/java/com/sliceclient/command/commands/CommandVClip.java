package com.sliceclient.command.commands;

import com.sliceclient.command.Command;
import com.sliceclient.command.data.CommandInfo;
import net.minecraft.client.MinecraftClient;

@CommandInfo(name = "vclip", description = "Clips a player in the y axis.")
public class CommandVClip extends Command {

    MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public boolean handle(String name, String[] args) {
        try {
            if (args.length < 1) {
                addMessage("Usage: vclip <blocks>");
                return false;
            }
            double blocks = Double.parseDouble(args[0]);

            if(mc.player == null) {
                addMessage("??????????");
                return false;
            }

            mc.player.setPosition(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ());

            return true;
        } catch (Exception e) {
            addMessage("That was not a number. SMH.");
            return false;
        }
    }
}
