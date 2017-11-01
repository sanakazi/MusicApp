package com.musicapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.SongTakeoverActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/20/2017.
 */
public class RvOfflineSongAdapter extends RecyclerView.Adapter<RvOfflineSongAdapter.PersonViewHolder> {

    static Context context;
    static ArrayList<HomeDetailsJson.DataList> list;
    static ArrayList<HomeDetailsJson.DataList> audio_itemsList;
    static ArrayList<HomeDetailsJson.DataList> video_itemsList;
    ArrayList<PlaylistItem> playlistData = new ArrayList<PlaylistItem>();
    private ImageLoader mImageLoader;
    public static Dialog dialog;
    //for create list popup
    RecyclerView rvPlayList;
    PopupWindow popupWindow;
    RvPopupPlaylistAdapter adapter;
    ProgressBar popupProgressBar;
    static int songId;



    public static class PersonViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView tvName, tvCategory;
        NetworkImageView ivThumbnail;
        ImageView ivDetail;

        PersonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCategory = (TextView) itemView.findViewById(R.id.tvSongCount);
            ivThumbnail = (NetworkImageView) itemView.findViewById(R.id.ivThumbnail);
            ivDetail = (ImageView) itemView.findViewById(R.id.ivDetail);
        }

        @Override
        public void onClick(View v) {
            int position=getPosition();
            songId=list.get(position).getColumns().getSongId();

            final int typeId=list.get(position).getColumns().getSongTypeId();
            if (typeId==1){
                //audio song

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



                Intent intent = new Intent(context, AudioPlayerActivity.class);
                Bundle bund = new Bundle();
                bund.putParcelableArrayList("categories", list);
                bund.putParcelableArrayList("specific_categories", audio_itemsList);
                bund.putInt("index", position);
                bund.putString("des", list.get(position).getColumns().getDescription());
                bund.putString(AudioPlayerActivity.ALBUM_NAME, "");
                bund.putString("from", "home");
                bund.putBoolean("isDeeplink", false);
                intent.putExtras(bund);
                ComonHelper.notifFlag = false;
                context.startActivity(intent);
            }else if (typeId==2){
                //video song
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


                //video song
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                Bundle b = new Bundle();
                b.putParcelableArrayList("categories", list);
                b.putInt("index", position);
                b.putString("from", "home");
                b.putString("album_name", "");
                b.putInt("songId", list.get(position).getColumns().getSongId());
                b.putParcelableArrayList("specific_categories", video_itemsList);
                b.putBoolean("isDeeplink", false);
                b.putString("des", list.get(position).getColumns().getDescription());
                intent.putExtras(b);
                Toast.makeText(context, "Pleasse wait we are preparing", Toast.LENGTH_LONG).show();
                context.startActivity(intent);
                Log.w("Song", "adapter " + list.get(position).getColumns().getSongId());
            }


        }
    }

    public RvOfflineSongAdapter(ArrayList<HomeDetailsJson.DataList> list,ArrayList<HomeDetailsJson.DataList> audio_itemsList,ArrayList<HomeDetailsJson.DataList> video_itemsList, Context context) {
        this.list = list;
        this.context = context;
        this.audio_itemsList=audio_itemsList;
        this.video_itemsList=video_itemsList;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_offline_song, viewGroup, false);
        PersonViewHolder personViewHolder = new PersonViewHolder(v);
        return personViewHolder;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, final int position) {
        final HomeDetailsJson.DataList current = list.get(position);
        personViewHolder.tvName.setText(current.getColumns().getSongName());
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        String imageUrl = current.getColumns().getThumbnailImage();
        if (!imageUrl.matches("") || imageUrl != null) {
            personViewHolder.ivThumbnail.setImageUrl(imageUrl, mImageLoader);
        }
       personViewHolder.ivDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                songId=list.get(position).getColumns().getSongId();

                final int typeId=list.get(position).getColumns().getSongTypeId();

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context,personViewHolder.ivDetail);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                if (typeId==1){
                                    //audio song

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


                                    Intent intent = new Intent(context, AudioPlayerActivity.class);
                                    Bundle bund = new Bundle();
                                    bund.putParcelableArrayList("categories", list);
                                    bund.putParcelableArrayList("specific_categories", audio_itemsList);
                                    bund.putInt("index", position);
                                    bund.putString(AudioPlayerActivity.ALBUM_NAME, "");
                                    bund.putString("from", "home");
                                    bund.putString("des", audio_itemsList.get(position).getColumns().getDescription());
                                    bund.putBoolean("isDeeplink", false);
                                    intent.putExtras(bund);
                                    ComonHelper.notifFlag = false;
                                    context.startActivity(intent);
                                }else if (typeId==2){
                                    //video song
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


                                    //video song
                                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                                    Bundle b = new Bundle();
                                    b.putParcelableArrayList("categories", list);
                                    b.putInt("index", position);
                                    b.putString("from", "home");
                                    b.putString("album_name", "");
                                    b.putInt("songId", list.get(position).getColumns().getSongId());
                                    b.putParcelableArrayList("specific_categories", video_itemsList);
                                    b.putBoolean("isDeeplink", false);
                                    b.putString("des", video_itemsList.get(position).getColumns().getDescription());
                                    intent.putExtras(b);
                                    Toast.makeText(context, "Pleasse wait we are preparing", Toast.LENGTH_LONG).show();
                                    context.startActivity(intent);
                                    Log.w("Song", "adapter " + list.get(position).getColumns().getSongId());
                                }
                                break;
                            case R.id.item2:
                                openPopupForlist(v);
                                break;
                            case R.id.item3:
                                list.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, list.size());

                                //saving the array again to shared preferance after updating
                                Gson gsonStore = new Gson();
                                String jsonOfflineSongStore = gsonStore.toJson(list);
                                PreferencesManager.getInstance(context).saveOfflineSong(jsonOfflineSongStore);

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
    private void openPopupForlist(View anchorView) {


        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_window_dim);
        dialog.setContentView(R.layout.popup_playlist);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        rvPlayList = (RecyclerView) dialog.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvPlayList.setLayoutManager(mLayoutManager);
        popupProgressBar = (ProgressBar) dialog.findViewById(R.id.popupProgressBar);
        if (ComonHelper.checkConnection(context)) {
            getPlaylist();
            //  dim_frame.getForeground().setAlpha(220);
        } else {
            Toast.makeText(context,((Activity)context).getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }

        dialog.show();
    }
    /*private void openPopupForlist(View anchorView) {

       final View popupView = ((Activity)context).getLayoutInflater().inflate(R.layout.popup_playlist, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        rvPlayList = (RecyclerView) popupView.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvPlayList.setLayoutManager(mLayoutManager);
        popupProgressBar = (ProgressBar) popupView.findViewById(R.id.popupProgressBar);
        if (ComonHelper.checkConnection(context)) {
            getPlaylist();

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);


    }*/

    private void getPlaylist() {
        playlistData.clear();
      int  userId = PreferencesManager.getInstance(context).getUserId();
       String deviceId = PreferencesManager.getInstance(context).getDeviceId();
        hideKeyboard();
        String url = Utility.getplaylist + "UserId=" + userId + "&DeviceId=" + deviceId;
        System.out.println("getlist" + url);
        popupProgressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {
                        System.out.println("getlist" + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int id = jsonObject.getInt("id");
                            if (id == 1) {
                                JSONArray jPlaylistArray = jsonObject.getJSONArray("playList");
                                for (int i = 0; i < jPlaylistArray.length(); i++) {
                                    JSONObject jObj = jPlaylistArray.getJSONObject(i);
                                    PlaylistItem playlistItem = new PlaylistItem();
                                    playlistItem.setPlaylistName(jObj.getString("playListName"));
                                    playlistItem.setChecked(false);
                                    playlistItem.setImageUrl("");
                                    playlistItem.setPlaylistId(jObj.getInt("playListId"));
                                    playlistItem.setSongCount(jObj.getString("songCount"));
                                    playlistData.add(playlistItem);
                                }
                                intializeAdapter();
                            } else {
                                dialog.dismiss();
                              //  Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                Toast.makeText(context, jsonObject.getString("Please create playlist"), Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        popupProgressBar.setVisibility(View.GONE);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        popupProgressBar.setVisibility(View.GONE);
                        System.out.println("Volley submit error" + error);
                        if (null != error.networkResponse) {
                            System.out.println("Volley submit error" + error);
                        }
                    }
                });

        MySingleton.getInstance(((Activity) context)).getRequestQueue().add(request);
    }

    private void intializeAdapter() {
        adapter = new RvPopupPlaylistAdapter(playlistData, context,songId);
        rvPlayList.setAdapter(adapter);
    }
    private static void hideKeyboard() {
        // Check if no view has focus:
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) ((Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}