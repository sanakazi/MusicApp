package com.musicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.musicapp.R;
import com.musicapp.activities.HomeItemClickDetailsActivity;
import com.musicapp.pojos.OfflineArtistItem;
import com.musicapp.singleton.MySingleton;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/20/2017.
 */
public class RvRecentlyPlayedArtist extends RecyclerView.Adapter<RvRecentlyPlayedArtist.PersonViewHolder> {

    static Context context;
    static ArrayList<OfflineArtistItem> list;
    private ImageLoader mImageLoader;
    public static class PersonViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView tvName, tvDetail;
        NetworkImageView ivThumbnail;
        ImageView ivDetail;

        PersonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
            ivThumbnail = (NetworkImageView) itemView.findViewById(R.id.ivThumbnail);
            ivDetail = (ImageView) itemView.findViewById(R.id.ivDetail);
        }

        @Override
        public void onClick(View v) {
            int position=getPosition();
            Intent intent = new Intent(context, HomeItemClickDetailsActivity.class);
            intent.putExtra(HomeItemClickDetailsActivity.CAT_ID, list.get(position).getCatId());
            intent.putExtra(HomeItemClickDetailsActivity.TYPE_ID, list.get(position).getTypeId());
            intent.putExtra(HomeItemClickDetailsActivity.TYPE_NAME, list.get(position).getTypeName());
            intent.putExtra(HomeItemClickDetailsActivity.IMAGE_URL, list.get(position).getThumbnail());
            context.startActivity(intent);


        }
    }

    public RvRecentlyPlayedArtist(ArrayList<OfflineArtistItem> list,Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_offline_artist, viewGroup, false);
        PersonViewHolder personViewHolder = new PersonViewHolder(v);
        return personViewHolder;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, final int position) {
        final OfflineArtistItem current = list.get(position);
        personViewHolder.tvName.setText(current.getArtistName());
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        String imageUrl = current.getThumbnail();
        System.out.println("ARTIST IMAGE"+imageUrl);
        if (!imageUrl.matches("") || imageUrl != null) {
            personViewHolder.ivThumbnail.setImageUrl(imageUrl, mImageLoader);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}