package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import androgeek.material.library.MaterialMusicPlayerView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.api.CoverService;
import com.fiuba.apredazzi.tp_taller2_android.api.SongsService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.model.AlbumArt;
import com.fiuba.apredazzi.tp_taller2_android.model.CoverArt;
import com.fiuba.apredazzi.tp_taller2_android.model.UserSong;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SongActivity extends BaseActivity {

    MaterialMusicPlayerView mpv;
    MediaPlayer mediaPlayer;

    TextView textViewSong;
    TextView textViewAditional;
    ProgressBar progress;
    View viewLoading;
    RatingBar ratingBar;
    ImageView heart;
    ImageView heartSelected;

    String album;
    String artist;
    String id;

    SongsService songsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_song, null, false);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        frameLayout.addView(contentView);

//        mediaPlayer = MediaPlayer.create(this, R.raw.soy);

        initializeService();

        checkPlayerRunning();

        setTitleTooblar("Sonando Ahora");

        textViewSong = (TextView) findViewById(R.id.textViewSong);
        textViewAditional = (TextView) findViewById(R.id.textViewSinger);
        progress = (ProgressBar) findViewById(R.id.progress_loading);
        viewLoading = findViewById(R.id.view_loading);
        heart = (ImageView) findViewById(R.id.like);
        heartSelected = (ImageView) findViewById(R.id.like_selected);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            textViewSong.setText(extras.getString("title", "No title"));
            artist = extras.getString("artist", "No artist");
            album = extras.getString("album", "No album");
            id = extras.getString("songid", "0");
            textViewAditional.setText(artist + " - " + album);
        }

        addListenerOnRatingBar();
        addListenerOnHeart();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CoverService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

        CoverService coverService = retrofit.create(CoverService.class);
        Call<AlbumArt> cover =
            coverService.getCoverArt("album.getinfo", CoverService.last_fm_api, artist, album, CoverService.format);
        cover.enqueue(new Callback<AlbumArt>() {
            @Override
            public void onResponse(final Call<AlbumArt> call, final Response<AlbumArt> response) {
                if (response.isSuccessful()) {
                    if (response.body().getAlbum() != null) {
                        String image = response.body().getAlbum().getImage().get(2).getText();
                        mpv.setCoverURL(image);
                    }
                }
            }

            @Override
            public void onFailure(final Call<AlbumArt> call, final Throwable t) {

            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                mpv.setMax(mediaPlayer.getDuration() / 1000);
                mpv.start();
                mediaPlayer.start();
                progress.setVisibility(View.GONE);
                viewLoading.setVisibility(View.GONE);
                mpv.setVisibility(View.VISIBLE);
            }
        });

        try {
            String url = "http://ec2-34-201-152-32.compute-1.amazonaws.com:8000/api/songs/" + id + ".mp3";
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mpv = (MaterialMusicPlayerView) findViewById(R.id.mpv);

        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    mediaPlayer.pause();
                } else {
                    mpv.start();
                    mediaPlayer.start();
                }
            }
        });
    }

    private void initializeService() {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String auth_token_string = settings.getString("auth_token", null);

        if (auth_token_string != null) {
            songsService = TokenGenerator.createService(SongsService.class, auth_token_string);
        }
    }

    private void addListenerOnHeart() {
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                heart.setVisibility(View.GONE);
                heartSelected.setVisibility(View.VISIBLE);
                Call<ResponseBody> likeRequest = songsService.likeSong(Integer.valueOf(id), new UserSong(true, ratingBar.getNumStars()));
                likeRequest.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SongActivity.this, "Likie con éxito", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SongActivity.this, "No Likie", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                        Toast.makeText(SongActivity.this, "No Likie onFailure", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        heartSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                heart.setVisibility(View.VISIBLE);
                heartSelected.setVisibility(View.GONE);
                Call<ResponseBody> likeRequest = songsService.unlikeSong(Integer.valueOf(id));
                likeRequest.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SongActivity.this, "Likie con éxito", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SongActivity.this, "No Likie", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                        Toast.makeText(SongActivity.this, "No Likie onFailure", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.rating);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                boolean fromUser) {
                if (fromUser) {
                    Call<ResponseBody> ratingRequest =
                        songsService.rankSong(Integer.valueOf(id), new UserSong(false, Math.round(rating)));
                    ratingRequest.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(SongActivity.this, "Rankie con éxito", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SongActivity.this, "No rankie", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                            Toast.makeText(SongActivity.this, "No rankie failure", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        checkPlayerRunning();
    }

    @Override
    protected void onPause() {
        super.onPause();

        checkPlayerRunning();
        finish();
    }

    private void checkPlayerRunning() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//            }
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }
}
