package com.sliceclient.setting;

import com.sliceclient.module.Module;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Setting {
    private String name;
    private boolean hidden;
    private Module module;

    public Setting(String name) {
        this.name = name;
    }

    public void setVisible(boolean visible) {
        setHidden(!visible);
    }
}
