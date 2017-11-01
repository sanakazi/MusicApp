package com.musicapp.adapters;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.HomeItemClickDetailsActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.others.ComonHelper;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.OfflineArtistItem;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by SanaKazi on 12/22/2016.
 */
public class HomeItemClickListItemAdapter extends RecyclerView.Adapter<HomeItemClickListItemAdapter.SingleItemRowHolder> {

    private ArrayList<HomeDetailsJson.DataList> itemsList;
    private static ArrayList<OfflineArtistItem> offlineArtistArrayList;
    private ArrayList<HomeDetailsJson.DataList> audio_itemsList;
    private ArrayList<HomeDetailsJson.DataList> video_itemsList;
    private Context mContext;
    int category_Id;
    private ImageLoader mImageLoader;
    int cat_id_forAllSongs, type_id_forAllSongs,subCategoryId;
    static boolean duplicateArtistName;
    String categoryName;

    public HomeItemClickListItemAdapter(Context context, ArrayList<HomeDetailsJson.DataList> itemsList,int category_Id ,int cat_id_forAllSongs,int type_id_forAllSongs,int subCategoryId) {
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        this.itemsList = itemsList;
        this.mContext = context;
        this.category_Id=category_Id;
        this.cat_id_forAllSongs=cat_id_forAllSongs;
        this.type_id_forAllSongs=type_id_forAllSongs;
        this.subCategoryId=subCategoryId;


    }


