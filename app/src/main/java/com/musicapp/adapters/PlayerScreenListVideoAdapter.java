package com.musicapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.musicapp.R;

import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.pojos.HomeDetailsJson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/4/2017.
 */
public class PlayerScreenListVideoAdapter extends RecyclerView.Adapter<PlayerScreenListVideoAdapter.SingleItemRowHolder> {

    private ArrayList<HomeDetailsJson.DataList> tempList=new ArrayList<>();
    private ArrayList<HomeDetailsJson.DataList> mainArray;
    private Context context;
    int playingIndex;

    public PlayerScreenListVideoAdapter(Context context,ArrayList<HomeDetailsJson.DataList> mainArray, int playingIndex) {
        this.mainArray=mainArray;
        this.context = context;
        this.playingIndex = playingIndex;
        tempList.addAll(mainArray);
        tempList.remove(playingIndex);


    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.player_screen_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, final int i) {

        holder.latest_song_name.setText(tempList.get(i).getColumns().getSongName());

    }

    @Override
    public int getItemCount() {
        System.out.println("LIST SIZE" + tempList.size());
        return (null != tempList ? tempList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView latest_song_name;

        protected ImageView itemImage;
        LinearLayout lnrMain;


        public SingleItemRowHolder(View view) {
            super(view);

            this.latest_song_name = (TextView) view.findViewById(R.id.latest_song_name);
          //  this.lnrMain = (LinearLayout) view.findViewById(R.id.lnrMain);
            //  this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
        view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(((Activity) context),"Please wait we are preparing",Toast.LENGTH_LONG).show();

                    int position =getPosition();

                    int songId = tempList.get(position).getColumns().getSongId();
                    System.out.println("Adapter song id of selected pos" + songId+" "+position);
                    int index = getIndexByProperty(songId);
                    VideoPlayerActivity.index = index;
                    System.out.println("Adapter got index" + index);
                    VideoPlayerActivity.rlDetailView.setVisibility(View.GONE);
                    VideoPlayerActivity.rlPlayerView.setVisibility(View.VISIBLE);
                    VideoPlayerActivity.isPlayerView = true;
                    if (VideoPlayerActivity.video_itemsList.size() != 0) {
                        if (index != VideoPlayerActivity.video_itemsList.size()) {

                            VideoPlayerActivity.isUrlChange = true;
                            System.out.println("Adapter got index" + VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongId());
                          //  index = index + 1;
                            VideoPlayerActivity.VideoURL = VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongURL();
                            Picasso.with(context).load(VideoPlayerActivity.video_itemsList.get(index).getColumns().getThumbnailImage());
                            VideoPlayerActivity.tvSong.setText(VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongName());
                            VideoPlayerActivity.latest_song_name_detail.setText(VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongName());
                            VideoPlayerActivity.tvDesSongName.setText(VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongName());
                            VideoPlayerActivity.isAnotherOptionScreen = false;
                            VideoPlayerActivity.detroyedTime = 0;
                            String url = VideoPlayerActivity.video_itemsList.get(index).getColumns().getThumbnailImage();
                            if (!url.matches("") && url != null) {
                                Picasso.with(context).load(url).into(VideoPlayerActivity.latest_song_img_detail);
                                Picasso.with(context).load(url).into(VideoPlayerActivity.ivDesSongImage);
                            }

                            VideoPlayerActivity.player.release();
                        }
                    }


                }
            });


        }
    }

    private int getIndexByProperty(int yourSongId) {
        System.out.println("VIDEO LIST SIZE"+VideoPlayerActivity.video_itemsList.size());
        for (int i = 0; i<mainArray.size(); i++) {
            int songId = mainArray.get(i).getColumns().getSongId();

            if (songId == yourSongId) {

                return i;
            }
        }
        return -1;// not there is list
    }
}
