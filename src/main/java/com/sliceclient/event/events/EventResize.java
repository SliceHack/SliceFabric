package com.sliceclient.event.events;

import com.sliceclient.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class EventResize extends Event {
    private int width, height;
    private double scaledWidth, scaledHeight;
    private double scaleFactor;
}