    public HomeItemClickListItemAdapter(Context context, ArrayList<HomeDetailsJson.DataList> itemsList, ArrayList<HomeDetailsJson.DataList> audio_itemsList, ArrayList<HomeDetailsJson.DataList> video_itemsList, int category_Id, String categoryName, int cat_id_forAllSongs, int type_id_forAllSongs, int subCategoryId) {
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        this.itemsList = itemsList;
        this.mContext = context;
        this.category_Id=category_Id;
        this.cat_id_forAllSongs=cat_id_forAllSongs;
        this.type_id_forAllSongs=type_id_forAllSongs;
        this.subCategoryId=subCategoryId;
        this.audio_itemsList=audio_itemsList;
        this.video_itemsList=video_itemsList;
        this.categoryName = categoryName;
    }
    

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_itemclicklist_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {


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
      /*  holder.itemImage.setDefaultImageResId(R.drawable.home_item);
        holder.itemImage.setImageUrl(itemsList.get(position).getColumns().getThumbnailImage(), mImageLoader);*/

        if (!itemsList.get(position).getColumns().getThumbnailImage().matches("")) {
            Picasso.with(mContext).load(itemsList.get(position).getColumns().getThumbnailImage()).into(holder.itemImage);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.w("HomeItemClickList", "category id is " + category_Id);

               if (category_Id == 2) {
                    ComonHelper.storeOfflineStoreArtist(mContext,position,itemsList,category_Id);
                }

               if(itemsList.get(position).getColumns().getSongTypeId()==0) {
                    Intent intent = new Intent(mContext, HomeItemClickDetailsActivity.class);
                    intent.putExtra(HomeItemClickDetailsActivity.CAT_ID, category_Id);
                    intent.putExtra(HomeItemClickDetailsActivity.TYPE_ID, itemsList.get(position).getColumns().getTypeId());
                    intent.putExtra(HomeItemClickDetailsActivity.TYPE_NAME, itemsList.get(position).getColumns().getTypeName());
                    intent.putExtra(HomeItemClickDetailsActivity.IMAGE_URL, itemsList.get(position).getColumns().getCoverImage());
                    mContext.startActivity(intent);
              }

                else if(itemsList.get(position).getColumns().getSongTypeId()==1){
                     /*-----to store the offline songs-----*/
                   ComonHelper.storeOfflineSongLisner(mContext,position,itemsList);

                   //open audio activity
                /*   Toast.makeText(mContext,"Audio Activity" ,Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(mContext, HomeItemClickDetailsActivity.class);
                   intent.putExtra(HomeItemClickDetailsActivity.CAT_ID, cat_id_forAllSongs);
                   intent.putExtra(HomeItemClickDetailsActivity.TYPE_ID, type_id_forAllSongs);
                   intent.putExtra(HomeItemClickDetailsActivity.TYPE_NAME, itemsList.get(position).getColumns().getTypeName());
                   intent.putExtra(HomeItemClickDetailsActivity.IMAGE_URL, itemsList.get(position).getColumns().getCoverImage());
                   mContext.startActivity(intent);*/


                   //store offline song here

                   //audio song
                   try {
                       if (ComonHelper.timer != null) {
                           ComonHelper.timer.cancel();
                       }
                       ComonHelper.pauseFlag = false;
                       if (AudioPlayerActivity.isPlaying) {
                           AudioPlayerActivity.timer.cancel();
                           BackgroundSoundService.mPlayer.release();
                           Intent intent = new Intent(mContext, BackgroundSoundService.class);
                           ((Activity) mContext).stopService(intent);
                       } else if (AudioPlayerActivity.isPause) {
                           if (AudioPlayerActivity.timer != null) {
                               AudioPlayerActivity.timer.cancel();
                           }
                           if (BackgroundSoundService.mPlayer != null) {
                               BackgroundSoundService.mPlayer.release();
                               Intent intent = new Intent(mContext, BackgroundSoundService.class);
                               ((Activity) mContext).stopService(intent);
                           }


                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }


                   Intent intent = new Intent(mContext, AudioPlayerActivity.class);
                   Bundle bund = new Bundle();
                   bund.putParcelableArrayList("categories", itemsList);
                   bund.putParcelableArrayList("specific_categories", audio_itemsList);
                   bund.putInt("index", position);
                   //  bund.putString(AudioPlayerActivity.ALBUM_NAME,  itemsList.get(position).getColumns().getSongName());
                   bund.putString(AudioPlayerActivity.ALBUM_NAME, categoryName);
                   bund.putString("from", "home");
                   bund.putString("des", audio_itemsList.get(position).getColumns().getDescription());
                   bund.putBoolean("isDeeplink", false);
                   intent.putExtras(bund);
                   ComonHelper.notifFlag = false;
                   mContext.startActivity(intent);

                }
                else if(itemsList.get(position).getColumns().getSongTypeId()==2){
                   /*-----to store the offline songs-----*/
                   ComonHelper.storeOfflineSongLisner(mContext,position,itemsList);

                  //open video activity


                 /*  Intent intent = new Intent(mContext, HomeItemClickDetailsActivity.class);
                   intent.putExtra(HomeItemClickDetailsActivity.CAT_ID, cat_id_forAllSongs);
                   intent.putExtra(HomeItemClickDetailsActivity.TYPE_ID, type_id_forAllSongs);
                   intent.putExtra(HomeItemClickDetailsActivity.TYPE_NAME, itemsList.get(i).getColumns().getTypeName());
                   intent.putExtra(HomeItemClickDetailsActivity.IMAGE_URL, itemsList.get(i).getColumns().getCoverImage());
                   mContext.startActivity(intent);*/


            //store offline song here


                   //video song
                   try {
                       if (AudioPlayerActivity.isPlaying) {
                           AudioPlayerActivity.timer.cancel();
                           BackgroundSoundService.mPlayer.release();
                           Intent intent = new Intent(mContext, BackgroundSoundService.class);
                           ((Activity) mContext).stopService(intent);
                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }


                   //video song
                   Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                   Bundle b = new Bundle();
                   b.putParcelableArrayList("categories", itemsList);
                   b.putInt("index", position);
                   b.putString("from", "home");
                   //  b.putString("album_name", itemsList.get(position).getColumns().getSongName());
                   System.out.println("ALBUM NAME" + categoryName);
                   b.putString("album_name", categoryName);
                   b.putInt("songId", itemsList.get(position).getColumns().getSongId());
                   b.putParcelableArrayList("specific_categories", video_itemsList);
                   b.putBoolean("isDeeplink", false);
                   b.putString("des", video_itemsList.get(position).getColumns().getDescription());
                   intent.putExtras(b);
                   Toast.makeText(mContext, "Pleasse wait we are preparing", Toast.LENGTH_LONG).show();
                   mContext.startActivity(intent);
                   Log.w("Song", "adapter " + itemsList.get(position).getColumns().getSongId());



                }

            }
        });


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
    private static boolean isCheckDuplicatArtist(String myArtist) {

        for (int i = 0; i < offlineArtistArrayList.size(); i++) {
            String artist = offlineArtistArrayList.get(i).getArtistName();
            if (artist.matches(myArtist)) {
                return true;
            }
        }
        return false;
    }
}