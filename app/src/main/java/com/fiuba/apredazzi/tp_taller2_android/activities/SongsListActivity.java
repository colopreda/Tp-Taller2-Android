package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.adapter.SongAdapter;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import java.util.ArrayList;
import java.util.List;

public class SongsListActivity extends AppCompatActivity {

    private List<Song> songsList;
    private RecyclerView mRecyclerView;
    private SongAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        loadAdapterWithMock();

        progressBar.setVisibility(View.GONE);
        adapter = new SongAdapter(SongsListActivity.this, songsList);
        mRecyclerView.setAdapter(adapter);
    }

    private void loadAdapterWithMock() {
        songsList = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            Song song = new Song();
            song.setTitle("Canción " + i);
            song.setArtist("Artista " + i);
            songsList.add(song);
        }
    }
}
