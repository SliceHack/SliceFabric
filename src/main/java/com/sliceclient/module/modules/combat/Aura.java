package com.sliceclient.module.modules.combat;

import com.sliceclient.event.events.EventClientTick;
import com.sliceclient.util.LoggerUtil;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import com.sliceclient.Slice;
import com.sliceclient.event.data.EventInfo;
import com.sliceclient.event.events.EventUpdate;
import com.sliceclient.module.Module;
import com.sliceclient.module.data.Category;
import com.sliceclient.module.data.ModuleInfo;
import com.sliceclient.setting.settings.BooleanValue;
import com.sliceclient.setting.settings.ModeValue;
import com.sliceclient.setting.settings.NumberValue;

import java.util.Collection;

@ModuleInfo(name = "Aura", description = "Kills players around you!", key = GLFW.GLFW_KEY_R, category = Category.COMBAT)
@SuppressWarnings("all")
public class Aura extends Module {

    ModeValue mode = new ModeValue("Mode", "Switch", "Switch", "Lock");
    ModeValue blockMode = new ModeValue("Block Mode", "Vanilla", "Vanilla", "None", "NCP", "Fake");
    ModeValue rotateMode = new ModeValue("Rotation Mode", "Bypass", "Bypass", "Smooth", "None");

    NumberValue cps = new NumberValue("CPS", 8, 1, 20, NumberValue.Type.INTEGER);
    NumberValue range = new NumberValue("Range", 3.0, 0.2, 10.0, NumberValue.Type.DOUBLE);
    NumberValue rotateRange = new NumberValue("Rotate Range", 5.0, 0.2, 20.0, NumberValue.Type.DOUBLE);

    BooleanValue delay9 = new BooleanValue("1.9 Delay", true);
    BooleanValue throughWalls = new BooleanValue("Through Walls", false);

    BooleanValue keepSprint = new BooleanValue("KeepSprint", true);
    BooleanValue noSwing = new BooleanValue("NoSwing", false);
    BooleanValue invis = new BooleanValue("Invisible", true);
    BooleanValue players = new BooleanValue("Players", true);
    BooleanValue mobs = new BooleanValue("Mobs", true);
    BooleanValue teams = new BooleanValue("Teams", false);

    LivingEntity target, rotateTarget;

    public static boolean fakeBlock;

    /** for bypassing */
    private int deltaCps;
    private boolean reachedCps;

    /** smooth rotating */
    private float deltaYaw, deltaPitch;
    private boolean reachedYaw, reachedPitch, hasRotated, wait;

    private float yaw, pitch;
    private boolean ran;

    private int ticks = 0;

    public void onUpdateNoToggle(EventUpdate event) {
        if(!this.isEnabled() || (rotateTarget == null && target == null)) {
            deltaPitch = mc.player.getPitch();
            deltaYaw = mc.player.getYaw();
        }
    }

    public void onDisable() {
        deltaPitch = 0;
        ran = false;
        deltaYaw = 0;
        fakeBlock = false;
        Slice.INSTANCE.target = null;
        rotateTarget = null;
    }

    public void onEnable() {
        deltaPitch = mc.player.getYaw();
        deltaYaw = mc.player.getPitch();
    }

    @EventInfo
    public void onTick(EventClientTick e) {
        if(wait) {
            ticks++;
        }
        if(ticks >= 5) {
            if(target == null) { wait = false; ticks = 0; return; }
            attack();
            wait = false;
            ticks = 0;
        }
    }

