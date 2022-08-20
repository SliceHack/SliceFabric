package com.sliceclient.mixin;

import com.sliceclient.event.events.EventKey;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyBoardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (!(action != 1 || MinecraftClient.getInstance().currentScreen instanceof KeybindsScreen && ((KeybindsScreen)screen).lastKeyCodeUpdateTime > Util.getMeasuringTimeMs() - 20L)) {
            if(MinecraftClient.getInstance().world != null) {
                EventKey e = new EventKey(key);
                e.call();
            }
        }
    }

}
