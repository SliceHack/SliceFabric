package com.sliceclient.module;

import com.sliceclient.Slice;
import com.sliceclient.module.data.Category;
import com.sliceclient.module.data.ModuleInfo;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;

/**
 * The Base of every Module
 *
 * @author Nick
 * */
@Getter @Setter
public class Module {

    /** module fields */
    protected MinecraftClient mc = MinecraftClient.getInstance();

    /** Info */
    private final ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);

    /** the info */
    private String name, description;
    private int key;

    private Category category;

    /** toggle state*/
    private boolean enabled;

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
    }


}
