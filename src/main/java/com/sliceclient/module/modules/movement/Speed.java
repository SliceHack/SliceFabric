package com.sliceclient.module.modules.movement;

import com.sliceclient.event.data.EventInfo;
import com.sliceclient.event.events.EventUpdate;
import com.sliceclient.module.Module;
import com.sliceclient.module.data.Category;
import com.sliceclient.module.data.ModuleInfo;
import com.sliceclient.util.MoveUtil;

@ModuleInfo(name = "Speed", description = "Lets you move faster", category = Category.MOVEMENT)
public class Speed extends Module {

    @EventInfo
    public void onUpdate(EventUpdate e) {
        if(mc.player == null || !MoveUtil.isMoving()) return;

        if(mc.player.isOnGround()) mc.player.jump();
        MoveUtil.strafe(0.5f);
    }
}
