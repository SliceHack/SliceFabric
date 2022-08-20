package com.sliceclient.manager.module;

import com.sliceclient.module.Module;
import com.sliceclient.module.modules.movement.Fly;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * ModuleManager class
 *
 * @author Nick
 * */
@Getter
public class ModuleManager {

    /** modules */
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        register(new Fly());
    }

    /**
     * Register a module
     *
     * @param module the module to register
     * */
    public void register(Module module) {
        modules.add(module);
    }
}
