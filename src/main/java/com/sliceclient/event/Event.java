package com.sliceclient.event;

import com.sliceclient.Slice;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Event {
    private boolean cancelled;

    public void call() {
        Slice.INSTANCE.getEventManager().runEvent(this);
    }
}
