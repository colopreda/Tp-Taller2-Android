package com.fiuba.apredazzi.tp_taller2_android.model;

import java.util.List;

/**
 * Created by apredazzi on 6/6/17.
 */

public class Playlist {

    int id;
    String name;
    String description;
    int userId;
    User user;
    List<Song> songs;

    public Playlist(final String name, final String description, final int userId, final User user,
        final List<Song> songs) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.user = user;
        this.songs = songs;
    }

    public Playlist(final String name, final String description, final int userId) {
        this.name = name;
        this.description = description;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(final List<Song> songs) {
        this.songs = songs;
    }
}
