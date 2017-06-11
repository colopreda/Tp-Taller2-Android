package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.adapter.PlaylistGridViewAdapter;
import com.fiuba.apredazzi.tp_taller2_android.api.PlaylistService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.model.Playlist;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apredazzi on 6/7/17.
 */

public class PlaylistActivity extends BaseActivity {

    private String auth_token_string;
    private String myId;
    private GridView gridView;
    private FloatingActionButton floating;
    private ProgressBar progressBar;

    private List<Playlist> listaPlaylists;
    PlaylistService playlistService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_grid_layout, frameLayout, false);
        frameLayout.addView(contentView);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        auth_token_string = settings.getString("auth_token", "null");
        myId = settings.getString("myId", "null");

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                startActivity(parent, position);
            }
        });

        floating = (FloatingActionButton) findViewById(R.id.floating_add);
        floating.setVisibility(View.VISIBLE);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                newPlaylistDialog();
            }
        });

        playlistService = TokenGenerator.createService(PlaylistService.class, auth_token_string);

        setTitleTooblar("Playlists");

        loadPlaylistFromServer();

    }

    private void loadPlaylistFromServer() {
        Call<ServerResponse> listPlaylists = playlistService.getPlaylists();
        listPlaylists.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    listaPlaylists = response.body().getPlaylists();
                    progressBar.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                    gridView.setAdapter(new PlaylistGridViewAdapter(PlaylistActivity.this, listaPlaylists));
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PlaylistActivity.this, "Recibi != 200 - playlists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PlaylistActivity.this, "Recibi onFailure playlists", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startActivity(final AdapterView<?> parent, final int position) {
        Intent intent = new Intent(PlaylistActivity.this, SongsListActivity.class);
        intent.putExtra("type", "playlists");
        intent.putExtra("playlists", true);
        intent.putExtra("id", String.valueOf(parent.getItemIdAtPosition(position)));
        startActivity(intent);
    }

    public void newPlaylistDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.playlist_dialog, null);

        builder.setTitle("Crear Playlist");
//        builder.setView(R.layout.filter_dialog);
        final EditText nameEditText = (EditText) dialogView.findViewById(R.id.edittext_name);
        final EditText descriptionEditText = (EditText) dialogView.findViewById(R.id.edittext_description);
        builder.setView(dialogView)
            .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    gridView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    createNewPlaylist(nameEditText.getText().toString(), descriptionEditText.getText().toString());
                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        ;
        builder.create();
        builder.show();
    }

    private void createNewPlaylist(String name, String description) {

        Playlist playlist = new Playlist(name, description, Integer.valueOf(myId));
        Call<ResponseBody> addPlaylist = playlistService.addPlaylists(playlist);
        addPlaylist.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PlaylistActivity.this, "Agregue playlist", Toast.LENGTH_LONG).show();
                    loadPlaylistFromServer();
                } else {
                    Toast.makeText(PlaylistActivity.this, "Recibi != 200 - agregar playlists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                Toast.makeText(PlaylistActivity.this, "Failure playlists", Toast.LENGTH_LONG).show();
            }
        });
    }
}
