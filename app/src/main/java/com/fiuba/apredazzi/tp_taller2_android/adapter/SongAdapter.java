package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import java.util.List;

/**
 * Created by apredazzi on 4/8/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.CustomViewHolder> {

    private List<Song> feedItemList;
    private Context mContext;

    public SongAdapter(Context context, List<Song> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
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
//        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
//            Picasso.with(mContext).load(feedItem.getThumbnail())
//                .error(R.drawable.placeholder)
//                .placeholder(R.drawable.placeholder)
//                .into(customViewHolder.imageView);
//        }

        //Setting text view title
        customViewHolder.textViewTitle.setText(feedItem.getTitle());
        customViewHolder.textViewSubtitle.setText(feedItem.getArtist());
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textViewTitle;
        protected TextView textViewSubtitle;

        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.thumbnail_row);
            textViewTitle = (TextView) view.findViewById(R.id.title_row);
            textViewSubtitle = (TextView) view.findViewById(R.id.subtitle_row);
        }
    }
}
