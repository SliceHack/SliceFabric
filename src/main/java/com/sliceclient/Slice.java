package com.sliceclient;

import com.sliceclient.event.data.EventInfo;
import com.sliceclient.event.events.EventKey;
import com.sliceclient.manager.event.EventManager;
import com.sliceclient.manager.module.ModuleManager;
import com.sliceclient.module.Module;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

/**
 * The Slice class
 *
 * @author Nick
 * */
@Getter
public enum Slice {
    INSTANCE;

    /** Minecraft instance */
    public final MinecraftClient mc = MinecraftClient.getInstance();

    /** managers */
    private final EventManager eventManager;
    private final ModuleManager moduleManager;

    Slice() {
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        eventManager.register(this);
    }

    @EventInfo
    public void onKey(EventKey e) {
        moduleManager.getModules().stream().filter(module -> module.getKey() == e.getKey()).forEach(Module::toggle);
    }
}
