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
import com.fiuba.apredazzi.tp_taller2_android.activities.SongsListActivity;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by apredazzi on 6/21/17.
 */

public class ArtistRecommendedAdapter extends RecyclerView.Adapter<ArtistRecommendedAdapter.SimpleViewHolder> {

    private Context context;
    private List<Artist> artists;

    public ArtistRecommendedAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final TextView artista;
        public final ImageView imagen;

        public SimpleViewHolder(View view) {
            super(view);
            artista = (TextView) view.findViewById(R.id.text_grid_view);
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
        holder.artista.setText(artists.get(position).getName());
        if (artists.get(position).getImages() != null) {
            Picasso.with(context).load(artists.get(position).getImages().get(0)).into(holder.imagen);
        }
        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(context, SongsListActivity.class);
                intent.putExtra("artists", true);
                intent.putExtra("id", String.valueOf(getItemId(position)));
                intent.putExtra("followed", !artists.get(position).getFollowed().isEmpty());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return artists.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return this.artists.size();
    }
}
