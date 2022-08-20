package com.sliceclient.mixin;

import com.sliceclient.Slice;
import com.sliceclient.SliceMain;
import com.sliceclient.event.events.EventResize;
import com.sliceclient.util.TimerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.montoyo.mcef.MCEF;
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

    @Inject(method = "<init>", at = @At("TAIL"))
    public void run(CallbackInfo ci) {
        Slice.INSTANCE.init();
    }

    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    public void onResolutionChanged(CallbackInfo ci) {
        EventResize event = new EventResize(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight(), MinecraftClient.getInstance().getWindow().getScaleFactor());
        event.call();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        MCEF.onMinecraftShutdown();
    }

}
