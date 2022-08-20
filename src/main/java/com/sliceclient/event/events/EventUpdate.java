package com.sliceclient.event.events;

import com.sliceclient.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class EventUpdate extends Event {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround, pre;

    public boolean isPost() {
        return !pre;
    }
}
