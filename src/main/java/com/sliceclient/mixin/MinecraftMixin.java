package com.sliceclient.mixin;

import com.sliceclient.Slice;
import com.sliceclient.SliceMain;
import com.sliceclient.event.events.EventClientTick;
import com.sliceclient.event.events.EventResize;
import com.sliceclient.util.TimerUtil;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.client.ClientProxy;
import org.jetbrains.annotations.Nullable;
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
        EventClientTick event = new EventClientTick();
        event.call();
        TimerUtil.timer = renderTickCounter;
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(boolean tick, CallbackInfo ci) {
        ClientProxy proxy = (ClientProxy) MCEF.PROXY;
        proxy.onTickStart();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void run(CallbackInfo ci) {
        Slice.INSTANCE.init();
        EventResize event = new EventResize(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight(), MinecraftClient.getInstance().getWindow().getScaleFactor());
        event.call();
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
