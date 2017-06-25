package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.activities.SongsListActivity;
import com.fiuba.apredazzi.tp_taller2_android.api.PlaylistService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.interfaces.RecyclerViewClickListener;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.model.Playlist;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import com.squareup.picasso.Picasso;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apredazzi on 4/8/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.CustomViewHolder> {

    private List<Song> feedItemList;
    private List<Playlist> listaPlaylists;
    private Context mContext;
    private static RecyclerViewClickListener mListener;
    private boolean isPlaylist;
    private String playlistId;

    private String songId;

    public SongAdapter(Context context, List<Song> feedItemList, RecyclerViewClickListener itemClickListener, boolean isPlaylist, @Nullable
        String playlistId) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.mListener = itemClickListener;
        this.isPlaylist = isPlaylist;
        this.playlistId = playlistId;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.songs_row, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Song feedItem = feedItemList.get(i);

        //Render image using Picasso library
        if (feedItem.getAlbum() != null && feedItem.getAlbum().getImages() != null) {
            Picasso.with(mContext).load(feedItem.getAlbum().getImages().get(0))
                .into(customViewHolder.imageView);
        }

        //Setting text view title
        customViewHolder.textViewTitle.setText(feedItem.getTitle());
        String artistsStr = "";
        if (feedItem.getArtist() != null) {
            for (Artist artist : feedItem.getArtist()) {
                artistsStr += artist.getName() + " ";
            }
            if (!feedItem.getArtist().isEmpty()) {
                artistsStr += " - ";
            }
        }
        if (feedItem.getAlbum() != null && feedItem.getAlbum().getName() != null) {
            String album = feedItem.getAlbum().getName();
            artistsStr += album;
        }
        customViewHolder.textViewSubtitle.setText(artistsStr);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected ImageView addImageView;
        protected TextView textViewTitle;
        protected TextView textViewSubtitle;
        protected CardView cardView;
        protected ProgressDialog progressDialog;

        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.thumbnail_row);
            textViewTitle = (TextView) view.findViewById(R.id.title_row);
            textViewSubtitle = (TextView) view.findViewById(R.id.subtitle_row);
            addImageView = (ImageView) view.findViewById(R.id.add_playlist);
            if (isPlaylist) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Eliminando canción de la playlist...");
                addImageView.setImageResource(R.drawable.ic_close);
                addImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        progressDialog.show();
                        deleteFromPlaylist(Integer.valueOf(playlistId), feedItemList.get(getLayoutPosition()).getId(), progressDialog);
                    }
                });
            } else {
                addImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        loadPlaylistFromServer(getLayoutPosition());
                    }
                });
            }

            cardView = (CardView) view.findViewById(R.id.cardview_song);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            mListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }

    private void deleteFromPlaylist(final int playlistId, final int layoutPosition, final ProgressDialog progressDialog) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(mContext);
        String auth_token_string = settings.getString("auth_token", "null");
        PlaylistService playlistService = TokenGenerator.createService(PlaylistService.class, auth_token_string);
        final Call<ResponseBody> deleteSong = playlistService.deleteSongFromPlaylist(playlistId, layoutPosition);
        deleteSong.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Borre","MID");
                    Intent i = new Intent(mContext, SongsListActivity.class);
                    i.putExtra("playlists", true);
                    i.putExtra("id", String.valueOf(playlistId));
                    mContext.startActivity(i);
                    ((Activity)mContext).finish();
                } else {
                    Log.d("falle 404","MID");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                Log.d("onFailure","MID");
                progressDialog.dismiss();
            }
        });
    }

    private void loadPlaylistFromServer(final int position) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(mContext);
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

    public void playlistDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Agregar a playlist");
//        builder.setView(inflater.inflate(R.layout.country_dialog, null))
//            // Add action buttons
//        loadCountries();
        final Spinner spinner = loadPlaylists();
        builder.setView(spinner)
            .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    addSongToPlaylist((Playlist) spinner.getSelectedItem(), position);
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
        Spinner playlists = new Spinner(mContext);
        SpinnerAdapter adapter =
            new SpinnerAdapter(mContext, android.R.layout.simple_spinner_item, listaPlaylists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playlists.setAdapter(adapter);

        return playlists;
    }

    private void addSongToPlaylist(Playlist playlist, int position) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(mContext);
        String auth_token_string = settings.getString("auth_token", "null");
        PlaylistService playlistService = TokenGenerator.createService(PlaylistService.class, auth_token_string);
        Call<ResponseBody> addSongToPlaylist = playlistService.addSongToPlaylist(playlist.getId(), feedItemList.get(position).getId());
        addSongToPlaylist.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(mContext, "Agregue la cancion a la playlist", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Falle al agregar la cancion", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ResponseBody> call, final Throwable t) {

            }
        });
    }
}
