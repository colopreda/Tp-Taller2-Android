package com.fiuba.apredazzi.tp_taller2_android.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.interfaces.RecyclerViewClickListener;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by apredazzi on 4/19/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.CustomViewHolder> {

    private List<User> feedItemList;
    private Context mContext;
    private static RecyclerViewClickListener mListener;

    public ContactsAdapter(Context context, List<User> feedItemList, RecyclerViewClickListener itemClickListener) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.mListener = itemClickListener;
    }

    @Override
    public ContactsAdapter.CustomViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactsAdapter.CustomViewHolder holder, final int position) {
        User feedItem = feedItemList.get(position);

        holder.textViewTitle.setText(feedItem.getEmail());
        if (feedItem.getImages() != null && !feedItem.getImages().isEmpty()) {
            Picasso.with(mContext).load(feedItem.getImages().get(0)).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView textViewTitle;
        protected CardView cardView;

        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.thumbnail_row);
            textViewTitle = (TextView) view.findViewById(R.id.title_row);
            cardView = (CardView) view.findViewById(R.id.cardview_song);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            mListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }
}
