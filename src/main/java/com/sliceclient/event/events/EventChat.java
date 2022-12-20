package com.sliceclient.event.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.sliceclient.event.Event;

@Getter @Setter
@AllArgsConstructor
public class EventChat extends Event {
    private String message;
}
