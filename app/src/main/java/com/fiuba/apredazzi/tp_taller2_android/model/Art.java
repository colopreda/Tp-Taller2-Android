package com.fiuba.apredazzi.tp_taller2_android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apredazzi on 5/22/17.
 */

public class Art {

    @SerializedName("#text")
    String text;
    String size;

    public Art(final String text, final String size) {
        this.text = text;
        this.size = size;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getSize() {
        return size;
    }

    public void setSize(final String size) {
        this.size = size;
    }
}
