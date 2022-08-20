package com.sliceclient.mixin;

import com.sliceclient.util.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
        if (entityType.getName().getString().equalsIgnoreCase(MinecraftClient.getInstance().getSession().getUsername())) {
            RotationUtil.headPitch = 0;
        }
    }
}
