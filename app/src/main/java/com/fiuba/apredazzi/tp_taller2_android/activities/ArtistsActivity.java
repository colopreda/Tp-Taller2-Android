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
import com.fiuba.apredazzi.tp_taller2_android.adapter.ArtistsGridViewAdapter;
import com.fiuba.apredazzi.tp_taller2_android.api.ArtistService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apredazzi on 6/13/17.
 */

public class ArtistsActivity extends BaseActivity {

    private GridView gridView;

    private List<Artist> artistList;

    String auth_token_string;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
//        View contentView = inflater.inflate(R.layout.content_main, frameLayout, false);
        View contentView = inflater.inflate(R.layout.activity_grid_layout, frameLayout, false);
        frameLayout.addView(contentView);

        gridView = (GridView) findViewById(R.id.gridView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        auth_token_string = settings.getString("auth_token", "null");

        ArtistService artistService = TokenGenerator.createService(ArtistService.class, auth_token_string);
        Call<ServerResponse> listArtistas;
        if (getIntent().getExtras() != null && getIntent().hasExtra("filter")) {
            String queryParam = getIntent().getExtras().getString("filter");
            listArtistas = artistService.getArtists(queryParam);
            setTitleTooblar("Artistas");
        } else if (getIntent().getExtras() != null && getIntent().hasExtra("drawer")) {
            listArtistas = artistService.getFavoriteArtists();
            setTitleTooblar("Mis artistas");
        } else {
            listArtistas = artistService.getArtists(null);
            setTitleTooblar("Artistas");
        }

        listArtistas.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    artistList = response.body().getArtists();
                    setAdapter();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ArtistsActivity.this, "Hubo un error al buscar los artistas",
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                Toast.makeText(ArtistsActivity
                    .this, "Falle", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setAdapter() {
        gridView.setAdapter(new ArtistsGridViewAdapter(this, artistList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                Intent intent = new Intent(ArtistsActivity.this, SongsListActivity.class);
                intent.putExtra("artists", true);
                intent.putExtra("id", String.valueOf(parent.getItemIdAtPosition(position)));
                if ((artistList.get(position).getFollowed() == null) || (!artistList.get(position).getFollowed().isEmpty())) {
                    intent.putExtra("followed", true);
                } else {
                    intent.putExtra("followed", false);
                }
                startActivity(intent);
            }
        });
    }
}
