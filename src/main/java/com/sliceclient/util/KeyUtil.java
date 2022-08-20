package com.sliceclient.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

/**
 * For Helping with Keys
 *
 * @author Nick
 * */
@UtilityClass
@SuppressWarnings("all")
public class KeyUtil {

    /**
     * returns all move keys
     * */
    public KeyBinding[] moveKeys() {
        return new KeyBinding[] {
                MinecraftClient.getInstance().options.forwardKey, MinecraftClient.getInstance().options.backKey,
                MinecraftClient.getInstance().options.leftKey, MinecraftClient.getInstance().options.rightKey,
                MinecraftClient.getInstance().options.jumpKey,
        };
    }

    /**
     * sneak key
     * */
    public KeyBinding sneak() {
        return MinecraftClient.getInstance().options.sneakKey;
    }

    /**
     * forward key
     * */
    public KeyBinding forward() {
        return MinecraftClient.getInstance().options.forwardKey;
    }

    /**
     * backward key
     * */
    public KeyBinding back() {
        return MinecraftClient.getInstance().options.backKey;
    }

    /**
     * left key
     * */
    public KeyBinding left() {
        return MinecraftClient.getInstance().options.leftKey;
    }

    /**
     * right key
     * */
    public KeyBinding right() {
        return MinecraftClient.getInstance().options.rightKey;
    }

    /**
     * jump key
     * */
    public KeyBinding jump() {
        return MinecraftClient.getInstance().options.jumpKey;
    }


}
