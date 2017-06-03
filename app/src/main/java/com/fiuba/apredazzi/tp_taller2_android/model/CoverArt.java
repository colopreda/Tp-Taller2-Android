package com.fiuba.apredazzi.tp_taller2_android.model;

import java.util.List;

/**
 * Created by apredazzi on 5/22/17.
 */

public class CoverArt {
    List<Art> image;

    public CoverArt(final List<Art> image) {
        this.image = image;
    }

    public List<Art> getImage() {
        return image;
    }

    public void setImage(final List<Art> image) {
        this.image = image;
    }
}
