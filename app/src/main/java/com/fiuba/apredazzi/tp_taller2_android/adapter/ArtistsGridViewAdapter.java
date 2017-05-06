package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by apredazzi on 4/30/17.
 */

public class ArtistsGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<Artist> artists;

    public ArtistsGridViewAdapter(final Context context,
        final List<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(final int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return artists.get(position).getId();
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
            ImageView imageView = (ImageView)grid.findViewById(R.id.image_grid_view);
            textView.setText(artists.get(position).getName());
            if (artists.get(position).getImages() != null) {
                Picasso.with(context).load(artists.get(position).getImages().get(0)).into(imageView);
            }
        } else {
            grid = (View) convertView;
        }

        return grid;

    }
}
