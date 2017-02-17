package com.musicapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.HomeItemClickListActivity;
import com.musicapp.activities.LiveStreamVideoActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.fragments.LivestreamFragment;
import com.musicapp.others.ComonHelper;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.LivestreamJson;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SanaKazi on 2/15/2017.
 */

public class LivestreamAdapter extends RecyclerView.Adapter<LivestreamAdapter.SingleItemRowHolder> {

    private ArrayList<LivestreamJson.DataListClass> itemsList;
    private Context mContext;
    int category_Id;
    private ImageLoader mImageLoader;
    private static final String TAG=LivestreamAdapter.class.getSimpleName();



    public LivestreamAdapter(Context mContext, ArrayList<LivestreamJson.DataListClass> itemsList ) {
        mImageLoader = MySingleton.getInstance(mContext).getImageLoader();
        this.itemsList = itemsList;
        this.mContext = mContext;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.livestream_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {



        holder.latest_song_name.setText(itemsList.get(position).getConcertTitle());
        holder.latest_song_details.setText(itemsList.get(position).getConcertDate().toString());
        if (!itemsList.get(position).getThumbnailImage().matches("")) {
            Picasso.with(mContext).load(itemsList.get(position).getThumbnailImage()).into(holder.latest_song_img);
        }
        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LiveStreamVideoActivity.class);
                intent.putParcelableArrayListExtra(LiveStreamVideoActivity.LIVE_ARRAY,itemsList);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });

        holder.latest_song_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String concertDate=itemsList.get(position).getConcertDate();

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext,holder.latest_song_edit);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_livestream_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                long startTime=0,endTime=0;

                                String startDate = "2011/09/01";
                                try {
                                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                                    startTime=date.getTime();
                                }
                                catch(Exception e){ }

                                Calendar cal = Calendar.getInstance();
                                Intent intent = new Intent(Intent.ACTION_EDIT);
                                intent.setType("vnd.android.cursor.item/event");
                                intent.putExtra("beginTime",startTime);
                                intent.putExtra("allDay", true);
                                intent.putExtra("rrule", "FREQ=YEARLY");
                                intent.putExtra("endTime", endTime);
                                intent.putExtra("title", "A Test Event from android app");
                                mContext.startActivity(intent);

                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView latest_song_name,latest_song_details;
        protected ImageView latest_song_img;

        LinearLayout main_layout,latest_song_edit;

        public SingleItemRowHolder(View view) {
            super(view);

            this.latest_song_name = (TextView) view.findViewById(R.id.latest_song_name);
            this.latest_song_details = (TextView) view.findViewById(R.id.latest_song_details);
            this.latest_song_img = (ImageView) view.findViewById(R.id.latest_song_img);
            this.latest_song_edit = (LinearLayout)view.findViewById(R.id.latest_song_edit);
            this.main_layout=(LinearLayout)view.findViewById(R.id.main_layout);
        }

    }

}