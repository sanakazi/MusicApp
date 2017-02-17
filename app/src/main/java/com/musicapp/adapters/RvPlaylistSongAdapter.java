package com.musicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.PlaylistSongActivity;

import com.musicapp.activities.SongTakeoverActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.pojos.PlaylistSongItem;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/12/2017.
 */
public class RvPlaylistSongAdapter extends RecyclerView.Adapter<RvPlaylistSongAdapter.PersonViewHolder> {

    static Context context;
    static ArrayList<PlaylistSongItem> playList;
    static ArrayList<PlaylistSongItem> video_list;
    static ArrayList<PlaylistSongItem> audio_list;
    int size;
    Typeface Roboto_Regular;

    public static class PersonViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView tvName;
        ImageView ivDetail, ivThumb;

        PersonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ivDetail = (ImageView) itemView.findViewById(R.id.ivDetail);
        }

        @Override
        public void onClick(View v) {
            int position = getPosition();
           int songTypeid=playList.get(position).getSongTypeId();
            if (songTypeid==1){
               Intent i=new Intent(context, AudioPlayerActivity.class);
                context.startActivity(i);
            }else if(songTypeid==2){
                Intent i=new Intent(context, VideoPlayerActivity.class);
                context.startActivity(i);
            }


        }
    }

    public RvPlaylistSongAdapter(ArrayList<PlaylistSongItem> playList, Context context) {
        this.playList = playList;
        this.context = context;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_playlist_songs, viewGroup, false);
        PersonViewHolder personViewHolder = new PersonViewHolder(v);
        return personViewHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int position) {
        PlaylistSongItem current = playList.get(position);
        personViewHolder.tvName.setText(current.getSongName());
        personViewHolder.ivDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,SongTakeoverActivity.class);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

}
