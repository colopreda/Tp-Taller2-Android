package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 5/21/17.
 */

public class NotificationRequest {

    Notification data;
    String to;

    public NotificationRequest(final Notification data, final String to) {
        this.data = data;
        this.to = to;
    }

    public Notification getData() {
        return data;
    }

    public void setData(final Notification data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(final String to) {
        this.to = to;
    }

}
