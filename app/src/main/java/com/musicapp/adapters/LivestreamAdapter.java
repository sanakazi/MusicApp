package com.musicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
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


import com.android.volley.toolbox.ImageLoader;

import com.musicapp.R;

import com.musicapp.activities.LiveStreamVideoActivity;

import com.musicapp.pojos.LivestreamJson;

import com.musicapp.singleton.MySingleton;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by SanaKazi on 2/15/2017.
 */

public class LivestreamAdapter extends RecyclerView.Adapter<LivestreamAdapter.SingleItemRowHolder> {

    private ArrayList<LivestreamJson.DataListClass> itemsList;
    private Context mContext;
    int category_Id;
    private ImageLoader mImageLoader;
    private static final String TAG=LivestreamAdapter.class.getSimpleName();
    long startTme;



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

                                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                calIntent.setType("vnd.android.cursor.item/event");
                                calIntent.putExtra(CalendarContract.Events.TITLE, itemsList.get(position).getConcertTitle());
                                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, itemsList.get(position).getLocation());
                                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, itemsList.get(position).getDescription());

                                String date = itemsList.get(position).getConcertDate();
                                try {
                                    Date sampleDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
                                    startTme = sampleDate.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                        startTme);
                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                        startTme);

                                mContext.startActivity(calIntent);

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