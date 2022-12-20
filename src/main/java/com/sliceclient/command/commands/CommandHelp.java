package com.sliceclient.command.commands;

import com.sliceclient.Slice;
import com.sliceclient.command.Command;
import com.sliceclient.command.data.CommandInfo;

@CommandInfo(name = "help", description = "Shows help for a command")
public class CommandHelp extends Command {

    @Override
    public boolean handle(String name, String[] args) {
        if(args.length == 0) {

            for(Command command : Slice.INSTANCE.getCommandManager().commands) {
                addMessage("." + command.getName() + ": " + command.getDescription());
            }
            return true;
        }

        try {
            Command command = Slice.INSTANCE.getCommandManager().getCommand(args[0]);
            addMessage("." + command.getName() + ": " + command.getDescription());
        } catch (Exception e) {
            try {
                for(Command command : Slice.INSTANCE.getCommandManager().commands) {

                    for(String s : command.getAliases()) {
                        if(s.equalsIgnoreCase(args[0])) {
                            addMessage("." + command.getName() + ": " + command.getDescription());
                            return true;
                        }
                    }
                }
            } catch (Exception e1) {
                addMessage("There is no command with the name " + args[0]);
            }
        }
        return true;
    }
}
