package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.activities.PlaylistActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.SongsListActivity;
import com.fiuba.apredazzi.tp_taller2_android.model.Playlist;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by apredazzi on 6/7/17.
 */

public class PlaylistGridViewAdapter extends BaseAdapter {

    List<Playlist> playlists;
    private Context context;

    public PlaylistGridViewAdapter(final Context context, final List<Playlist> playlists) {
        this.playlists = playlists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(final int position) {
        return playlists.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return playlists.get(position).getId();
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
            textView.setText(playlists.get(position).getName());
        } else {
            grid = (View) convertView;
        }

        return grid;
    }

    private void startActivity(final int position) {
        Intent intent = new Intent(context, SongsListActivity.class);
        intent.putExtra("playlists", true);
        intent.putExtra("id", String.valueOf(getItemId(position)));
        context.startActivity(intent);
    }
}
