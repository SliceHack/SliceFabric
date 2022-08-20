package com.sliceclient.event.events;

import com.sliceclient.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class EventKey extends Event {
    private int key;
}
