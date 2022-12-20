package com.sliceclient.command.commands;

import com.sliceclient.Slice;
import com.sliceclient.command.Command;
import com.sliceclient.command.data.CommandInfo;
import com.sliceclient.module.Module;

@CommandInfo(name = "toggle", description = "Toggles a module", aliases = {"t"})
public class CommandToggle extends Command {

    @Override
    public boolean handle(String name, String[] args) {
        if(args.length < 1) {
            addMessage("Usage: .toggle <module>");
            return false;
        }
        Module module = Slice.INSTANCE.getModuleManager().getModule(args[0]);
        if(module == null) {
            addMessage("Module not found");
            return false;
        }
        module.toggle();
        addMessage("Module " + module.getName() + " is now " + (module.isEnabled() ? "enabled" : "disabled"));
        return false;
    }
}
