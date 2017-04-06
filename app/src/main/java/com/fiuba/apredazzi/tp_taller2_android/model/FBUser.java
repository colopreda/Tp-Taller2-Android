package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 4/6/17.
 */

public class FBUser {

    private String user_id;

    public FBUser(String user_id, String access_token) {
        this.user_id = user_id;
        this.access_token = access_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(final String user_id) {
        this.user_id = user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(final String access_token) {
        this.access_token = access_token;
    }

    private String access_token;
}
