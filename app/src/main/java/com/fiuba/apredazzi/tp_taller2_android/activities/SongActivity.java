package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androgeek.material.library.MaterialMusicPlayerView;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;

public class SongActivity extends BaseActivity {

    MaterialMusicPlayerView mpv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        mpv = (MaterialMusicPlayerView) findViewById(R.id.mpv);
        mpv.setCoverURL("http://i.ytimg.com/vi/y7HvUEPjbzU/hqdefault.jpg");

        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating())
                    mpv.stop();
                else
                    mpv.start();
            }
        });
    }

}
