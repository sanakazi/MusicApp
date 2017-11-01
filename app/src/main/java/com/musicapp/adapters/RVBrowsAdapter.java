package com.musicapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.HomeItemClickListActivity;
import com.musicapp.activities.SongTakeoverActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.others.ComonHelper;
import com.musicapp.pojos.SearchListItem;
import com.musicapp.pojos.SearchListSectionItem;
import com.musicapp.service.BackgroundSoundService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by PalseeTrivedi on 12/22/2016.
 */
public class RVBrowsAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private ArrayList<SearchListItem> list;
    static Context context;
    String from, playlistName;
    int playlistId;
    private LikeUnlikeSongListener likeUnlikeSongListener;


    public interface LikeUnlikeSongListener {
        void onSongLikeUnlike(int playlistId, String from, String songName, String albumName, String thumbnail, int songId, String like, int songTypeId);

    }

    public RVBrowsAdapter(ArrayList<SearchListItem> list, Context context, String from, int playlistId, String playlistName) {
        this.context = context;
        this.list = list;
        this.from = from;
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        likeUnlikeSongListener = (LikeUnlikeSongListener) context;

    }

    @Override
    public int getSectionCount() {
        return list.size();
    }

    @Override
    public int getItemCount(int section) {

        return list.get(section).getSectionList().size();

    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {


        String sectionName = list.get(section).getCategoryName();
        SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        sectionViewHolder.sectionTitle.setText(sectionName);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, final int relativePosition, int absolutePosition) {

        final ArrayList<SearchListSectionItem> itemsInSection = list.get(section).getSectionList();
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.tvName.setText(itemsInSection.get(relativePosition).getTypeName());
        // itemViewHolder.tvArtist.setText(itemsInSection.get(relativePosition).getArtistName());
        String imgPath = itemsInSection.get(relativePosition).getThumbnailImage();
        int songType = itemsInSection.get(relativePosition).getSongTypeId();

        if (songType == 0) {
            itemViewHolder.ivThumbnailOverlayOpacity.setVisibility(View.GONE);
            itemViewHolder.ivThumbnailOverlay.setVisibility(View.GONE);
        } else if (songType == 1) {
            itemViewHolder.ivThumbnailOverlayOpacity.setVisibility(View.VISIBLE);
            itemViewHolder.ivThumbnailOverlay.setVisibility(View.VISIBLE);
            itemViewHolder.ivThumbnailOverlay.setImageResource(R.drawable.audio_android);
        } else {
            itemViewHolder.ivThumbnailOverlayOpacity.setVisibility(View.VISIBLE);
            itemViewHolder.ivThumbnailOverlay.setVisibility(View.VISIBLE);
            itemViewHolder.ivThumbnailOverlay.setImageResource(R.drawable.video_android);
        }
        if (!imgPath.matches("")) {
            Picasso.with(context).load(imgPath).into(itemViewHolder.ivThumbnail);
        }
        itemViewHolder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int songType = itemsInSection.get(relativePosition).getSongTypeId();
                if (songType == 1) {
                    String songUrl = itemsInSection.get(relativePosition).getSongUrl();
                    if (songUrl.matches("null")) {
                        songUrl = "";
                    }

                    String imageUrl = itemsInSection.get(relativePosition).getThumbnailImage();
                    if (imageUrl.matches("null")) {
                        imageUrl = "";
                    }
                    String description = itemsInSection.get(relativePosition).getDescription();
                    if (description == null || description.matches("null")) {
                        description = "";
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("songUrl", songUrl);
                    bundle.putString("songName", itemsInSection.get(relativePosition).getTypeName());
                    bundle.putString("imageUrl", imageUrl);
                    bundle.putString("description", description);
                    bundle.putString("from", "search");
                    bundle.putBoolean("isDeeplink", false);
                    try {
                        if (ComonHelper.timer != null) {
                            ComonHelper.timer.cancel();
                        }
                        ComonHelper.pauseFlag = false;
                        if (AudioPlayerActivity.isPlaying) {
                            AudioPlayerActivity.timer.cancel();
                            BackgroundSoundService.mPlayer.release();
                            Intent intent = new Intent(context, BackgroundSoundService.class);
                            ((Activity) context).stopService(intent);
                        } else if (AudioPlayerActivity.isPause) {
                            if (AudioPlayerActivity.timer != null) {
                                AudioPlayerActivity.timer.cancel();
                            }
                            if (BackgroundSoundService.mPlayer != null) {
                                BackgroundSoundService.mPlayer.release();
                                Intent intent = new Intent(context, BackgroundSoundService.class);
                                ((Activity) context).stopService(intent);
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ComonHelper.notifFlag = false;
                    Intent i = new Intent(context, AudioPlayerActivity.class);
                    i.putExtras(bundle);
                    ((Activity) context).startActivity(i);
                } else if (songType == 2) {
                    try {
                        if (AudioPlayerActivity.isPlaying) {
                            AudioPlayerActivity.timer.cancel();
                            BackgroundSoundService.mPlayer.release();
                            Intent intent = new Intent(context, BackgroundSoundService.class);
                            ((Activity) context).stopService(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String songUrl = itemsInSection.get(relativePosition).getSongUrl();
                    if (songUrl.matches("null")) {
                        songUrl = "";
                    }

                    String imageUrl = itemsInSection.get(relativePosition).getThumbnailImage();
                    if (imageUrl.matches("null")) {
                        imageUrl = "";
                    }
                    String description = itemsInSection.get(relativePosition).getDescription();
                    if (description == null || description.matches("null")) {
                        description = "";
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("songUrl", songUrl);
                    bundle.putString("songName", itemsInSection.get(relativePosition).getTypeName());
                    bundle.putString("imageUrl", imageUrl);
                    bundle.putString("from", "search");
                    bundle.putString("description", description);
                    bundle.putBoolean("isDeeplink", false);
                    Intent i = new Intent(context, VideoPlayerActivity.class);
                    i.putExtras(bundle);
                    Toast.makeText(context, "Pleasse wait we are preparing", Toast.LENGTH_LONG).show();
                    ((Activity) context).startActivity(i);

                } else {
                    Intent intent = new Intent(context, HomeItemClickListActivity.class);
                    intent.putExtra(HomeItemClickListActivity.CAT_ID, Integer.parseInt(itemsInSection.get(relativePosition).getCategoryId()));
                    intent.putExtra(HomeItemClickListActivity.TYPE_ID, Integer.parseInt(itemsInSection.get(relativePosition).getId()));
                    intent.putExtra(HomeItemClickListActivity.TYPE_NAME, itemsInSection.get(relativePosition).getTypeName());
                    intent.putExtra(HomeItemClickListActivity.IMAGE_URL, itemsInSection.get(relativePosition).getCoverImage());
                    context.startActivity(intent);
                }

            }
        });

        itemViewHolder.ivDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              /*   songId = bundle.getInt("songId");
            playlistId = bundle.getInt("playlistId");
            from = bundle.getString("from");
            albumname = bundle.getString("albumName");
            songName = bundle.getString("songName");
            thumbnail = bundle.getString("thumbnail");*/

            /*    Bundle bundle=new Bundle();
                bundle.putInt("songId",Integer.parseInt(itemsInSection.get(relativePosition).getId()));
                bundle.putInt("playlistId",playlistId);
                bundle.putString("from",from);
                bundle.putString("Like",itemsInSection.get(relativePosition).getLike());
                bundle.putString("albumName",playlistName);
                bundle.putString("songName",itemsInSection.get(relativePosition).getTypeName());
                bundle.putString("thumbnail",itemsInSection.get(relativePosition).getThumbnailImage());
                Intent i=new Intent(context, SongTakeoverActivity.class);
                i.putExtras(bundle);
                context.startActivity(i);*/


                likeUnlikeSongListener.onSongLikeUnlike(playlistId, from, itemsInSection.get(relativePosition).getTypeName(), playlistName,
                        itemsInSection.get(relativePosition).getThumbnailImage(), Integer.parseInt(itemsInSection.get(relativePosition).getId()), itemsInSection.get(relativePosition).getLike(), itemsInSection.get(relativePosition).getSongTypeId());


            }
        });


        //  itemViewHolder.ivThumbnail.setImageResource(R.drawable.download);

        // Try to put a image . for sample i set background color in xml layout file
        // itemViewHolder.itemImage.setBackgroundColor(Color.parseColor("#01579b"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == VIEW_TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_section, parent, false);
            return new SectionViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.raw_item_search_list, parent, false);
            return new ItemViewHolder(v);
        }

    }

    // SectionViewHolder Class for Sections
    public static class SectionViewHolder extends RecyclerView.ViewHolder {


        final TextView sectionTitle;

        public SectionViewHolder(View itemView) {
            super(itemView);

            sectionTitle = (TextView) itemView.findViewById(R.id.tvHeader);


        }
    }

    // ItemViewHolder Class for Items in each Section
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView tvName/*,tvArtist*/;
        LinearLayout ivThumbnailOverlayOpacity;
        final ImageView ivThumbnail, ivThumbnailOverlay, ivDetail;
        final RelativeLayout rlMain;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            //tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            ivThumbnailOverlay = (ImageView) itemView.findViewById(R.id.ivThumbnailOverlay);
            rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
            ivThumbnailOverlayOpacity = (LinearLayout) itemView.findViewById(R.id.ivThumbnailOverlayOpacity);
            ivDetail = (ImageView) itemView.findViewById(R.id.ivDetail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {





                  /*  Intent i=new Intent(context,AudioPlayerActivity.class);
                    ((Activity) context).startActivity(i);*/

                    // Toast.makeText(v.getContext(), tvName.getText(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}