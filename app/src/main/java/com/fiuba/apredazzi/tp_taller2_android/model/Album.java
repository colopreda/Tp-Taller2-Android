package com.fiuba.apredazzi.tp_taller2_android.model;

import java.util.List;

/**
 * Created by apredazzi on 5/1/17.
 */

public class Album {

    private int id;
    private String name;
    private String release_date;
    private List<String> genres;
    private List<String> images;
    private List<Song> songs;

    public Album(final int id, final String name, final String release_date, final List<String> genres,
        final List<String> images,
        final List<Song> songs) {
        this.id = id;
        this.name = name;
        this.release_date = release_date;
        this.genres = genres;
        this.images = images;
        this.songs = songs;
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

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(final String release_date) {
        this.release_date = release_date;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(final List<String> genres) {
        this.genres = genres;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(final List<String> images) {
        this.images = images;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(final List<Song> songs) {
        this.songs = songs;
    }
}
