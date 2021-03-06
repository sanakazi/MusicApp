package com.musicapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.HomeItemClickListActivity;
import com.musicapp.activities.PlayerScreenListActivity;
import com.musicapp.activities.SongTakeoverActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.others.ComonHelper;

import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;


public class HomeItemClickDetailsAdapter extends RecyclerView.Adapter<HomeItemClickDetailsAdapter.ViewHolder> {

    Context c;
    private ArrayList<HomeDetailsJson.DataList> itemsList;
    private static ArrayList<HomeDetailsJson.DataList> offlineSongArrayList;
    private ArrayList<HomeDetailsJson.DataList> audio_itemsList;
    private ArrayList<HomeDetailsJson.DataList> video_itemsList;
    private ImageLoader mImageLoader;
    String albumName;
    static String from;
    int playlistId;
    static boolean duplicationIndex;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private LikeUnlikeSongListener likeUnlikeSongListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView imageView;
        ImageView latest_song_edit;
        ImageView profile;
        TextView Name;
        TextView email;
        //   LinearLayout  lnrRight;
        RelativeLayout item_ll;
        NetworkImageView ivThumbnail;
        LinearLayout itemImageOverlay_opacity;
        ImageView itemImageOverlay;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);


            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if (ViewType == TYPE_ITEM) {


                itemImageOverlay_opacity = (LinearLayout) itemView.findViewById(R.id.itemImageOverlay_opacity);
                itemImageOverlay = (ImageView) itemView.findViewById(R.id.itemImageOverlay);

                textView = (TextView) itemView.findViewById(R.id.latest_song_name);
                item_ll = (RelativeLayout) itemView.findViewById(R.id.item_ll);

                //  lnrRight = (LinearLayout) itemView.findViewById(R.id.lnrRight);
                latest_song_edit = (ImageView) itemView.findViewById(R.id.latest_song_edit);
                ivThumbnail = (NetworkImageView) itemView.findViewById(R.id.ivThumbnail);
                // lnrRight.setVisibility(View.VISIBLE);
                latest_song_edit.setVisibility(View.VISIBLE);
              /*  if (from.matches("playlist")) {
                    lnrRight.setVisibility(View.VISIBLE);
                    latest_song_edit.setVisibility(View.VISIBLE);
                } else {
                    lnrRight.setVisibility(View.GONE);
                    latest_song_edit.setVisibility(View.GONE);
                }*/
                // Creating TextView object with the id of textView from item_row.xml
                //      imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {
                //header view

                Holderid = 0;
            }
        }


    }


    public interface LikeUnlikeSongListener {
        void onSongLikeUnlike(int playlistId, String from, String songName, String albumName, String thumbnail, int songId, String like, int songTypeId);

    }

    public HomeItemClickDetailsAdapter(Context c, ArrayList<HomeDetailsJson.DataList> itemsList, ArrayList<HomeDetailsJson.DataList> audio_itemsList, ArrayList<HomeDetailsJson.DataList> video_itemsList, String albumName, String from, int playlistId) {

        this.itemsList = itemsList;
        this.c = c;
        this.albumName = albumName;
        this.audio_itemsList = audio_itemsList;
        this.video_itemsList = video_itemsList;
        this.from = from;
        this.playlistId = playlistId;
        likeUnlikeSongListener = (LikeUnlikeSongListener) c;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_item_click_detials_item, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType);
            return vhItem;
        }
      /*  else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_item_click_detials_header,parent,false); //Inflating the layout
            ViewHolder vhHeader = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view
            return vhHeader; //returning the object created
        }*/
        return null;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder.Holderid == 1) {
            //   holder.textView.setText(itemsList.get(position-1).getColumns().getSongName());
            holder.textView.setText(itemsList.get(position).getColumns().getSongName());

            mImageLoader = MySingleton.getInstance(c).getImageLoader();
            String imageUrl = itemsList.get(position).getColumns().getThumbnailImage();
            Log.w("hie", imageUrl);
            if (!imageUrl.matches("") || imageUrl != null) {
                holder.ivThumbnail.setImageUrl(imageUrl, mImageLoader);
            }
            if (itemsList.get(position).getColumns().getSongTypeId() == 1) {
                holder.itemImageOverlay_opacity.setVisibility(View.VISIBLE);
                holder.itemImageOverlay.setImageResource(R.drawable.audio_android);
            } else if (itemsList.get(position).getColumns().getSongTypeId() == 2) {
                holder.itemImageOverlay_opacity.setVisibility(View.VISIBLE);
                holder.itemImageOverlay.setImageResource(R.drawable.video_android);
            }


            Log.w("hie", itemsList.get(position).getColumns().getSongName());
            holder.item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                   /*-----to store the offline songs-----*/
                    ComonHelper.storeOfflineSongLisner(c, position, itemsList);


                    if (itemsList.get(position).getColumns().getSongTypeId() == 1) {


                        //audio song
                        ComonHelper.pauseFlag = false;

                        try {
                            if (ComonHelper.timer != null) {
                                ComonHelper.timer.cancel();
                            }
                            if (AudioPlayerActivity.isPlaying) {
                                AudioPlayerActivity.timer.cancel();
                                BackgroundSoundService.mPlayer.release();
                                Intent intent = new Intent(c, BackgroundSoundService.class);
                                ((Activity) c).stopService(intent);
                            } else if (AudioPlayerActivity.isPause) {
                                if (AudioPlayerActivity.timer != null) {
                                    AudioPlayerActivity.timer.cancel();
                                }
                                if (BackgroundSoundService.mPlayer != null) {
                                    BackgroundSoundService.mPlayer.release();
                                    Intent intent = new Intent(c, BackgroundSoundService.class);
                                    ((Activity) c).stopService(intent);
                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                       /*-----to store the offline songs-----*/


                        System.out.println("POSITION OF LIST" + position + " " + audio_itemsList.size());
                        Intent intent = new Intent(c, AudioPlayerActivity.class);
                        Bundle bund = new Bundle();
                        bund.putParcelableArrayList("categories", itemsList);
                        bund.putParcelableArrayList("specific_categories", audio_itemsList);
                        bund.putInt("index", position);
                        bund.putString(AudioPlayerActivity.ALBUM_NAME, albumName);
                        bund.putString("from", "home");
                        // bund.putString("des",audio_itemsList.get(position).getColumns().getDescription()); // changes on 25 sept 2017
                        bund.putString("des", itemsList.get(position).getColumns().getDescription());
                        bund.putBoolean("isDeeplink", false);
                        intent.putExtras(bund);
                        ComonHelper.notifFlag = false;
                        c.startActivity(intent);
                    } else if (itemsList.get(position).getColumns().getSongTypeId() == 2) {
                        //video song
                        try {
                            if (AudioPlayerActivity.isPlaying) {
                                AudioPlayerActivity.timer.cancel();
                                BackgroundSoundService.mPlayer.release();
                                Intent intent = new Intent(c, BackgroundSoundService.class);
                                ((Activity) c).stopService(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //video song
                        Intent intent = new Intent(c, VideoPlayerActivity.class);
                        Bundle b = new Bundle();
                        b.putParcelableArrayList("categories", itemsList);
                        b.putInt("index", position);
                        b.putString("from", "home");
                        b.putString("album_name", albumName);
                        b.putInt("songId", itemsList.get(position).getColumns().getSongId());
                        b.putParcelableArrayList("specific_categories", video_itemsList);
                        b.putBoolean("isDeeplink", false);
                        //  b.putString("des",video_itemsList.get(position).getColumns().getDescription());// changes on 25 sept 2017
                        b.putString("des", itemsList.get(position).getColumns().getDescription());

                        intent.putExtras(b);
                        Toast.makeText(c, "Pleasse wait we are preparing", Toast.LENGTH_LONG).show();
                        c.startActivity(intent);
                        Log.w("Song", "adapter " + itemsList.get(position).getColumns().getSongId());
                    }
                }
            });
            holder.latest_song_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    likeUnlikeSongListener.onSongLikeUnlike(playlistId, from, itemsList.get(position).getColumns().getSongName(), albumName,
                            itemsList.get(position).getColumns().getThumbnailImage(), itemsList.get(position).getColumns().getSongId(), itemsList.get(position).getColumns().getLike(), itemsList.get(position).getColumns().getSongTypeId());


                }
            });

        } else {

        }
    }


    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        //   return itemsList.size()+1;
        return itemsList.size();
    }


    @Override
    public int getItemViewType(int position) {
    /*    if (isPositionHeader(position))
            return TYPE_HEADER;
*/
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}