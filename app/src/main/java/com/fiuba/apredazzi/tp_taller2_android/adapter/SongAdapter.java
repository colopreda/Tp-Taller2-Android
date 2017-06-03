package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.interfaces.RecyclerViewClickListener;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import java.util.List;

/**
 * Created by apredazzi on 4/8/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.CustomViewHolder> {

    private List<Song> feedItemList;
    private Context mContext;
    private static RecyclerViewClickListener mListener;

    public SongAdapter(Context context, List<Song> feedItemList, RecyclerViewClickListener itemClickListener) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.mListener = itemClickListener;
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
        String artistsStr = "";
        if (feedItem.getArtist() != null) {
            for (Artist artist : feedItem.getArtist()) {
                artistsStr += artist.getName() + " ";
            }
        }
        customViewHolder.textViewSubtitle.setText(artistsStr);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView textViewTitle;
        protected TextView textViewSubtitle;
        protected CardView cardView;

        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.thumbnail_row);
            textViewTitle = (TextView) view.findViewById(R.id.title_row);
            textViewSubtitle = (TextView) view.findViewById(R.id.subtitle_row);
            cardView = (CardView) view.findViewById(R.id.cardview_song);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            mListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }
}
