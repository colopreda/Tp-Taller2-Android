package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.adapter.AlbumsGridViewAdapter;
import com.fiuba.apredazzi.tp_taller2_android.api.AlbumService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.model.Album;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apredazzi on 5/1/17.
 */

public class AlbumsActivity extends BaseActivity {

    private GridView gridView;

    private List<Album> albumList;

    String auth_token_string;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_grid_layout, frameLayout, false);
        frameLayout.addView(contentView);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                startActivity(parent, position);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        auth_token_string = settings.getString("auth_token", "null");

        setTitleTooblar("Álbumes");

        getAlbumsFromServer();
    }

    private void startActivity(final AdapterView<?> parent, final int position) {
        Intent intent = new Intent(AlbumsActivity.this, SongsListActivity.class);
        intent.putExtra("albums", true);
        intent.putExtra("id", String.valueOf(parent.getItemIdAtPosition(position)));
        startActivity(intent);
    }

    private void getAlbumsFromServer() {
        AlbumService albumService = TokenGenerator.createService(AlbumService.class, auth_token_string);
        Call<ServerResponse> listAlbums = albumService.getAlbums();
        listAlbums.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    albumList = response.body().getAlbums();
                    progressBar.setVisibility(View.GONE);
                    setAdapter();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AlbumsActivity.this, "Recibi != 200 - albumes", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AlbumsActivity.this, "onFailure albums", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAdapter() {
//        albumList = new ArrayList<>();
//        albumList.add(new Album(1, "Album1", null, null, null, null));
//        albumList.add(new Album(2, "Album2", null, null, null, null));
//        albumList.add(new Album(3, "Album3", null, null, null, null));
//        albumList.add(new Album(4, "Album4", null, null, null, null));
        gridView.setAdapter(new AlbumsGridViewAdapter(AlbumsActivity.this, albumList));
    }
}
