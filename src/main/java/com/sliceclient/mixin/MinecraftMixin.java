package com.sliceclient.mixin;

import com.sliceclient.util.TimerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {

    @Shadow @Final private RenderTickCounter renderTickCounter;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        TimerUtil.timer = renderTickCounter;
    }
}
