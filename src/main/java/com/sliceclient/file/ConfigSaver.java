package com.sliceclient.file;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import org.json.JSONObject;
import com.sliceclient.Slice;
import com.sliceclient.manager.module.ModuleManager;
import com.sliceclient.module.Module;
import com.sliceclient.setting.Setting;
import com.sliceclient.setting.settings.BooleanValue;
import com.sliceclient.setting.settings.ModeValue;
import com.sliceclient.setting.settings.NumberValue;

import java.awt.*;
import java.io.*;

@Getter @Setter
@SuppressWarnings("all")
public class ConfigSaver {

    private File modules;
    private ModuleManager moduleManager;

    public ConfigSaver(String configName, ModuleManager moduleManager) {
        this.modules = new File(MinecraftClient.getInstance().runDirectory, "Slice\\configs\\" + configName + ".json");
        this.moduleManager = moduleManager;
    }

    public void load() {
        try {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(modules));
            } catch (FileNotFoundException e) {
                return;
            }

            String line;
            StringBuilder builder = new StringBuilder();
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            JSONObject json = new JSONObject(builder.toString());
            for(Module module : moduleManager.getModules()) {
                JSONObject moduleJson;
                try {
                    moduleJson = json.getJSONObject(module.getName());
                } catch (Exception e) {
                    return;
                }
                module.setEnabled(moduleJson.getBoolean("enabled"));

                JSONObject settingsJson = moduleJson.getJSONObject("settings");
                for(Setting key : module.getSettings()) {
                    if(!settingsJson.has(key.getName()))
                        return;

                    if(key instanceof BooleanValue) {
                        ((BooleanValue) key).setValue(settingsJson.getBoolean(key.getName()));
                    }
                    if(key instanceof ModeValue) {
                        ((ModeValue) key).setValue(settingsJson.getString(key.getName()));
                    }
                    if(key instanceof NumberValue) {
                        NumberValue value1 = (NumberValue) module.getSetting(key.getName());
                        Number value;
                        try {
                            switch (value1.getType()) {
                                case DOUBLE:
                                    value = settingsJson.getDouble(key.getName());
                                    break;
                                case FLOAT:
                                    value = settingsJson.getFloat(key.getName());
                                    break;
                                case LONG:
                                    value = settingsJson.getLong(key.getName());
                                    break;
                                default:
                                    value = settingsJson.getInt(key.getName());
                                    break;
                            }
                            value1.setValue(value);
                        } catch (Exception ignored){}
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if(!modules.getParentFile().exists()) modules.getParentFile().mkdirs();

        JSONObject json = new JSONObject();
        for(Module module : moduleManager.getModules()) {
            JSONObject moduleJson = new JSONObject();
            moduleJson.put("enabled", module.isEnabled());
            moduleJson.put("key", module.getKey());

            JSONObject settingsJson = new JSONObject();
            for(Setting key : module.getSettings()) {
                if(key instanceof BooleanValue) {
                    settingsJson.put(key.getName(), ((BooleanValue) key).getValue());
                }
                if(key instanceof ModeValue) {
                    settingsJson.put(key.getName(), ((ModeValue) key).getValue());
                }
                if(key instanceof NumberValue) {
                    settingsJson.put(key.getName(), ((NumberValue) key).getValue());
                }
            }
            moduleJson.put("settings", settingsJson);
            json.put(module.getName(), moduleJson);

            json.put("build", Slice.VERSION);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(modules));
                writer.write(json.toString());
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
