package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 4/19/17.
 */

public class UserSong {

    private boolean liked;
    private int rank;

    public UserSong(final boolean liked, final int rank) {
        this.liked = liked;
        this.rank = rank;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(final boolean liked) {
        this.liked = liked;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }
}
