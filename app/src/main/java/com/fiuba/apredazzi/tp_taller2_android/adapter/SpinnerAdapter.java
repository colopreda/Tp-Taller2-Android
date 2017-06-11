package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.model.Playlist;
import java.util.List;

/**
 * Created by apredazzi on 6/7/17.
 */

public class SpinnerAdapter extends ArrayAdapter<Playlist> {

    private Context context;
    private List<Playlist> playlists;

    public SpinnerAdapter(@NonNull final Context context,
        @LayoutRes final int resource, List<Playlist> playlists) {
        super(context, resource, playlists);
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Nullable
    @Override
    public Playlist getItem(final int position) {
        return playlists.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return playlists.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        TextView label = new TextView(context);
        label.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 128));
        label.setPadding(32,16,16,0);
        label.setTextSize(24);
        label.setText(playlists.get(position).getName());

        return label;
    }

    @Override
    public View getDropDownView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        TextView label = new TextView(context);
        label.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 128));
        label.setPadding(16,0,16,0);
        label.setTextSize(24);
        label.setText(playlists.get(position).getName());

        return label;
    }
}
