package com.fiuba.apredazzi.tp_taller2_android.model;

import java.util.List;

/**
 * Created by apredazzi on 6/20/17.
 */

public class Activity {

    List<Artist> artists;
    List<Song> songs;

    public Activity(final List<Artist> artists, final List<Song> songs) {
        this.artists = artists;
        this.songs = songs;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(final List<Artist> artists) {
        this.artists = artists;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(final List<Song> songs) {
        this.songs = songs;
    }
}
