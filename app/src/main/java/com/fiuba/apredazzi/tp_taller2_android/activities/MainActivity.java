package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.adapter.ArtistRecommendedAdapter;
import com.fiuba.apredazzi.tp_taller2_android.adapter.ArtistsGridViewAdapter;
import com.fiuba.apredazzi.tp_taller2_android.adapter.TracksRecommendedAdapter;
import com.fiuba.apredazzi.tp_taller2_android.api.ArtistService;
import com.fiuba.apredazzi.tp_taller2_android.api.SongsService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.api.UsersService;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private TextView userEmail;
    private TextView nameDrawer;
    private TextView emailDrawer;

    private HorizontalGridView gridViewArtists;
    private HorizontalGridView gridViewTracks;

    private List<Artist> artistList;
    private List<Song> songsList;

    String auth_token_string;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
//        View contentView = inflater.inflate(R.layout.content_main, frameLayout, false);
        View contentView = inflater.inflate(R.layout.activity_reccommendations, frameLayout, false);
        frameLayout.addView(contentView);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        auth_token_string = settings.getString("auth_token", "null");

        gridViewArtists = (HorizontalGridView) findViewById(R.id.horizontal_gridView_artist);
        gridViewTracks = (HorizontalGridView) findViewById(R.id.horizontal_gridView_songs);

//        gridViewArtists.setNumRows(5);
//        gridViewTracks.setNumRows(5);

        getRecommendedArtists();
        getRecommendedSongs();

        String myId = settings.getString("myId", null);
        if (myId != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("user_" + myId);
        }

        userEmail = (TextView) findViewById(R.id.user_email);
        nameDrawer = (TextView) findViewById(R.id.name_drawer);
        emailDrawer = (TextView) findViewById(R.id.email_drawer);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

//        String full_name = settings.getString("full_name", "null");
//        String email = settings.getString("email", "null");
//
//        if (!"null".equals(full_name) && !"null".equals(email)) {
//            setNameAndEmail(full_name, email);
//        }

        if (auth_token_string != null) {
//            userEmail.setText(auth_token_string);
//            getMe();
        } else {
            userEmail.setText("No hay token seteado");
        }
    }

    private void setAdapterArtist() {
        gridViewArtists.setAdapter(new ArtistRecommendedAdapter(this, artistList));
    }

    private void setAdapterSongs() {
        gridViewTracks.setAdapter(new TracksRecommendedAdapter(this, songsList));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Salir")
            .setMessage("¿Estás seguro que deseas salir de la aplicación?")
            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    public void getRecommendedArtists() {
        ArtistService artistService = TokenGenerator.createService(ArtistService.class, auth_token_string);
        Call<ServerResponse> listArtistas = artistService.getRecommendedArtists();
        listArtistas.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    artistList = response.body().getArtists();
                    setAdapterArtist();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "Error al recibir los artistas recomendados", Toast.LENGTH_LONG)
                        .show();
                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                Toast.makeText(MainActivity
                    .this, "Falle", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getRecommendedSongs() {
        SongsService songsService = TokenGenerator.createService(SongsService.class, auth_token_string);
        Call<ServerResponse> listSongs = songsService.getRecommendedSongs();
        listSongs.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    songsList = response.body().getSongs();
                    setAdapterSongs();
                } else {
                    Toast.makeText(MainActivity.this, "Error al recibir las canciones recomendaeos",
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {

            }
        });
    }
}
