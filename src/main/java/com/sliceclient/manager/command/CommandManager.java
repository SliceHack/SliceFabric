package com.sliceclient.manager.command;

import com.sliceclient.manager.module.ModuleManager;
import com.sliceclient.command.Command;
import com.sliceclient.command.commands.*;
import com.sliceclient.event.events.EventChat;
import com.sliceclient.util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    public List<Command> commands = new ArrayList<>();

    public CommandManager(ModuleManager moduleManager) {
        register(new CommandSetting(moduleManager));
        register(new CommandToggle());
        register(new CommandBind());
        register(new CommandVClip());
        register(new CommandConfig());
//        register(new CommandPlugins());
        register(new CommandHelp());
        register(new CommandConnect());
//        register(new CommandReload());
//        register(new CommandJS());
    }

    public void register(Command command) {
        if(getCommand(command.getName()) != null) commands.remove(getCommand(command.getName()));

        commands.add(command);
    }

    /**
     * Used to handle commands
     * */
    public void handleChat(EventChat event) {
        String message = event.getMessage();
        if(message.startsWith(".")) {
            event.setCancelled(true);

            String command = message.split(" ")[0].replace(".", "");
            String[] args = message.split(" ");
            String[] newArgs = removeFirstArgument(args);

            for(Command command1 : commands) {
                if(command1.getName().equalsIgnoreCase(command)) {
                    command1.handle(command, newArgs);
                    return;
                }
                for(String alias : command1.getAliases()) {
                    if(alias.equalsIgnoreCase(command)) {
                        command1.handle(command, newArgs);
                        return;
                    }
                }
            }
            LoggerUtil.addMessage("Command " + command + " can not be found.");
        }
    }

    /**
     * Gets command by name
     * @param name - name of command
     * */
    public Command getCommand(String name) {
        return commands.stream().filter(command -> command.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Used to remove the first argument from the array
     * @param args the array of arguments to remove the first argument from
     * */
    private String[] removeFirstArgument(String[] args) {
        String[] newArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            newArgs[i - 1] = args[i];
        }
        return newArgs;
    }
}