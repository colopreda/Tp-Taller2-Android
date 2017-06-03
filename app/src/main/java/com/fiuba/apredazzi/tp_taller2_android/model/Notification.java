package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 5/21/17.
 */

public class Notification {

    String sender_id;
    String message;

    public Notification(final String sender_id, final String message) {
        this.sender_id = sender_id;
        this.message = message;
    }

    public Notification(final String message) {
        this.message = message;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(final String sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
