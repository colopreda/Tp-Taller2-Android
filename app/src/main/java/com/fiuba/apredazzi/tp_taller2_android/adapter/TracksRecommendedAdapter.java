package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.activities.SongActivity;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by apredazzi on 6/21/17.
 */

public class TracksRecommendedAdapter extends RecyclerView.Adapter<TracksRecommendedAdapter.SimpleViewHolder> {

    private Context context;
    private List<Song> songs;

    public TracksRecommendedAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final TextView cancion;
        public final ImageView imagen;

        public SimpleViewHolder(View view) {
            super(view);
            cancion = (TextView) view.findViewById(R.id.text_grid_view);
            imagen = (ImageView) view.findViewById(R.id.image_grid_view);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.item_grid_view, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.cancion.setText(songs.get(position).getTitle());
        if (songs.get(position).getAlbum() != null && songs.get(position).getAlbum().getImages() != null &&
            !songs.get(position).getAlbum().getImages().isEmpty()) {
            Picasso.with(context).load(songs.get(position).getAlbum().getImages().get(0)).into(holder.imagen);
        }
        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(context, SongActivity.class);
                intent.putExtra("artists", true);
                intent.putExtra("songid", String.valueOf(getItemId(position)));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return this.songs.size();
    }
}
