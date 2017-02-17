package com.musicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.HomeItemClickDetailsActivity;
import com.musicapp.activities.PlayerScreenListActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.pojos.HomeDetailsJson;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 12/27/2016.
 */
public class PlayerScreenListAdapter extends RecyclerView.Adapter<PlayerScreenListAdapter.SingleItemRowHolder> {

    private ArrayList<HomeDetailsJson.DataList> itemsList;
    private Context mContext;
    int playingIndex;

    public PlayerScreenListAdapter(Context context, ArrayList<HomeDetailsJson.DataList> itemsList, int playingIndex) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.playingIndex = playingIndex;
        itemsList.remove(playingIndex);
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.player_screen_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, final int i) {


      /*  if (i == playingIndex) {
            itemsList.remove(i);
            if (itemsList.size() != 0) {
                holder.latest_song_name.setText(itemsList.get(i).getColumns().getSongName());
            }else {
                PlayerScreenListActivity.tvLabelNext.setVisibility(View.GONE);
            }
        } else {*/
            holder.latest_song_name.setText(itemsList.get(i).getColumns().getSongName());
       /* }*/

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

        protected TextView latest_song_name;

        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);

            this.latest_song_name = (TextView) view.findViewById(R.id.latest_song_name);
            //  this.itemImage = (ImageView) view.findViewById(R.id.itemImage);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =getPosition();
                    int songId = itemsList.get(position).getColumns().getSongId();
                    System.out.println("TYPE ID NAME" + songId);

                    int index = getIndexByProperty(songId);
                    AudioPlayerActivity.index=index;
                    if (AudioPlayerActivity.isPlaying = true) {
                        AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                        audioPlayerActivity.playSong(index);
                    }


                }
            });


        }

    }

    private int getIndexByProperty(int yourSongId) {
        for (int i = 0; i < AudioPlayerActivity.audio_itemsList.size(); i++) {
            int songId = AudioPlayerActivity.audio_itemsList.get(i).getColumns().getSongId();

            if (songId == yourSongId) {

                return i;
            }
        }
        return -1;// not there is list
    }

}
