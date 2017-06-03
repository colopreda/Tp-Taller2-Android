package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 5/22/17.
 */

public class AlbumArt {

    CoverArt album;

    public AlbumArt(final CoverArt album) {
        this.album = album;
    }

    public CoverArt getAlbum() {
        return album;
    }

    public void setAlbum(final CoverArt album) {
        this.album = album;
    }
}
