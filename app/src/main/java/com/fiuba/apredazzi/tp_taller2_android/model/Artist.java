package com.fiuba.apredazzi.tp_taller2_android.model;

import java.util.List;

/**
 * Created by apredazzi on 4/30/17.
 */

public class Artist {

    private int id;
    private String name;
    private String description;
    private List<String> genres;
    private List<String> images;
    private List<User> followed;
    private int popularity;

    public Artist(final int id, final String name, final String description, final List<String> genres,
        final List<String> images, final int popularity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.genres = genres;
        this.images = images;
        this.popularity = popularity;
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

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(final int popularity) {
        this.popularity = popularity;
    }

    public List<User> getFollowed() {
        return followed;
    }

    public void setFollowed(final List<User> followed) {
        this.followed = followed;
    }
}
