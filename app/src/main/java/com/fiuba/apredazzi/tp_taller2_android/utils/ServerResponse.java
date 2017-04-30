package com.fiuba.apredazzi.tp_taller2_android.utils;

import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by apredazzi on 4/29/17.
 */

public class ServerResponse {

    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
