package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 4/6/17.
 */

public class FBUser {

    private String user_id;
    private String authToken;

    public FBUser(String user_id, String authToken) {
        this.user_id = user_id;
        this.authToken = authToken;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(final String user_id) {
        this.user_id = user_id;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
    }
}
