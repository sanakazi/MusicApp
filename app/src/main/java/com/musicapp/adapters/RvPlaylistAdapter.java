package com.musicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.musicapp.R;
import com.musicapp.activities.PlayListActivity;
import com.musicapp.activities.PlaylistSongActivity;
import com.musicapp.activities.PlaylistTakeOverActivity;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.singleton.MySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/12/2017.
 */
public class RvPlaylistAdapter extends RecyclerView.Adapter<RvPlaylistAdapter.PersonViewHolder> {

    static Context context;
    static ArrayList<PlaylistItem> playList;
   public static ArrayList<Integer> checkList=new ArrayList<Integer>();
    int size;
    Typeface Roboto_Regular;
    PlaylistTakeoverListener playlistTakeoverListener;

    private ImageLoader mImageLoader;

    public interface PlaylistTakeoverListener
    {
        void onPlayListTakeoverClick(int playlistId, String playlistname,String thumbnail);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView tvName,tvSongCount;
        ImageView ivThumbnail;
         ImageView ivDetail;
         CheckBox checkPlaylist;
        PersonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSongCount=(TextView) itemView.findViewById(R.id.tvSongCount);
            ivDetail = (ImageView) itemView.findViewById(R.id.ivDetail);
            ivThumbnail=(ImageView) itemView.findViewById(R.id.ivThumbnail);
            checkPlaylist = (CheckBox) itemView.findViewById(R.id.checkPlaylist);
        }

        @Override
        public void onClick(View v) {
            int position = getPosition();
            Bundle bundle=new Bundle();
            bundle.putInt("playlistId",playList.get(position).getPlaylistId());
            bundle.putString("playlistName",playList.get(position).getPlaylistName());
            bundle.putString("playlistImage", playList.get(position).getImageUrl());
            Intent i = new Intent(context, PlaylistSongActivity.class);
            i.putExtras(bundle);
            context.startActivity(i);


        }
    }

    public RvPlaylistAdapter(ArrayList<PlaylistItem> playList, Context context) {
        this.playList = playList;
        this.context = context;
        playlistTakeoverListener = (PlaylistTakeoverListener)context;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_playlist, viewGroup, false);
        PersonViewHolder personViewHolder = new PersonViewHolder(v);
        return personViewHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int position) {
        final PlaylistItem current = playList.get(position);
        personViewHolder.tvName.setText(current.getPlaylistName());
        personViewHolder.tvSongCount.setText(current.getSongCount()+" Songs");
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        if (!current.getSongCount().matches("0")){
          //  personViewHolder.ivThumbnail.setImageUrl(current.getImageUrl(), mImageLoader);
            Picasso.with(context).load(current.getImageUrl()).into(personViewHolder.ivThumbnail);
        }
        if (PlayListActivity.isEditPressed) {
            System.out.println("ADAPTER DELETE" + PlayListActivity.isEditPressed);
            personViewHolder.ivDetail.setVisibility(View.GONE);
            personViewHolder.checkPlaylist.setVisibility(View.VISIBLE);
        } else {
            System.out.println("ADAPTER DELETE" + PlayListActivity.isEditPressed);
            personViewHolder.checkPlaylist.setVisibility(View.GONE);
            personViewHolder.ivDetail.setVisibility(View.VISIBLE);
        }
        personViewHolder.ivDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playlistTakeoverListener.onPlayListTakeoverClick(current.getPlaylistId(), current.getPlaylistName(), current.getImageUrl());

            }
        });

        personViewHolder.checkPlaylist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                  checkList.add(current.getPlaylistId());
                }else {
                    checkList.remove(position);
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return playList.size();
    }
/*    public void adapterDeleteListner() {
        System.out.println("ADAPTER DELETE");
        if (PlayListActivity.isEditPressed) {
            System.out.println("ADAPTER DELETE" + PlayListActivity.isEditPressed);
            ivDetail.setVisibility(View.GONE);
            checkPlaylist.setVisibility(View.VISIBLE);
        } else {
            System.out.println("ADAPTER DELETE" + PlayListActivity.isEditPressed);
            checkPlaylist.setVisibility(View.GONE);
            ivDetail.setVisibility(View.VISIBLE);
        }
    }*/
}
