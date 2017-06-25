package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 4/19/17.
 */

public class UserSong {

    private boolean liked;
    private int rate;
    private int id;

    public UserSong(final boolean liked, final int rate) {
        this.liked = liked;
        this.rate = rate;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(final boolean liked) {
        this.liked = liked;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(final int rate) {
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
