package com.sliceclient.manager.module;

import com.sliceclient.module.Module;
import com.sliceclient.module.modules.combat.Aura;
import com.sliceclient.module.modules.movement.Fly;
import com.sliceclient.module.modules.movement.Speed;
import com.sliceclient.module.modules.render.FullBright;
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
        register(new Aura());
        register(new FullBright());
        register(new Speed());
    }

    /**
     * Register a module
     *
     * @param module the module to register
     * */
    public void register(Module module) {
        modules.add(module);
    }

    /**
     * Get a module by class
     *
     * @param clazz the class of the module
     * @return the module
     * */
    public Module getModule(Class<? extends Module> clazz) {
        return modules.stream().filter(module -> module.getClass().equals(clazz)).findFirst().orElse(null);
    }

    /**
     * Get a module by name
     *
     * @param name the name of the module
     * @return the module
     * */
    public Module getModule(String name) {
        return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }


    /**
     * Gets Aura Module
     *
     * @return Aura Module
     * */
    public Aura getAura() {
        return (Aura) getModule(Aura.class);
    }
}
