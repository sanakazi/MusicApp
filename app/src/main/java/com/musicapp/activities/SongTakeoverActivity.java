package com.musicapp.activities;

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
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.musicapp.R;
import com.musicapp.adapters.RvPopupPlaylistAdapter;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/18/2017.
 */
public class SongTakeoverActivity extends Activity {
    static int songId;
    static int userId;
    public static int playlistId;
    String from;
    String songName;
    String thumbnail;
    String albumname;
    String Like;
    static String deviceId;
    LinearLayout lnrRemove, lnrAddtoPlaylist,lnrCancel,lnrLikeSong;
    ImageView ivThumbnail,ivLike;
    TextView tvSongName, tvDetail,tvLike;
    static SongTakeoverActivity songTakeoverActivity;
    static ProgressBar progressBar;
    int playlistSize;
    ArrayList<PlaylistItem> list = new ArrayList<PlaylistItem>();
    //for create list popup
    RecyclerView rvPlayList;
    PopupWindow popupWindow;
    RvPopupPlaylistAdapter adapter;
    public static FrameLayout dim_frame;
    ProgressBar popupProgressBar;
    private static final String TAG = SongTakeoverActivity.class.getSimpleName();
    private static Dialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_overtake);
        songTakeoverActivity = this;
        userId = PreferencesManager.getInstance(songTakeoverActivity).getUserId();
        deviceId = PreferencesManager.getInstance(songTakeoverActivity).getDeviceId();
        playlistSize = PreferencesManager.getInstance(songTakeoverActivity).getPlayListSize();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            songId = bundle.getInt("songId");
            playlistId = bundle.getInt("playlistId");
            from = bundle.getString("from");
            albumname = bundle.getString("albumName");
            songName = bundle.getString("songName");
            thumbnail = bundle.getString("thumbnail");
            Like=bundle.getString("Like");

        }

        initialize();
        setupViewAction();
    }


    private void initialize() {
        lnrRemove = (LinearLayout) findViewById(R.id.lnrRemove);
        lnrCancel=(LinearLayout) findViewById(R.id.lnrCancel);
        lnrLikeSong=(LinearLayout) findViewById(R.id.lnrLikeSong);
        if (!from.matches("playlist")) {
            lnrRemove.setVisibility(View.GONE);
        }
        lnrAddtoPlaylist = (LinearLayout) findViewById(R.id.lnrAddtoPlaylist);
        ivThumbnail = (ImageView) findViewById(R.id.ivThumbnail);
        ivLike=(ImageView) findViewById(R.id.ivLike);
        Picasso.with(songTakeoverActivity).load(thumbnail).into(ivThumbnail);
        tvSongName = (TextView) findViewById(R.id.tvSongName);
        tvSongName.setText(songName);
        tvDetail = (TextView) findViewById(R.id.tvDetail);
        tvDetail.setText(albumname);
        tvLike=(TextView) findViewById(R.id.tvLike);

        if (Like.matches("1")){
            ivLike.setImageResource(R.drawable.dislike);
            tvLike.setText("Unlike");
        }else {
            ivLike.setImageResource(R.drawable.like);
            tvLike.setText("Like");
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dim_frame = (FrameLayout) findViewById(R.id.dim_frame);
      //  dim_frame.getForeground().setAlpha(0);
    }


    private void setupViewAction() {
        lnrCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(2);
                finish();
            }
        });

        lnrLikeSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ComonHelper.checkConnection(songTakeoverActivity)){
                    performeLike();
                }else {
                    Toast.makeText(songTakeoverActivity,getResources().getString(R.string.error_no_internet),Toast.LENGTH_LONG).show();
                }
            }
        });

        lnrRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ComonHelper.checkConnection(songTakeoverActivity)) {
                    performeDeletion();
                } else {
                    Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });

        lnrAddtoPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlistSize > 1) {
                    openPopupForlist(v);
                } else if(playlistSize<1){
                    Toast.makeText(songTakeoverActivity,"Please create playlist",Toast.LENGTH_LONG).show();
                }else {
                    if (ComonHelper.checkConnection(songTakeoverActivity)) {
                        addtoPlaylist();
                    } else {
                        Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }


    private void openPopupForlist(View anchorView) {


        dialog = new Dialog(SongTakeoverActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_window_dim);
        dialog.setContentView(R.layout.popup_playlist);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        rvPlayList = (RecyclerView) dialog.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(songTakeoverActivity);
        rvPlayList.setLayoutManager(mLayoutManager);
        popupProgressBar = (ProgressBar) dialog.findViewById(R.id.popupProgressBar);
        if (ComonHelper.checkConnection(songTakeoverActivity)) {
            getPlaylist();
            //  dim_frame.getForeground().setAlpha(220);
        } else {
            Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }

        dialog.show();
    }

   /* private void openPopupForlist(View anchorView) {

        final View popupView = songTakeoverActivity.getLayoutInflater().inflate(R.layout.popup_playlist, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        rvPlayList = (RecyclerView) popupView.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(songTakeoverActivity);
        rvPlayList.setLayoutManager(mLayoutManager);
        popupProgressBar = (ProgressBar) popupView.findViewById(R.id.popupProgressBar);
        if (ComonHelper.checkConnection(songTakeoverActivity)) {
            getPlaylist();
          //  dim_frame.getForeground().setAlpha(220);
        } else {
            Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

    }*/


    public static void addtoPlaylist() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            jsonParams.put("songId", songId);
            jsonParams.put("userPlaylistId", playlistId);
            jsonParams.put("deviceId", deviceId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.addSongtoPlaylist, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        Log.w(TAG,"add to playlist"+ response.toString());
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            String id = response.getString("id");
                            if (id.matches("1")) {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(songTakeoverActivity, PlayListActivity.class);
                                songTakeoverActivity.startActivity(i);
                                songTakeoverActivity.finish();

                            } else {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        System.out.println("Volley submit error" + error);
                        if (null != error.networkResponse) {
                            System.out.println("Volley submit error" + error);
                        }
                    }
                });
        MySingleton.getInstance(songTakeoverActivity).getRequestQueue().add(request);
    }
    private void performeLike(){

    hideKeyboard();
    progressBar.setVisibility(View.VISIBLE);
    JSONObject jsonParams = new JSONObject();
    try {
        jsonParams.put("userId", userId);
        jsonParams.put("songId", songId);
        jsonParams.put("deviceId", deviceId);
        System.out.println("Volley submit param" + jsonParams);
    } catch (JSONException e) {
        e.printStackTrace();
    }


    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.songLike, jsonParams,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)

                {
                    try {
                        //JSONObject jsonObject = new JSONObject(response);
                        String id = response.getString("id");
                        if (id.matches("1")) {
                            Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                            setResult(1);
                                finish();
                        } else {
                            Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }


                }
            },

            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    System.out.println("Volley submit error" + error);
                    if (null != error.networkResponse) {
                        System.out.println("Volley submit error" + error);
                    }
                }
            });
    MySingleton.getInstance(songTakeoverActivity).getRequestQueue().add(request);




}
    private void performeDeletion() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            jsonParams.put("strSongId", songId);
            jsonParams.put("userPlaylistId", playlistId);
            jsonParams.put("deviceId", deviceId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.deletesongfromPlaylist, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            String id = response.getString("id");
                            if (id.matches("1")) {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(songTakeoverActivity, PlayListActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        System.out.println("Volley submit error" + error);
                        if (null != error.networkResponse) {
                            System.out.println("Volley submit error" + error);
                        }
                    }
                });
        MySingleton.getInstance(songTakeoverActivity).getRequestQueue().add(request);
    }

    private void getPlaylist() {
        list.clear();
        hideKeyboard();
        String url = Utility.getplaylist + "UserId=" + userId + "&DeviceId=" + deviceId;
        popupProgressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {
                        Log.w(TAG,response.toString());
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
                                    list.add(playlistItem);
                                }
                                intializeAdapter();
                            } else {
                                Toast.makeText(songTakeoverActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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

        MySingleton.getInstance(songTakeoverActivity).getRequestQueue().add(request);
    }

    private void intializeAdapter() {
        adapter = new RvPopupPlaylistAdapter(list, songTakeoverActivity,songId);
        rvPlayList.setAdapter(adapter);


    }


    private static void hideKeyboard() {
        // Check if no view has focus:
        View view = songTakeoverActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) songTakeoverActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  dim_frame.getForeground().setAlpha(0);
    }
}
