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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.adapter.SongAdapter;
import com.fiuba.apredazzi.tp_taller2_android.api.AlbumService;
import com.fiuba.apredazzi.tp_taller2_android.api.SongsService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.interfaces.RecyclerViewClickListener;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongsListActivity extends BaseActivity implements RecyclerViewClickListener {

    private List<Song> songsList;
    private RecyclerView mRecyclerView;
    private SongAdapter adapter;
    private ProgressBar progressBar;

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

        // loadAdapterWithMock();

        String id = "";
        if (getIntent().getExtras() != null) {
            id = getIntent().getExtras().getString("id");
            boolean album = getIntent().getExtras().getBoolean("album");
        }

        loadAdapterFromServer(id);

    }

    private void loadAdapterWithMock() {

        for (int i = 0; i < 25; i++) {
//            Song song = new Song();
//            song.setTitle("Canción " + i);
//            song.setArtist("Artista " + i);
//            songsList.add(song);
        }
    }

    private void loadAdapterFromServer(String id) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String auth_token_string = settings.getString("auth_token", "null");

        if (!"null".equals(auth_token_string)) {
            if (id.isEmpty()) {
                SongsService songsService = TokenGenerator.createService(SongsService.class, auth_token_string);
                Call<ServerResponse> allSongs = songsService.getSongs();
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

                    }
                });
            } else {
                AlbumService albumService = TokenGenerator.createService(AlbumService.class, auth_token_string);
                Call<ServerResponse> albumSongs = albumService.getAlbum(Integer.valueOf(id));
                albumSongs.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            songsList = response.body().getAlbum().getSongs();
                            loadAdapter();
                        }
                    }

                    @Override
                    public void onFailure(final Call<ServerResponse> call, final Throwable t) {

                    }
                });
            }
        }
    }

    public void loadAdapter() {
        progressBar.setVisibility(View.GONE);
        adapter = new SongAdapter(SongsListActivity.this, songsList, this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void recyclerViewListClicked(final View v, final int position) {
        Song song = songsList.get(position);
        Intent i = new Intent(this, SongActivity.class);
        String artistsStr = "";
        for (Artist artist : song.getArtist()) {
            artistsStr += artist.getName() + " ";
        }
        i.putExtra("artist", artistsStr);
        i.putExtra("album", song.getAlbum());
        i.putExtra("title",song.getTitle());
        i.putExtra("songid", String.valueOf(song.getId()));
        startActivity(i);
        Toast.makeText(SongsListActivity.this, "Cliqueaste en la canción " + song.getTitle(), Toast.LENGTH_LONG).show();
    }
}
