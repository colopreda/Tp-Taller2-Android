package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.activities.SongsListActivity;
import com.fiuba.apredazzi.tp_taller2_android.api.PlaylistService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.model.Album;
import com.fiuba.apredazzi.tp_taller2_android.model.Playlist;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import org.w3c.dom.Text;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apredazzi on 5/1/17.
 */

public class AlbumsGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<Album> albums;
    private List<Playlist> listaPlaylists;

    public AlbumsGridViewAdapter(final Context context,
        final List<Album> artists) {
        this.context = context;
        this.albums = artists;
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(final int position) {
        return albums.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return albums.get(position).getId();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View grid;
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(context);
            grid = inflater.inflate(R.layout.item_grid_view, null);
            TextView textView = (TextView) grid.findViewById(R.id.text_grid_view);
            ImageView imageView = (ImageView) grid.findViewById(R.id.image_grid_view);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startActivity(position);
                }
            });
            ImageView addPlaylist = (ImageView) grid.findViewById(R.id.add_to_playlist);
            addPlaylist.setVisibility(View.VISIBLE);
            addPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Toast.makeText(context, "Click en add playlist", Toast.LENGTH_LONG).show();
                    loadPlaylistFromServer(position);
                }
            });
            textView.setText(albums.get(position).getName());
            if (albums.get(position).getImages() != null) {
                Picasso.with(context).load(albums.get(position).getImages().get(0)).into(imageView);
            }
        } else {
            grid = (View) convertView;
        }

        return grid;
    }

    private void startActivity(final int position) {
        Intent intent = new Intent(context, SongsListActivity.class);
        intent.putExtra("type", "albums");
        intent.putExtra("id", String.valueOf(getItemId(position)));
        context.startActivity(intent);
    }

    public void playlistDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Agregar a playlist");
//        builder.setView(inflater.inflate(R.layout.country_dialog, null))
//            // Add action buttons
//        loadCountries();
        final Spinner spinner = loadPlaylists();
        builder.setView(spinner)
            .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    addAlbumToPlaylist((Playlist) spinner.getSelectedItem(), position);
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

    private Spinner loadPlaylists() {
        Spinner playlists = new Spinner(context);
        List<String> playlistsStr = new ArrayList<>();
        for (Playlist item : listaPlaylists) {
            playlistsStr.add(item.getName());
        }
        SpinnerAdapter adapter =
            new SpinnerAdapter(context, android.R.layout.simple_spinner_item, listaPlaylists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playlists.setAdapter(adapter);

        return playlists;
    }

    private void loadPlaylistFromServer(final int position) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(context);
        String auth_token_string = settings.getString("auth_token", "null");
        PlaylistService playlistService = TokenGenerator.createService(PlaylistService.class, auth_token_string);
        final Call<ServerResponse> listPlaylists = playlistService.getPlaylists();
        listPlaylists.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    listaPlaylists = response.body().getPlaylists();
                    playlistDialog(position);
                } else {

                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {

            }
        });
    }

    private void addAlbumToPlaylist(Playlist playlist, int position) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(context);
        String auth_token_string = settings.getString("auth_token", "null");
        PlaylistService playlistService = TokenGenerator.createService(PlaylistService.class, auth_token_string);
        Call<ResponseBody> addAlbumToPlaylist = playlistService.addAlbumToPlaylist(playlist.getId(), (int) getItemId(position));
        addAlbumToPlaylist.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Agregue el album a la playlist", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Falle al agregar album", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ResponseBody> call, final Throwable t) {

            }
        });
    }
}
