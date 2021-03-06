package com.musicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.musicapp.R;
import com.musicapp.activities.HomeItemClickListActivity;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.singleton.MySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 1/18/2017.
 */

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.SingleItemRowHolder> {

    private ArrayList<HomeDetailsJson.DataList> itemsList;
    private Context mContext;
    int categoryId;
    private ImageLoader mImageLoader;
    private static final String TAG=ArtistsAdapter.class.getSimpleName();
    ArtistClickListener mListener;

    public interface ArtistClickListener{
        void onArtistClick(ArrayList<HomeDetailsJson.DataList> itemList,int catId, int typeId, String typeName, String coverImage, int position);
    }
    public ArtistsAdapter(Context context, ArrayList<HomeDetailsJson.DataList> itemsList,int categoryId ) {
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        this.itemsList = itemsList;
        this.mContext = context;
        this.categoryId =categoryId;
        mListener=(ArtistClickListener) context;
        Log.w(TAG, itemsList.size()+ " ");

    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_artist, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder,final int i) {


        holder.latest_song_name.setText(itemsList.get(i).getColumns().getTypeName());
        holder.latest_song_details.setText(String.valueOf(itemsList.get(i).getColumns().getSongCount())+ " Songs");
        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG,categoryId+" , "+itemsList.get(i).getColumns().getTypeId()+" , "+itemsList.get(i).getColumns().getTypeName()+" , "+itemsList.get(i).getColumns().getCoverImage() );
                mListener.onArtistClick(itemsList,categoryId,itemsList.get(i).getColumns().getTypeId(),itemsList.get(i).getColumns().getTypeName(),itemsList.get(i).getColumns().getCoverImage(),i);
            }
        });
      //  holder.latest_song_img.setDefaultImageResId(R.drawable.playlist_music_white);
       // holder.latest_song_img.setImageUrl(itemsList.get(i).getColumns().getThumbnailImage(), mImageLoader);
        if (!itemsList.get(i).getColumns().getThumbnailImage().matches("")) {
            Picasso.with(mContext).load(itemsList.get(i).getColumns().getThumbnailImage()).into(holder.latest_song_img);
        }

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView latest_song_name,latest_song_details;
        protected LinearLayout main_layout;
        protected ImageView latest_song_img;

        public SingleItemRowHolder(View view) {
            super(view);

            this.latest_song_name = (TextView) view.findViewById(R.id.latest_song_name);
            this.latest_song_details = (TextView) view.findViewById(R.id.latest_song_details);
            this.latest_song_img = (ImageView) view.findViewById(R.id.latest_song_img);
             this.main_layout = (LinearLayout)view.findViewById(R.id.main_layout);

        }

    }

}