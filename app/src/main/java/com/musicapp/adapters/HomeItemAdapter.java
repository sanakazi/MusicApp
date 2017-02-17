package com.musicapp.adapters;

/**
 * Created by pratap.kesaboyina on 24-12-2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.HomeItemClickListActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.others.ComonHelper;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.SingleItemRowHolder> {

    private ArrayList<HomeDetailsJson.DataList> itemsList;
    private ArrayList<HomeDetailsJson.DataList> audio_itemsList;
    private ArrayList<HomeDetailsJson.DataList> video_itemsList;
    private Context mContext;
    int category_Id;
    private ImageLoader mImageLoader;



    public HomeItemAdapter(Context context, ArrayList<HomeDetailsJson.DataList> itemsList,ArrayList<HomeDetailsJson.DataList> audio_itemsList, ArrayList<HomeDetailsJson.DataList> video_itemsList,int category_Id) {
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        this.itemsList = itemsList;
        this.mContext = context;
        this.category_Id=category_Id;
        this.audio_itemsList=audio_itemsList;
        this.video_itemsList=video_itemsList;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder,final int position) {


        if(itemsList.get(position).getColumns().getSongTypeId()==0){
            holder.itemImageOverlay_opacity.setVisibility(View.GONE);
            holder.itemImageOverlay.setVisibility(View.GONE);
        }
        else if(itemsList.get(position).getColumns().getSongTypeId()==1){
            holder.itemImageOverlay_opacity.setVisibility(View.VISIBLE);
            holder.itemImageOverlay.setVisibility(View.VISIBLE);
            holder.itemImageOverlay.setImageResource(R.drawable.audio_android);
        }
        else if(itemsList.get(position).getColumns().getSongTypeId()==2){
            holder.itemImageOverlay_opacity.setVisibility(View.VISIBLE);
            holder.itemImageOverlay.setVisibility(View.VISIBLE);
            holder.itemImageOverlay.setImageResource(R.drawable.video_android);
        }

       holder.tvTitle.setText(itemsList.get(position).getColumns().getTypeName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CATEGORRYYYYY"+category_Id);

                if (category_Id == 2) {
                    ComonHelper.storeOfflineStoreArtist(mContext,position,itemsList,category_Id);
                }


                if(itemsList.get(position).getColumns().getSongTypeId()==0) {
                    Intent intent = new Intent(mContext, HomeItemClickListActivity.class);
                    intent.putExtra(HomeItemClickListActivity.CAT_ID, category_Id);
                    intent.putExtra(HomeItemClickListActivity.TYPE_ID, itemsList.get(position).getColumns().getTypeId());
                    intent.putExtra(HomeItemClickListActivity.TYPE_NAME, itemsList.get(position).getColumns().getTypeName());
                    intent.putExtra(HomeItemClickListActivity.IMAGE_URL, itemsList.get(position).getColumns().getCoverImage());
                    mContext.startActivity(intent);

                }
                else if(itemsList.get(position).getColumns().getSongTypeId()==1){
                    //open audio activity
                /*   Toast.makeText(mContext,"Audio Activity" ,Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(mContext, HomeItemClickDetailsActivity.class);
                   intent.putExtra(HomeItemClickDetailsActivity.CAT_ID, cat_id_forAllSongs);
                   intent.putExtra(HomeItemClickDetailsActivity.TYPE_ID, type_id_forAllSongs);
                   intent.putExtra(HomeItemClickDetailsActivity.TYPE_NAME, itemsList.get(position).getColumns().getTypeName());
                   intent.putExtra(HomeItemClickDetailsActivity.IMAGE_URL, itemsList.get(position).getColumns().getCoverImage());
                   mContext.startActivity(intent);*/


                   /*-----to store the offline songs-----*/
                    ComonHelper.storeOfflineSongLisner(mContext,position,itemsList);
                    //audio song
                    Intent intent = new Intent(mContext, AudioPlayerActivity.class);
                    Bundle bund = new Bundle();
                    bund.putParcelableArrayList("categories", itemsList);
                    bund.putParcelableArrayList("specific_categories", audio_itemsList);
                    bund.putInt("index", position);
                    bund.putString(AudioPlayerActivity.ALBUM_NAME,  itemsList.get(position).getColumns().getSongName());
                    bund.putString("from", "home");
                    intent.putExtras(bund);
                    mContext.startActivity(intent);

                }
                else if(itemsList.get(position).getColumns().getSongTypeId()==2){
                    //open video activity
                    /*-----to store the offline songs-----*/
                    ComonHelper.storeOfflineSongLisner(mContext,position,itemsList);

                 /*  Intent intent = new Intent(mContext, HomeItemClickDetailsActivity.class);
                   intent.putExtra(HomeItemClickDetailsActivity.CAT_ID, cat_id_forAllSongs);
                   intent.putExtra(HomeItemClickDetailsActivity.TYPE_ID, type_id_forAllSongs);
                   intent.putExtra(HomeItemClickDetailsActivity.TYPE_NAME, itemsList.get(i).getColumns().getTypeName());
                   intent.putExtra(HomeItemClickDetailsActivity.IMAGE_URL, itemsList.get(i).getColumns().getCoverImage());
                   mContext.startActivity(intent);*/


                    //store offline song here


                    //video song
                    if (AudioPlayerActivity.isPlaying) {
                        AudioPlayerActivity.timer.cancel();
                        BackgroundSoundService.mPlayer.release();
                        Intent intent = new Intent(mContext, BackgroundSoundService.class);
                        ((Activity) mContext).stopService(intent);
                    }

                    //video song
                    Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelableArrayList("categories", itemsList);
                    b.putInt("index", position);
                    b.putString("from", "home");
                    b.putString("album_name", itemsList.get(position).getColumns().getSongName());
                    b.putInt("songId", itemsList.get(position).getColumns().getSongId());
                    b.putParcelableArrayList("specific_categories", video_itemsList);
                    intent.putExtras(b);
                    Toast.makeText(mContext, "Pleasse wait we are preparing", Toast.LENGTH_LONG).show();
                    mContext.startActivity(intent);
                    Log.w("Song", "adapter " + itemsList.get(position).getColumns().getSongId());



                }




            }
        });

        if (!itemsList.get(position).getColumns().getThumbnailImage().matches("")) {
            Picasso.with(mContext).load(itemsList.get(position).getColumns().getThumbnailImage()).into(holder.itemImage);
        }

    /*    holder.itemImage.setDefaultImageResId(R.drawable.audio_android);
        holder.itemImage.setImageUrl(itemsList.get(position).getColumns().getThumbnailImage(), mImageLoader);
*/
       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected ImageView itemImage;
        protected CardView cardView;
        protected LinearLayout itemImageOverlay_opacity;
        protected ImageView itemImageOverlay;
        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
            this.cardView = (CardView)view.findViewById(R.id.cardView);
            this.itemImageOverlay_opacity=(LinearLayout)view.findViewById(R.id.itemImageOverlay_opacity);
            this.itemImageOverlay=(ImageView)view.findViewById(R.id.itemImageOverlay);

        }

    }

}