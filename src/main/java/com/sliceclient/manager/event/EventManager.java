package com.sliceclient.manager.event;

import com.sliceclient.event.Event;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The event manager
 *
 * @author Nick
 * */
@Getter
public class EventManager {

    /** The list of every registered object */
    private final ArrayList<Object> registeredObjects = new ArrayList<>();

    /**
     * Runs an event
     *
     * @param event The event to run
     * */
    public void runEvent(Event event) {
        List<Object> registeredObjects = new ArrayList<>(this.registeredObjects);
        for(Object object : registeredObjects) new EventSender(object, event);
    }

    /**
     * Register an object to the event manager
     * @param object The object to register
     */
    public void register(Object object) {
        registeredObjects.add(object);
    }

    /**
     * Unregister an object from the event manager
     * @param object The object to unregister
     */
    public void unregister(Object object) {
        registeredObjects.remove(object);
    }
}