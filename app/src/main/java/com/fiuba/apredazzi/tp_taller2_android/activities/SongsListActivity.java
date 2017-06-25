package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.adapter.SongAdapter;
import com.fiuba.apredazzi.tp_taller2_android.api.AlbumService;
import com.fiuba.apredazzi.tp_taller2_android.api.ArtistService;
import com.fiuba.apredazzi.tp_taller2_android.api.PlaylistService;
import com.fiuba.apredazzi.tp_taller2_android.api.SongsService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.interfaces.RecyclerViewClickListener;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongsListActivity extends BaseActivity implements RecyclerViewClickListener {

    private List<Song> songsList;
    private RecyclerView mRecyclerView;
    private SongAdapter adapter;
    private ProgressBar progressBar;
    private Button playAll;
    private Button followArtist;

    private List<String> songsListStr;

    private ArtistService artistService;

    private boolean album;
    private boolean artist;
    private boolean playlist;
    private boolean songs;
    private boolean followed;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_songs_list, null, false);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        frameLayout.addView(contentView);

        setTitle("Canciones");

        songsList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        playAll = (Button) findViewById(R.id.rep_all);
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startSong(0);
            }
        });

        // loadAdapterWithMock();

        album = false;
        playlist = false;
        artist = false;
        songs = false;

        id = "";
        if (getIntent().getExtras() != null) {
            id = getIntent().getExtras().getString("id");
            album = getIntent().getExtras().getBoolean("albums");
            playlist = getIntent().getExtras().getBoolean("playlists");
            artist = getIntent().getExtras().getBoolean("artists");
            songs = getIntent().getExtras().getBoolean("songs");
        }

        followArtist = (Button) findViewById(R.id.follow_artist);

        loadAdapterFromServer(id);
    }

    private void followArtist(final String id) {
        final String finalId = id;
        followArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Call<ResponseBody> artistFollow = artistService.followArtist(Integer.valueOf(finalId));
                artistFollow.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(SongsListActivity.this, "Error al seguir al artista", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                        Toast.makeText(SongsListActivity.this, "onFailure siguiendo a artista", Toast.LENGTH_LONG).show();
                    }
                });
                followArtist.setBackgroundResource(R.drawable.rounded_button_clicked);
                followArtist.setText("SIGUIENDO");
                unfollowArtist(id);
            }
        });
    }

    private void unfollowArtist(final String id) {
        final String finalId = id;
        followArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Call<ResponseBody> artistUnfollow = artistService.unfollowArtist(Integer.valueOf(finalId));
                artistUnfollow.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(SongsListActivity.this, "Error al intentar dejar de seguir a artista", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                        Toast.makeText(SongsListActivity.this, "onFailure dejando de seguir a artista", Toast.LENGTH_LONG).show();
                    }
                });
                followArtist.setBackgroundResource(R.drawable.rounded_button);
                followArtist.setText("SEGUIR ARTISTA");
                followArtist(id);
            }
        });
    }

    private void loadAdapterFromServer(String id) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String auth_token_string = settings.getString("auth_token", "null");

        if (!"null".equals(auth_token_string)) {
            if (songs) {
                SongsService songsService = TokenGenerator.createService(SongsService.class, auth_token_string);
                Call<ServerResponse> allSongs;
                if (getIntent().hasExtra("filter")){
                    allSongs = songsService.getSongs(getIntent().getExtras().getString("filter"));
                    setTitleTooblar("Canciones");
                } else if (getIntent().hasExtra("drawer")) {
                    allSongs = songsService.getFavoriteSongs();
                    setTitleTooblar("Mis canciones");
                } else {
                    allSongs = songsService.getSongs(null);
                    setTitleTooblar("Canciones");
                }
                allSongs.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            songsList = response.body().getSongs();
                            loadAdapter();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                        Toast.makeText(SongsListActivity.this, "Error onFailure playlist", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (album) {
                AlbumService albumService = TokenGenerator.createService(AlbumService.class, auth_token_string);
                Call<ServerResponse> albumSongs = albumService.getAlbum(Integer.valueOf(id));
                albumSongs.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            songsList = response.body().getAlbum().getSongs();
                            for (Song item : songsList) {
                                item.setAlbum(response.body().getAlbum());
                            }
                            loadAdapter();
                        } else {
                            Toast.makeText(SongsListActivity.this, "Error al cargar las canciones del album", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ServerResponse> call, final Throwable t) {

                    }
                });
            } else if (playlist) {
                PlaylistService playlistService =
                    TokenGenerator.createService(PlaylistService.class, auth_token_string);
                Call<ServerResponse> playlistSongs = playlistService.getSongsFromPlaylist(Integer.valueOf(id));
                playlistSongs.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            songsList = response.body().getSongs();
                            loadAdapter();
                        } else {
                            Toast.makeText(SongsListActivity.this, "Error al cargar las canciones de la playlist", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                        Toast.makeText(SongsListActivity.this, "OnFailure playlist", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (artist) {
                followArtist.setVisibility(View.VISIBLE);
                followed = getIntent().getBooleanExtra("followed", false);
                if (!followed) {
                    followArtist(id);
                } else {
                    followArtist.setBackgroundResource(R.drawable.rounded_button_clicked);
                    followArtist.setText("SIGUIENDO");
                    unfollowArtist(id);
                }
                artistService = TokenGenerator.createService(ArtistService.class, auth_token_string);
                Call<ServerResponse> artistSongs = artistService.getSongsFromArtist(Integer.valueOf(id));
                artistSongs.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            songsList = response.body().getSongs();
                            loadAdapter();
                        } else {
                            Toast.makeText(SongsListActivity.this, "Error al cargar las canciones del artista", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                        Toast.makeText(SongsListActivity.this, "Error onFailure artist", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public void loadAdapter() {
        progressBar.setVisibility(View.GONE);
        if (playlist) {
            adapter = new SongAdapter(SongsListActivity.this, songsList, this, true, id);
        } else {
            adapter = new SongAdapter(SongsListActivity.this, songsList, this, false, null);
        }
        mRecyclerView.setAdapter(adapter);
        songsListStr = new ArrayList<String>();
        for (Song item : songsList) {
            songsListStr.add(String.valueOf(item.getId()));
        }
    }

    @Override
    public void recyclerViewListClicked(final View v, final int position) {
        startSong(position);
    }

    private void startSong(int position) {
        Song song = songsList.get(position);
        Intent i = new Intent(this, SongActivity.class);
        String artistsStr = "";
        if (song.getArtist() != null) {
            for (Artist artist : song.getArtist()) {
                artistsStr += artist.getName() + " ";
            }
            i.putExtra("artist", artistsStr);
        }
        if (song.getAlbum() != null && song.getAlbum().getName() != null) {
            i.putExtra("album", song.getAlbum().getName());
        }
        if (song.getTitle() != null) {
            i.putExtra("title", song.getTitle());
        }
        if (song.getId() != 0) {
            i.putExtra("songid", String.valueOf(song.getId()));
            ArrayList<String> finalList =
                new ArrayList<String>(songsListStr.subList(position + 1, songsListStr.size()));
            i.putStringArrayListExtra("songList", (ArrayList<String>) finalList);
            ArrayList<String> previousList = new ArrayList<String>(songsListStr.subList(0, position));
            i.putStringArrayListExtra("previousList", (ArrayList<String>) previousList);
        }
        startActivity(i);
    }
}
