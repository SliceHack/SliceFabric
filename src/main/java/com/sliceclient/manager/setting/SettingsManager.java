package com.sliceclient.manager.setting;

import com.sliceclient.manager.module.ModuleManager;
import com.sliceclient.setting.Setting;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter @Setter
public class SettingsManager {

    public SettingsManager(ModuleManager moduleManager) {
        moduleManager.getModules().forEach((module) -> {
            Field[] fields = module.getClass().getDeclaredFields();

            for(Field field : fields) {
                try {
                    if (field.getType().getSuperclass().equals(Setting.class)) {
                        try {
                            field.setAccessible(true);
                            Setting setting = (Setting) field.get(module);
                            setting.setModule(module);
                            module.getSettings().add(setting);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception ignored) {}
            }
        });
    }
}
