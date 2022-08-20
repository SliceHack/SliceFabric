package com.sliceclient.notification;

import com.sliceclient.manager.notification.Type;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Notification {

    private String title, message;
    private double seconds;

    private boolean ran;

    public Notification(Type type, String message, double seconds) {
        this.title = type.getName();
        this.message = message;
        this.seconds = seconds;
    }

    public Notification(String type, String message, double seconds) {
        this.title = type;
        this.message = message;
        this.seconds = seconds;
    }

}
