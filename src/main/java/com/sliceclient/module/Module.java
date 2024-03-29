package com.sliceclient.module;

import com.sliceclient.Slice;
import com.sliceclient.cef.RequestHandler;
import com.sliceclient.clickgui.HTMLGui;
import com.sliceclient.manager.notification.NotificationManager;
import com.sliceclient.manager.notification.Type;
import com.sliceclient.module.data.Category;
import com.sliceclient.module.data.ModuleInfo;
import com.sliceclient.notification.Notification;
import com.sliceclient.setting.Setting;
import com.sliceclient.setting.settings.ModeValue;
import com.sliceclient.util.Timer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;

import java.util.ArrayList;
import java.util.List;

/**
 * The Base of every Module
 *
 * @author Nick
 * */
@Getter @Setter
public class Module {

    /** module fields */
    protected MinecraftClient mc = MinecraftClient.getInstance();
    protected Timer timer = new Timer();

    /** Info */
    private final ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);

    /** the info */
    private String name, description;
    private int key;

    private Category category;

    /** toggle state*/
    private boolean enabled;

    /** settings */
    private List<Setting> settings = new ArrayList<>();

    public Module() {
        if(info == null) return;

        name = info.name();
        description = info.description();
        category = info.category();
        key = info.key();
    }

    public void onEnable() {}
    public void onDisable() {}

    public void startOnEnable() {
        Slice.INSTANCE.getEventManager().register(this);
        onEnable();
    }

    public void startOnDisable() {
        Slice.INSTANCE.getEventManager().unregister(this);
        onDisable();
    }

    /**
     * Toggles the module
     * */
    public void toggle() {
        enabled = !enabled;
        if(enabled) startOnEnable();
        else startOnDisable();

        if(enabled) RequestHandler.addToArrayList(getMode() != null ? name + " " + getMode().getValue() : name);
        else RequestHandler.removeFromArrayList(getMode() != null ? name + " " + getMode().getValue() : name);

        HTMLGui clickGui = Slice.INSTANCE.getClickGui();
        if(clickGui != null) {
            clickGui.setEnabled(name, enabled);
        }
    }

    /**
     * Gets the modules mode
     * */
    public ModeValue getMode() {
        return settings.stream().filter(setting -> (setting instanceof ModeValue && setting.getName().equalsIgnoreCase("mode"))).map(setting -> (ModeValue) setting).findFirst().orElse(null);
    }

    /**
     * Gets a setting by name
     * @param name the name of the setting
     *             - if the setting is not found, null is returned
     * */
    public Setting getSetting(String name) {
        return settings.stream().filter(setting -> setting.getName().replace(" ", "").equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
