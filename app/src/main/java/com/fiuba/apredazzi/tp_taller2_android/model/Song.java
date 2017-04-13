package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 4/8/17.
 */

public class Song {

    private String title;
    private String album;
    private String artist;
    private String url_art;

    public Song() {
    }

    public Song(final String title, final String album, final String artist, final String url_art) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.url_art = url_art;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public String getUrl_art() {
        return url_art;
    }

    public void setUrl_art(final String url_art) {
        this.url_art = url_art;
    }
}
