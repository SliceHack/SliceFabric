package com.sliceclient.mixin;

import com.sliceclient.Slice;
import com.sliceclient.module.modules.render.FullBright;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.render.LightmapTextureManager.getBrightness;

@Mixin(LightmapTextureManager.class)
public class LightTextureMixin {
    @Shadow @Final
    private NativeImageBackedTexture texture;

    @Shadow @Final
    private NativeImage image;

    @Shadow
    private boolean dirty;

    @Shadow
    private float flickerIntensity;

    @Shadow @Final
    private GameRenderer renderer;

    @Shadow @Final
    private MinecraftClient client;


    @Inject(method = "update", at = @At("HEAD"))
    public void update(float delta, CallbackInfo ci) {
        if (this.dirty) {
            this.dirty = false;
            this.client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
                float f = clientWorld.getStarBrightness(1.0F);
                float g;
                if (clientWorld.getLightningTicksLeft() > 0) {
                    g = 1.0F;
                } else {
                    g = f * 0.95F + 0.05F;
                }

                float h = !Slice.INSTANCE.getModuleManager().getModule(FullBright.class).isEnabled() ? this.client.options.getDarknessEffectScale().getValue().floatValue() : 0;
                float i = this.getDarknessFactor(delta) * h;
                float j = this.getDarkness(this.client.player, i, delta) * h;

                if(this.client.player == null) return;

                float k = this.client.player.getUnderwaterVisibility();
                float l;
                if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    l = GameRenderer.getNightVisionStrength(this.client.player, delta);
                } else if (k > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    l = k;
                } else {
                    l = 0.0F;
                }

                Vec3f vec3f = new Vec3f(f, f, 1.0F);
                vec3f.lerp(new Vec3f(1.0F, 1.0F, 1.0F), 0.35F);
                float m = this.flickerIntensity + 1.5F;
                Vec3f vec3f2 = new Vec3f();

                for(int n = 0; n < 16; ++n) {
                    for(int o = 0; o < 16; ++o) {
                        float p = getBrightness(clientWorld.getDimension(), n) * g;
                        float q = getBrightness(clientWorld.getDimension(), o) * m;
                        float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float t = q * (q * q * 0.6F + 0.4F);
                        vec3f2.set(q, s, t);
                        boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
                        float u;
                        Vec3f vec3f4;
                        if (bl) {
                            vec3f2.lerp(new Vec3f(0.99F, 1.12F, 1.0F), 0.25F);
                            vec3f2.clamp(0.0F, 1.0F);
                        } else {
                            Vec3f vec3f3 = vec3f.copy();
                            vec3f3.scale(p);
                            vec3f2.add(vec3f3);
                            vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (this.renderer.getSkyDarkness(delta) > 0.0F) {
                                u = this.renderer.getSkyDarkness(delta);
                                vec3f4 = vec3f2.copy();
                                vec3f4.multiplyComponentwise(0.7F, 0.6F, 0.6F);
                                vec3f2.lerp(vec3f4, u);
                            }
                        }

                        float v;
                        if (l > 0.0F) {
                            v = Math.max(vec3f2.getX(), Math.max(vec3f2.getY(), vec3f2.getZ()));
                            if (v < 1.0F) {
                                u = 1.0F / v;
                                vec3f4 = vec3f2.copy();
                                vec3f4.scale(u);
                                vec3f2.lerp(vec3f4, l);
                            }
                        }

                        if (!bl) {
                            if (j > 0.0F) {
                                vec3f2.add(-j, -j, -j);
                            }

                            vec3f2.clamp(0.0F, 1.0F);
                        }

                        v = !Slice.INSTANCE.getModuleManager().getModule(FullBright.class).isEnabled() ? this.client.options.getGamma().getValue().floatValue() : 1000.0F;
                        Vec3f vec3f5 = vec3f2.copy();
                        vec3f5.modify(this::easeOutQuart);
                        vec3f2.lerp(vec3f5, Math.max(0.0F, v - i));
                        vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                        vec3f2.clamp(0.0F, 1.0F);
                        vec3f2.scale(255.0F);
                        int x = (int)vec3f2.getX();
                        int y = (int)vec3f2.getY();
                        int z = (int)vec3f2.getZ();
                        this.image.setColor(o, n, -16777216 | z << 16 | y << 8 | x);
                    }
                }

                this.texture.upload();
                this.client.getProfiler().pop();
            }
        }

    }

    @Shadow
    private float getDarknessFactor(float delta) {
        return 0;
    }

//    shadow easeOutQuart
    @Shadow
    private float easeOutQuart(float f) {
        return 0;
    }

    @Shadow
    private float getDarkness(LivingEntity entity, float darkness, float delta) {
        return 0;
    }


}
