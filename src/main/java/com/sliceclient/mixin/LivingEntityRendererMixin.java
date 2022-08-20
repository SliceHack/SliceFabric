package com.sliceclient.mixin;

import com.google.common.collect.Lists;
import com.sliceclient.util.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow protected M model;
    @Shadow @Final protected List<FeatureRenderer<T, M>> features = Lists.newArrayList();

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void render(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if(livingEntity == MinecraftClient.getInstance().player) {

            float n;
            Direction direction;
            matrixStack.push();
            this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, g);
            this.model.riding = livingEntity.hasVehicle();
            this.model.child = livingEntity.isBaby();
            float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
            float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
            float k = j - h;
            if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity) livingEntity.getVehicle();
                h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
                k = j - h;
                float l = MathHelper.wrapDegrees(k);
                if (l < -85.0f) {
                    l = -85.0f;
                }
                if (l >= 85.0f) {
                    l = 85.0f;
                }
                h = j - l;
                if (l * l > 2500.0f) {
                    h += l * 0.2f;
                }
                k = j - h;
            }
            float m = MathHelper.lerp(g, RotationUtil.prevHeadPitch, RotationUtil.headPitch);
            if (LivingEntityRenderer.shouldFlipUpsideDown(livingEntity)) {
                m *= -1.0f;
                k *= -1.0f;
            }
            if (livingEntity.isInPose(EntityPose.SLEEPING) && (direction = livingEntity.getSleepingDirection()) != null) {
                n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1f;
                matrixStack.translate((float) (-direction.getOffsetX()) * n, 0.0, (float) (-direction.getOffsetZ()) * n);
            }
            float l = this.getAnimationProgress(livingEntity, g);
            this.setupTransforms(livingEntity, matrixStack, l, h, g);
            matrixStack.scale(-1.0f, -1.0f, 1.0f);
            this.scale(livingEntity, matrixStack, g);
            matrixStack.translate(0.0, -1.501f, 0.0);
            n = 0.0f;
            float o = 0.0f;
            if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
                n = MathHelper.lerp(g, livingEntity.lastLimbDistance, livingEntity.limbDistance);
                o = (livingEntity.limbAngle - livingEntity.limbDistance * (1.0f - g));
                if (livingEntity.isBaby()) {
                    o *= 3.0f;
                }
                if (n > 1.0f) {
                    n = 1.0f;
                }
            }
            this.model.animateModel(livingEntity, o, n, g);
            this.model.setAngles(livingEntity, o, n, l, k, m);
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            boolean bl = this.isVisible(livingEntity);
            boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraftClient.player);
            boolean bl3 = minecraftClient.hasOutline(livingEntity);
            RenderLayer renderLayer = this.getRenderLayer(livingEntity, bl, bl2, bl3);
            if (renderLayer != null) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
                int p = LivingEntityRenderer.getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g));
                this.model.render(matrixStack, vertexConsumer, i, p, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
            }
            if (!livingEntity.isSpectator()) {
                for (FeatureRenderer<T, M> featureRenderer : this.features) {
                    featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, l, k, m);
                }
            }
            matrixStack.pop();
            ci.cancel(); // return;
        }

    }

    @Shadow protected RenderLayer getRenderLayer(T livingEntity, boolean bl, boolean bl2, boolean bl3) {
        return null;
    }

    @Shadow protected float getAnimationCounter(T entity, float tickDelta) {
        return 0.0f;
    }

    @Shadow protected boolean isVisible(T livingEntity) {
        return false;
    }

    @Shadow protected float getHandSwingProgress(T livingEntity, float tickDelta) {
        return 0.0f;
    }

    @Shadow protected void scale(T livingEntity, MatrixStack matrixStack, float tickDelta) {}

    @Shadow protected float getAnimationProgress(T entity, float tickDelta) {
        return 0.0f;
    }

    @Shadow protected void setupTransforms(T livingEntity, MatrixStack matrixStack, float tickDelta, float headYaw, float tickDelta2) {}

}
