package com.sliceclient.manager.event;

import com.sliceclient.event.Event;
import com.sliceclient.event.data.EventInfo;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Runs an event.
 *
 * @author Nick
 */
public record EventSender(Object object, Event event) {

    public EventSender(Object object, Event event) {
        this.object = object;
        this.event = event;
        this.run();
    }

    public void run() {
        for(Method method : object.getClass().getMethods()) {
            if(hasEventInfo(method)
                    && method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                try {
                    method.invoke(object, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean hasEventInfo(Method method) {
        return method.getAnnotation(EventInfo.class) != null;
    }
}
