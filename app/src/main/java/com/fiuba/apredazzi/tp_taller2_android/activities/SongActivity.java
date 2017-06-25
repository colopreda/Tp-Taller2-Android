package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.fiuba.apredazzi.tp_taller2_android.model.UserSong;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private static MediaPlayer mediaPlayer;

    TextView textViewSong;
    TextView textViewAditional;
    ProgressBar progress;
    View viewLoading;
    RatingBar ratingBar;
    ImageView heart;
    ImageView heartSelected;
    ImageView next;
    ImageView previous;

    String album;
    String artist;
    String id;
    ArrayList<String> songsList;
    ArrayList<String> songsPlayed;

    String myId;

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
        next = (ImageView) findViewById(R.id.next);
        previous = (ImageView) findViewById(R.id.previous);
        mpv = (MaterialMusicPlayerView) findViewById(R.id.mpv);

        songsPlayed = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            textViewSong.setText(extras.getString("title", "No title"));
            artist = extras.getString("artist", "No artist");
            album = extras.getString("album", "No album");
            id = extras.getString("songid", "0");
            textViewAditional.setText(artist + " - " + album);
            songsList = extras.getStringArrayList("songList");
            songsPlayed = extras.getStringArrayList("previousList");
            updateInfoSong();
        }

        if (songsPlayed != null) {
            Collections.reverse(songsPlayed);
        }

        addListenerOnRatingBar();
        addListenerOnHeart();
        addListenerNext();
        addListenerPrevious();

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

//        CoverService coverService = retrofit.create(CoverService.class);
//        Call<AlbumArt> cover =
//            coverService.getCoverArt("album.getinfo", CoverService.last_fm_api, artist, album, CoverService.format);
//        cover.enqueue(new Callback<AlbumArt>() {
//            @Override
//            public void onResponse(final Call<AlbumArt> call, final Response<AlbumArt> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getAlbum() != null) {
//                        String image = response.body().getAlbum().getImage().get(2).getText();
//                        mpv.setCoverURL(image);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(final Call<AlbumArt> call, final Throwable t) {
//
//            }
//        });

        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
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

            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(final MediaPlayer mp, final int what, final int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            mpv.stop();
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            mpv.start();
                            break;
                    }
                    return false;
                }
            });

            try {
                String url = "http://ec2-34-201-152-32.compute-1.amazonaws.com:8000/api/songs/" + id + ".mp3";
                mediaPlayer.setDataSource(url);
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                        Toast.makeText(SongActivity.this, "La canción no se encuentra en el servidor", Toast.LENGTH_LONG)
                            .show();
                        Intent i = new Intent(SongActivity.this, MainActivity.class);
                        startActivity(i);
                        return false;
                    }
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mpv.setCoverDrawable(R.drawable.vinyl);

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
        if (id == null) {
            progress.setVisibility(View.GONE);
            viewLoading.setVisibility(View.GONE);
            mpv.setVisibility(View.VISIBLE);
            mpv.setOnClickListener(null);
            mediaPlayer.setOnPreparedListener(null);
        }
    }

    private void addListenerPrevious() {
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (songsList != null && !songsPlayed.isEmpty()) {
                    String idPreviousSong = songsPlayed.get(0);
                    songsPlayed.remove(0);
                    songsList.add(0, id);
                    id = idPreviousSong;
                    updateInfoSong();
                    progress.setVisibility(View.VISIBLE);
                    viewLoading.setVisibility(View.VISIBLE);
                    mpv.setProgress(0);
                    try {
                        mediaPlayer.reset();
                        String url = "http://ec2-34-201-152-32.compute-1.amazonaws.com:8000/api/songs/" + id + ".mp3";
                        mediaPlayer.setDataSource(url);
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void addListenerNext() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (songsList != null && !songsList.isEmpty()) {
                    String idNextSong = songsList.get(0);
                    songsList.remove(0);
                    songsPlayed.add(0, id);
                    id = idNextSong;
                    updateInfoSong();
                    progress.setVisibility(View.VISIBLE);
                    viewLoading.setVisibility(View.VISIBLE);
                    mpv.setProgress(0);
                    try {
                        mediaPlayer.reset();
                        String url = "http://ec2-34-201-152-32.compute-1.amazonaws.com:8000/api/songs/" + id + ".mp3";
                        mediaPlayer.setDataSource(url);
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateInfoSong() {
        Call<ServerResponse> song = songsService.getSong(Integer.valueOf(id));
        song.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    List<User> usuarios = response.body().getSong().getUsers();
                    for (User item : usuarios) {
                        if (item.getId().equals(myId)) {
                            ratingBar.setRating(item.getUserSong().getRate());
                            if (item.getUserSong().isLiked()) {
                                heartSelected.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    if (usuarios.isEmpty()) {
                        ratingBar.setRating(0);
                        heartSelected.setVisibility(View.GONE);
                        heart.setVisibility(View.VISIBLE);
                    }
                    Song song = response.body().getSong();
                    textViewSong.setText(song.getTitle());
                    artist = song.getArtist().get(0).getName();
                    album = song.getAlbum().getName();
                    textViewAditional.setText(artist + " - " + album);
                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {

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

        myId = settings.getString("myId", "null");
    }

    private void addListenerOnHeart() {
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                heart.setVisibility(View.GONE);
                heartSelected.setVisibility(View.VISIBLE);
                Call<ResponseBody> likeRequest =
                    songsService.likeSong(Integer.valueOf(id), new UserSong(true, ratingBar.getNumStars()));
                likeRequest.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(final Call<ResponseBody> call, final Throwable t) {

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

                    }

                    @Override
                    public void onFailure(final Call<ResponseBody> call, final Throwable t) {

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

                        }

                        @Override
                        public void onFailure(final Call<ResponseBody> call, final Throwable t) {

                        }
                    });
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkPlayerRunning();
    }
}
