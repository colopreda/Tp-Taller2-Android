package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androgeek.material.library.MaterialMusicPlayerView;
import android.widget.FrameLayout;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import java.io.IOException;

public class SongActivity extends BaseActivity {

    MaterialMusicPlayerView mpv;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_song, null, false);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        frameLayout.addView(contentView);

//        mediaPlayer = MediaPlayer.create(this, R.raw.soy);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                mpv.setMax(mediaPlayer.getDuration() / 1000);
                mpv.start();
                mediaPlayer.start();
            }
        });

        try {
            mediaPlayer.setDataSource("http://slider.kz/download/170/cs8-2v4.vk-cdn.net/p17/61879d1f54447a/Galway%20-%20Galway%20Girl.mp3");
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mpv = (MaterialMusicPlayerView) findViewById(R.id.mpv);
        mpv.setCoverURL("http://i.ytimg.com/vi/y7HvUEPjbzU/hqdefault.jpg");

        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    mediaPlayer.pause();
                }
                else {
                    mpv.start();
                    mediaPlayer.start();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
