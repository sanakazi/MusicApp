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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.SongsLikedActivity;
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
 * Created by SanaKazi on 1/23/2017.
 */
public class SongsLikedAdapter extends RecyclerView.Adapter<SongsLikedAdapter.SingleItemRowHolder> {

    private ArrayList<HomeDetailsJson.DataList> itemsList;
    static ArrayList<HomeDetailsJson.DataList> audio_itemsList;
    static ArrayList<HomeDetailsJson.DataList> video_itemsList;
    ArrayList<PlaylistItem> playlistData = new ArrayList<PlaylistItem>();
    private static Context mContext;
    int categoryId;
    private ImageLoader mImageLoader;
    private static final String TAG = SongsLikedAdapter.class.getSimpleName();
   // SongsLikedClickListener mListener;
    //for create list popup
   public static Dialog dialog;
    RecyclerView rvPlayList;
    PopupWindow popupWindow;
    RvPopupPlaylistAdapter adapter;
    ProgressBar popupProgressBar;
    int songId,posforUnlike;
  /*  public interface SongsLikedClickListener {
        void onSongClick(int position,int catId, int typeId, String typeName, String coverImage);
    }*/

    public SongsLikedAdapter(Context context, ArrayList<HomeDetailsJson.DataList> itemsList,int categoryId,ArrayList<HomeDetailsJson.DataList> video_itemsList,ArrayList<HomeDetailsJson.DataList> audio_itemsList ) {
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        this.itemsList = itemsList;
        this.mContext = context;
        this.categoryId =categoryId;
        this.video_itemsList=video_itemsList;
        this.audio_itemsList=audio_itemsList;
        //mListener=(SongsLikedClickListener) context;
        Log.w(TAG, itemsList.size()+ " ");

    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_songs_liked, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {


        holder.latest_song_name.setText(itemsList.get(i).getColumns().getTypeName());
      //  holder.latest_song_details.setText(String.valueOf(itemsList.get(i).getColumns().getSongCount()) + " Songs");
        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int typeId=itemsList.get(i).getColumns().getSongTypeId();
                System.out.println("SIZE OF LISSSTTT"+audio_itemsList.size());
                if (typeId == 1) {
                    //audio song
                    Intent intent = new Intent(mContext, AudioPlayerActivity.class);
                    Bundle bund = new Bundle();
                    bund.putParcelableArrayList("categories", itemsList);
                    bund.putParcelableArrayList("specific_categories", audio_itemsList);
                    bund.putInt("index", i);
                    bund.putString(AudioPlayerActivity.ALBUM_NAME, "");
                    bund.putString("from", "home");
                    intent.putExtras(bund);
                    ((Activity) mContext).startActivity(intent);
                } else if (typeId == 2) {
                    //video song
                    if (AudioPlayerActivity.isPlaying) {
                        AudioPlayerActivity.timer.cancel();
                        BackgroundSoundService.mPlayer.release();
                        Intent intent = new Intent(mContext, BackgroundSoundService.class);
                        ((Activity) mContext).stopService(intent);
                    }

                    //video song
                    Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelableArrayList("categories", itemsList);
                    b.putInt("index", i);
                    b.putString("from", "home");
                    b.putString("album_name", "");
                    b.putInt("songId", itemsList.get(i).getColumns().getSongId());
                    b.putParcelableArrayList("specific_categories", video_itemsList);
                    intent.putExtras(b);
                    Toast.makeText(mContext, "Pleasse wait we are preparing", Toast.LENGTH_LONG).show();
                    ((Activity) mContext).startActivity(intent);
                    Log.w("Song", "adapter " + itemsList.get(i).getColumns().getSongId());
                }

                //Log.w(TAG, categoryId + " , " + itemsList.get(i).getColumns().getTypeId() + " , " + itemsList.get(i).getColumns().getTypeName() + " , " + itemsList.get(i).getColumns().getCoverImage());
                //mListener.onSongClick(i,categoryId, itemsList.get(i).getColumns().getTypeId(), itemsList.get(i).getColumns().getTypeName(), itemsList.get(i).getColumns().getCoverImage());
            }
        });
        holder.latest_song_img.setDefaultImageResId(R.drawable.playlist_music_white);
        holder.latest_song_img.setImageUrl(itemsList.get(i).getColumns().getThumbnailImage(), mImageLoader);
        holder.latest_song_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                songId = itemsList.get(i).getColumns().getSongId();

                final int typeId = itemsList.get(i).getColumns().getSongTypeId();

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.latest_song_edit);
                //inflating menu from xml resource
                popup.inflate(R.menu.option_menu_for_liked_song);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                if (typeId == 1) {
                                    //audio song
                                    Intent intent = new Intent(mContext, AudioPlayerActivity.class);
                                    Bundle bund = new Bundle();
                                    bund.putParcelableArrayList("categories", itemsList);
                                    bund.putParcelableArrayList("specific_categories", audio_itemsList);
                                    bund.putInt("index", i);
                                    bund.putString(AudioPlayerActivity.ALBUM_NAME, "");
                                    bund.putString("from", "home");
                                    intent.putExtras(bund);
                                } else if (typeId == 2) {
                                    //video song
                                    if (AudioPlayerActivity.isPlaying) {
                                        AudioPlayerActivity.timer.cancel();
                                        BackgroundSoundService.mPlayer.release();
                                        Intent intent = new Intent(mContext, BackgroundSoundService.class);
                                        ((Activity) mContext).stopService(intent);
                                    }

                                    //video song
                                    Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                                    Bundle b = new Bundle();
                                    b.putParcelableArrayList("categories", itemsList);
                                    b.putInt("index", i);
                                    b.putString("from", "home");
                                    b.putString("album_name", "");
                                    b.putInt("songId", itemsList.get(i).getColumns().getSongId());
                                    b.putParcelableArrayList("specific_categories", video_itemsList);
                                    intent.putExtras(b);
                                    Toast.makeText(mContext, "Pleasse wait we are preparing", Toast.LENGTH_LONG).show();
                                    mContext.startActivity(intent);
                                    Log.w("Song", "adapter " + itemsList.get(i).getColumns().getSongId());
                                }
                                break;
                            case R.id.item2:
                                openPopupForlist(v);
                                break;
                            case R.id.item3:
                                songId=itemsList.get(i).getColumns().getSongId();
                                posforUnlike=i;
                                performeUnlike();
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

        protected TextView latest_song_name, latest_song_details;
        protected LinearLayout main_layout;
        protected ImageView latest_song_edit;
        protected NetworkImageView latest_song_img;

        public SingleItemRowHolder(View view) {
            super(view);

            this.latest_song_name = (TextView) view.findViewById(R.id.latest_song_name);
        //    this.latest_song_details = (TextView) view.findViewById(R.id.latest_song_details);
            this.latest_song_img = (NetworkImageView) view.findViewById(R.id.latest_song_img);
            this.main_layout = (LinearLayout) view.findViewById(R.id.main_layout);
            this.latest_song_edit = (ImageView) view.findViewById(R.id.latest_song_edit);

        }

    }

    private void openPopupForlist(View anchorView) {


        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_window_dim);
        dialog.setContentView(R.layout.popup_playlist);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        rvPlayList = (RecyclerView) dialog.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvPlayList.setLayoutManager(mLayoutManager);
        popupProgressBar = (ProgressBar) dialog.findViewById(R.id.popupProgressBar);
        if (ComonHelper.checkConnection(mContext)) {
            getPlaylist();
            //  dim_frame.getForeground().setAlpha(220);
        } else {
            Toast.makeText(mContext,((Activity)mContext).getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }

        dialog.show();
    }



   /* private void openPopupForlist(View anchorView) {

        final View popupView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_playlist, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        rvPlayList = (RecyclerView) popupView.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvPlayList.setLayoutManager(mLayoutManager);
        popupProgressBar = (ProgressBar) popupView.findViewById(R.id.popupProgressBar);
        if (ComonHelper.checkConnection(mContext)) {
            getPlaylist();

        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
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
        int  userId = PreferencesManager.getInstance(mContext).getUserId();
        String deviceId = PreferencesManager.getInstance(mContext).getDeviceId();
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
                                Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                               // Toast.makeText(mContext, "Please create playlist", Toast.LENGTH_LONG).show();

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

        MySingleton.getInstance(((Activity) mContext)).getRequestQueue().add(request);
    }

    private void intializeAdapter() {
        adapter = new RvPopupPlaylistAdapter(playlistData, mContext,songId);
        rvPlayList.setAdapter(adapter);
    }

    private void performeUnlike() {
        int  userId = PreferencesManager.getInstance(mContext).getUserId();
        String deviceId = PreferencesManager.getInstance(mContext).getDeviceId();
        hideKeyboard();
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("deviceId", deviceId);
            jsonParams.put("songId", songId);
            jsonParams.put("userId", userId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.songLike, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        System.out.println("getlist" + response.toString());
                        try {
                            int id = response.getInt("id");
                            if (id == 1) {
                                Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();

                                itemsList.remove(posforUnlike);
                                notifyItemRemoved(posforUnlike);
                                notifyItemRangeChanged(posforUnlike, itemsList.size());
                            } else {
                                // Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("Volley submit error" + error);
                        if (null != error.networkResponse) {
                            System.out.println("Volley submit error" + error);
                        }
                    }
                });

        MySingleton.getInstance(((Activity) mContext)).getRequestQueue().add(request);
    }




    private static void hideKeyboard() {
        // Check if no view has focus:
        View view = ((Activity) mContext).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) ((Activity) mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