    @EventInfo
    public void onUpdate(EventUpdate e) {

        if (rotateTarget == null) {
            deltaYaw = mc.player.getYaw();
            deltaPitch = mc.player.getPitch();
        }

        if(rotateTarget != null && !mode.getValue().equalsIgnoreCase("None")) {
            e.setYaw(yaw);
            e.setPitch(pitch);
        }

        if (rotateTarget != null) {
            float yaw, pitch;
            switch (rotateMode.getValue()) {
                case "Bypass":
                    yaw = getBypassRotate(rotateTarget)[0];
                    pitch = getBypassRotate(rotateTarget)[1];
                    break;
                case "Smooth":
                    yaw = getRotationsFixedSens(rotateTarget)[0];
                    pitch = getRotationsFixedSens(rotateTarget)[1];
                    break;
                case "None":
                    yaw = mc.player.getYaw();
                    pitch = mc.player.getPitch();
                default:
                    yaw = getRotationsFixedSens(rotateTarget)[0];
                    pitch = getRotationsFixedSens(rotateTarget)[1];
                    break;
            }

            if(yaw != mc.player.getYaw() && pitch != mc.player.getPitch() && yaw != this.yaw && pitch != this.pitch) {
                this.yaw = yaw;
                this.pitch = pitch;
            }

            if (!rotateMode.getValue().equalsIgnoreCase("Smooth")) {
                hasRotated = true;
            }
        }

        try {

            switch (mode.getValue()) {
                case "Switch":
                    target = swapAura(target, range.getValue().doubleValue());
                    break;
                case "Lock":
                    target = getTarget();
                    break;
            }

            if ((target == null || target.isDead() || target.getHealth() <= 0) && fakeBlock) {
                fakeBlock = false;
            }

            if(!throughWalls.getValue() && !mc.player.canSee(target)) {
                deltaPitch = mc.player.getPitch();
                deltaYaw = mc.player.getYaw();
                fakeBlock = false;
                Slice.INSTANCE.target = null;
                rotateTarget = null;
                return;
            }

            if (target == null) {
                reachedCps = false;
                reachedPitch = false;
                reachedYaw = false;
                deltaYaw = mc.player.getYaw();
                deltaPitch = mc.player.getPitch();
                deltaCps = 0;
                hasRotated = false;
            }

            if (target != null) {


                boolean block = mc.player.getInventory().getMainHandStack().getItem() != null && !blockMode.getValue().equalsIgnoreCase("None");

                fakeBlock = block && (blockMode.getValue().equalsIgnoreCase("Fake") || !(mc.player.getInventory().getMainHandStack().getItem() instanceof SwordItem));

                deltaCps = deltaCps == cps.getValue().intValue() ? (cps.getValue().intValue() != 1 ? (cps.getValue().intValue() - 1) : 1) : cps.getValue().intValue();

                double cps = this.cps.getValue().intValue();
                if (delay9.getValue()) {
                    cps = 4;
                }

                if (e.isPre()) {
                    if (!delay9.getValue() ? timer.hasReached((long) (1000 / cps)) && hasReached(target) : mc.player.getAttackCooldownProgress(0.0f) == 1.0F && mc.player.handSwingTicks == 0) {

                        if(delay9.getValue()) wait = mc.player.getAttackCooldownProgress(0.0f) == 1.0F;

                        if(!delay9.getValue()) {
                            attack();
                        }
                    }
                }
            }
        } catch(Exception ignored){}
        switch (mode.getValue()) {
            case "Switch":
                rotateTarget = swapAuraRotate(rotateTarget, rotateRange.getValue().doubleValue());
                break;
            case "Lock":
                rotateTarget = getRotateTarget();
                break;
        }

        if(rotateTarget == null) {
            hasRotated = false;
        }
    }

    public boolean hasReached(LivingEntity target) {
        return  mode.getValue().equalsIgnoreCase("Smooth") ? ran : true;
    }

    public boolean isRoughlyEqual(float a, float b) {
        return Math.abs(a - b) < 2.2;
    }

    public LivingEntity swapAura(LivingEntity target, double ranage) {
        if(target != null) {
            if(target.distanceTo(mc.player) > ranage || (target.isDead() || target.getHealth() <= 0)) {
                return target = null;
            }
        }
        target = getTarget();

        return target;
    }

    public LivingEntity swapAuraRotate(LivingEntity target, double ranage) {
        if(target != null) {
            if(target.distanceTo(mc.player) > ranage || (target.isDead() || target.getHealth() <= 0)) {
                return target = null;
            }
        }
        target = getRotateTarget();

        return target;
    }


