package com.fiuba.apredazzi.tp_taller2_android.model;

import java.util.List;

/**
 * Created by apredazzi on 4/8/17.
 */

public class Song {

    private int id;
    private String name;
    private int album;
    private List<Artist> artists;
    private String url_art;

    public Song() {
    }

    public Song(final String name, final int album, final List<Artist> artist, final String url_art) {
        this.name = name;
        this.album = album;
        this.artists = artist;
        this.url_art = url_art;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(final String name) {
        this.name = name;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(final int album) {
        this.album = album;
    }

    public List<Artist> getArtist() {
        return artists;
    }

    public void setArtist(final List<Artist> artist) {
        this.artists = artist;
    }

    public String getUrl_art() {
        return url_art;
    }

    public void setUrl_art(final String url_art) {
        this.url_art = url_art;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
