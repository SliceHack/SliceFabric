package com.sliceclient.command.commands;

import com.sliceclient.Slice;
import com.sliceclient.command.Command;
import com.sliceclient.command.data.CommandInfo;
import com.sliceclient.manager.module.ModuleManager;
import com.sliceclient.setting.Setting;
import com.sliceclient.setting.settings.BooleanValue;
import com.sliceclient.setting.settings.ModeValue;
import com.sliceclient.setting.settings.NumberValue;
import com.sliceclient.module.Module;

@CommandInfo(name = "setting", description = "Change settings")
public class CommandSetting extends Command {

    public CommandSetting(ModuleManager moduleManager) {
        moduleManager.getModules().forEach(module -> addAlias(module.getName()));
    }

    @Override
    public boolean handle(String name, String[] args) {
        if(name.equalsIgnoreCase("setting")) {
            addMessage("&cUsage: .<module> <setting> <value>");
            return false;
        }
        Module module = Slice.INSTANCE.getModuleManager().getModule(name);
        if(module == null) {
            addMessage("&cModule not found");
            return false;
        }
        if(args.length < 2) {
            addMessage("&cUsage: ." + name + " <setting> <value>");
            return false;
        }
        Setting setting = module.getSetting(args[0]);
        if(setting == null) {
            addMessage("&cSetting not found");
            return false;
        }

        if(setting instanceof ModeValue mode) {
            for(String s : mode.getValues()) {
                if(s.replace(" ", "").equalsIgnoreCase(args[1])) {
                    mode.setValue(s);
                    addMessage("&a" + args[0] + " set to " + s);
                    return true;
                }
            }
            addMessage("&cValue not found");
        }
        if(setting instanceof BooleanValue booleanValue) {
            boolean value;
            if(args[1] == null) value = !booleanValue.getValue();
            else value = Boolean.parseBoolean(args[1]);
            booleanValue.setValue(value);
            addMessage("&a" + args[0] + " set to " + value);
        }
        if(setting instanceof NumberValue numberValue) {
            double value;
            if(args[1] == null) {
                value = numberValue.getValue().doubleValue();
            } else {
                try {
                    value = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    addMessage("&cInvalid number");
                    return false;
                }
            }

            numberValue.setValue(value);
            addMessage("&a" + args[0] + " set to " + value);
        }
        return false;
    }
}
