package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.model.Album;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by apredazzi on 5/1/17.
 */

public class AlbumsGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<Album> albums;

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
            textView.setText(albums.get(position).getName());
            if (albums.get(position).getImages() != null) {
                Picasso.with(context).load(albums.get(position).getImages().get(0)).into(imageView);
            }
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}
