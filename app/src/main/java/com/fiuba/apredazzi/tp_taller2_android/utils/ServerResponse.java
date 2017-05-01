package com.fiuba.apredazzi.tp_taller2_android.utils;

import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by apredazzi on 4/29/17.
 */

public class ServerResponse {

    @SerializedName("user")
    private User user;

    @SerializedName("users")
    private List<User> users;

    @SerializedName("artists")
    private List<Artist> artists;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(final List<User> users) {
        this.users = users;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(final List<Artist> artists) {
        this.artists = artists;
    }
}