    private void attack() {
        if (!delay9.getValue() && !noSwing.getValue()) mc.player.swingHand(Hand.MAIN_HAND);

        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));

        if (delay9.getValue() && !noSwing.getValue()) mc.player.swingHand(Hand.MAIN_HAND);
        mc.player.resetLastAttackedTicks();
    }

    public float[] getBypassRotate(Entity e) {
        double deltaX = e.getX() + (e.getX() - e.lastRenderX) - mc.player.getX(),
                deltaY = e.getY() - 3.5 + e.getEyeHeight(e.getPose()) - mc.player.getY() + mc.player.getEyeHeight(e.getPose()),
                deltaZ = e.getZ() + (e.getZ() - e.lastRenderZ) - mc.player.getZ(),
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX - deltaZ)),
                pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        if(deltaX < 0 && deltaZ < 0) yaw = (float) (90 + (Math.toDegrees(Math.atan(deltaZ / deltaX))));
        if(deltaX > 0 && deltaZ < 0) yaw = (float) (-90 + (Math.toDegrees(Math.atan(deltaZ / deltaX))));
        if(deltaX < 0 && deltaZ > 0) yaw = (float) (90 + (Math.toDegrees(Math.atan(deltaZ / deltaX))));
        if(deltaX > 0 && deltaZ > 0) yaw = (float) (-90 + (Math.toDegrees(Math.atan(deltaZ / deltaX))));

        if(deltaX == 0 && deltaZ < 0) yaw = -90;
        if(deltaX == 0 && deltaZ > 0) yaw = 90;
        if(deltaX < 0 && deltaZ == 0) yaw = 180;
        if(deltaX > 0 && deltaZ == 0) yaw = 0;
        
        if(pitch > 90) pitch = 90;
        if(pitch < -90) pitch = -90;

        return new float[]{yaw, pitch};
    }

    public float[] getRotate(Entity e) {
        double x = e.getX() - mc.player.getX();
        double y = e.getY() - mc.player.getY();
        double z = e.getZ() - mc.player.getZ();
        double dist = Math.sqrt(x * x + y * y + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(y, dist) * 180.0D / Math.PI);
        reachedYaw = true;
        reachedPitch = true;
        return new float[] {yaw, pitch};
    }

    @SuppressWarnings("all")
    public LivingEntity getTarget() {
        double dist = range.getValue().doubleValue();
        LivingEntity target = null;
        for (Object object : mc.world.getEntities()) {
            Entity entity = (Entity) object;
            if (entity instanceof LivingEntity) {
                LivingEntity player = (LivingEntity) entity;
                if (canAttack(player)) {
                    double currentDist = mc.player.distanceTo(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = player;
                    }
                }
            }
        }
        Slice.INSTANCE.target = target;
        return target;
    }

    @SuppressWarnings("all")
    public LivingEntity getRotateTarget() {
        double dist = rotateRange.getValue().doubleValue();
        LivingEntity target = null;
        for (Object object : mc.world.getEntities()) {
            Entity entity = (Entity) object;
            if (entity instanceof LivingEntity) {
                LivingEntity player = (LivingEntity) entity;
                if (canAttack(player)) {
                    double currentDist = mc.player.distanceTo(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = player;
                    }
                }
            }
        }
        rotateTarget = target;
        Slice.INSTANCE.target = target;
        return target;
    }

    public float[] getRotationsFixedSens(Entity e) {
        float yaw = getBypassRotate(e)[0];
        float pitch = getBypassRotate(e)[1];

        int smooth = 2;

        if (deltaPitch < pitch) deltaPitch += Math.abs(pitch - deltaPitch) / smooth;
        if(deltaPitch > pitch) deltaPitch -= Math.abs(pitch - deltaPitch) / smooth;

        if (deltaYaw < yaw) deltaYaw += Math.abs(yaw - deltaYaw) / smooth;
        if(deltaYaw > yaw) deltaYaw -= Math.abs(yaw - deltaYaw) / smooth;

        ran = (((int)deltaPitch - (int)pitch) < 2) && (((int)deltaYaw - (int)yaw) < 2);
        hasRotated = ran;


        return new float[] { deltaYaw+(float) Math.random(), deltaPitch };
    }

    public boolean canAttack(LivingEntity entity) {
        boolean player = players.getValue();
        boolean invis = this.invis.getValue();
        boolean animal = mobs.getValue();
        boolean monster = mobs.getValue();
        boolean villager = mobs.getValue();
        boolean team = teams.getValue();

        float reach = (float) range.getValue().doubleValue();

        if(entity instanceof PlayerEntity || entity instanceof AnimalEntity || entity instanceof MobEntity || entity instanceof VillagerEntity) {
            if(entity instanceof PlayerEntity && !player) {
                return false;
            }
            if(entity instanceof AnimalEntity && !animal) {
                return false;
            }
            if(entity instanceof MobEntity && !monster) {
                return false;
            }
            if(entity instanceof VillagerEntity && !villager) {
                return false;
            }
        }
//        Collection<String> playerTeam = entity.getScoreboardTeam().getPlayerList();
//        if(playerTeam.contains(entity) && team) {
//            return false;
//        }
        if(entity.isInvisible() && !invis) {
            return false;
        }
        return entity != mc.player && entity.isAlive() && !(entity instanceof ArmorStandEntity && entity.isInvisible());
    }

}
