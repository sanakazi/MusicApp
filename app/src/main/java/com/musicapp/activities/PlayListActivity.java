package com.musicapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.musicapp.R;
import com.musicapp.adapters.RVBrowsAdapter;
import com.musicapp.adapters.RvPlaylistAdapter;
import com.musicapp.fragments.BrowseFragment;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.musicapp.R.id.lnrBottomCreate;
import static com.musicapp.R.id.tvOk;

/**
 * Created by PalseeTrivedi on 1/11/2017.
 */

public class PlayListActivity extends AppCompatActivity implements RvPlaylistAdapter.PlaylistTakeoverListener {
    PlayListActivity playListActivity;
    RecyclerView rvPlayList;
    LinearLayout lnrtvCreate, lnrBottomCreate, lnrCreateList;
    ProgressBar progressBar;
    RvPlaylistAdapter adapter;
    ArrayList<PlaylistItem> list = new ArrayList<PlaylistItem>();
    ImageView  ivEdit;
    public static boolean isEditPressed = false;
    String deletionString = "";

    //for bottom player
    ImageView ivUp, ivBottomPlay;
    public static RelativeLayout bottomPlayerView;
    SeekBar seekView;
    TextView tvPlayName;

    //for create list popup
    EditText edtPopupName;
    TextView tvCreate, tvCancel;


    int userId;
    String deviceId;
    private static final String TAG = PlayListActivity.class.getSimpleName();
    private static Dialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);
        playListActivity = this;
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Playlist");
        userId = PreferencesManager.getInstance(playListActivity).getUserId();
        deviceId = PreferencesManager.getInstance(playListActivity).getDeviceId();


        initialize();
        setupViewAction();
        bottomPlayerListner();

        if (ComonHelper.checkConnection(playListActivity)) {
            getData();

        } else {
            Toast.makeText(playListActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }


    }

    private void initialize() {
        rvPlayList = (RecyclerView) findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(playListActivity);
        rvPlayList.setLayoutManager(mLayoutManager);
        lnrtvCreate = (LinearLayout) findViewById(R.id.lnrtvCreate);
        lnrBottomCreate = (LinearLayout) findViewById(R.id.lnrBottomCreate);
        lnrCreateList = (LinearLayout) findViewById(R.id.lnrCreateList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ivEdit = (ImageView) findViewById(R.id.ivEdit);
        ivEdit.setImageResource(R.drawable.edit_playlist);
    }

    private void setupViewAction() {

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditPressed) {
                    if (RvPlaylistAdapter.checkList.size()!=0) {
                        deletionString = TextUtils.join(",", RvPlaylistAdapter.checkList);
                        System.out.println("IDDDSSS STRING" + deletionString);
                        if (ComonHelper.checkConnection(playListActivity)) {
                            performeDeletion();
                        } else {
                            Toast.makeText(playListActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(playListActivity,"Please select items to delete",Toast.LENGTH_LONG).show();
                    }

                    isEditPressed = false;
                    ivEdit.setImageResource(R.drawable.edit_playlist);
                 intializeAdapter();
                } else {

                    ivEdit.setImageResource(R.drawable.delete_action);
                    intializeAdapter();
                    isEditPressed = true;
                }
            }
        });
     /*   ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
        lnrCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupCreateList(v);
            }
        });


        lnrBottomCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupCreateList(v);
            }
        });

    }

    private void openPopupCreateList(View anchorView) {

        dialog = new Dialog(PlayListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_window_dim);
        dialog.setContentView(R.layout.popup_create_playlist);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        tvCreate = (TextView) dialog.findViewById(R.id.tvCreate);
        tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        edtPopupName = (EditText) dialog.findViewById(R.id.edtPopupName);
        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPopupName.getText().toString().trim().matches("")) {
                    //   edtPopupName.setError(getResources().getString(R.string.error_input_glob));
                    Toast.makeText(playListActivity, getResources().getString(R.string.error_input_glob), Toast.LENGTH_LONG).show();
                } else {

                    if (ComonHelper.checkConnection(playListActivity)) {
                        //popupWindow.dismiss();
                        createList(v);

                    } else {
                        Toast.makeText(playListActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }


                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void bottomPlayerListner() {
        //for bottom view
        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        seekView = (SeekBar) findViewById(R.id.seekView);
        tvPlayName = (TextView) findViewById(R.id.tvPlayName);
        ivUp = (ImageView) findViewById(R.id.ivUp);
        ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, playListActivity);
        } else {
            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, playListActivity);
            } else {
                bottomPlayerView.setVisibility(View.GONE);
            }
        }


        seekView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (ComonHelper.timer != null) {
                        ComonHelper.timer.cancel();
                        ComonHelper.timer = null;
                    }
                    BackgroundSoundService.mPlayer.seekTo(seekBar.getProgress());
                    ComonHelper.updateSeekProgressTimer(seekBar, PlayListActivity.this);
                    if (AudioPlayerActivity.timer != null) {
                        AudioPlayerActivity.timer.cancel();
                        AudioPlayerActivity.timer = null;
                    }
                    AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                    audioPlayerActivity.updateProgressBar();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void getData() {
        list.clear();
        hideKeyboard();
       final String url = Utility.getplaylist + "UserId="+userId+"&DeviceId="+deviceId;
        System.out.println("URRLLLL"+url);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {
                        Log.w(TAG,"url is " + url.toString());
                        Log.w(TAG, "Response is " + response.toString());
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
                                    playlistItem.setImageUrl(jObj.getString("thumbnailImage"));
                                    playlistItem.setPlaylistId(jObj.getInt("playListId"));
                                    playlistItem.setSongCount(jObj.getString("songCount"));
                                    list.add(playlistItem);

                                }
                                if (list.size() != 0) {
                                    PreferencesManager.getInstance(playListActivity).savePlaylistSize(list.size());
                                    lnrtvCreate.setVisibility(View.GONE);
                                    rvPlayList.setVisibility(View.VISIBLE);
                                    lnrBottomCreate.setVisibility(View.VISIBLE);

                                } else {
                                    lnrtvCreate.setVisibility(View.VISIBLE);
                                    rvPlayList.setVisibility(View.GONE);
                                    lnrBottomCreate.setVisibility(View.GONE);
                                    ivEdit.setVisibility(View.GONE);
                                }
                                intializeAdapter();

                            } else {
                                PreferencesManager.getInstance(playListActivity).savePlaylistSize(0);

                                    lnrtvCreate.setVisibility(View.VISIBLE);
                                    rvPlayList.setVisibility(View.GONE);
                                    lnrBottomCreate.setVisibility(View.GONE);
                                    ivEdit.setVisibility(View.GONE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);

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

        MySingleton.getInstance(playListActivity).getRequestQueue().add(request);
    }

    private void createList(final View v) {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            jsonParams.put("userPlaylistName", edtPopupName.getText().toString().trim());
            jsonParams.put("deviceId", deviceId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.creatPlaylist, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            int id = response.getInt("id");
                            if (id == 1) {
                                Toast.makeText(playListActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                if (ComonHelper.checkConnection(playListActivity)) {
                                    getData();
                                } else {
                                    Toast.makeText(playListActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                                }
                            }else if (id==-1){
                                Toast.makeText(playListActivity, response.getString("message"), Toast.LENGTH_LONG).show();

                            }else {
                                dialog.dismiss();
                                Toast.makeText(playListActivity, response.getString("message"), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);

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

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(request);

    }


    public void performeDeletion(){

        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            jsonParams.put("userPlaylistId",deletionString);
            jsonParams.put("deviceId", deviceId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.deleteplaylist, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            int id = response.getInt("id");
                            if (id == 1) {
                                RvPlaylistAdapter.checkList.clear();
                                Toast.makeText(playListActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                if (ComonHelper.checkConnection(playListActivity)) {
                                    ivEdit.setImageResource(R.drawable.edit_playlist);
                                    isEditPressed=false;
                                    getData();
                                } else {
                                    Toast.makeText(playListActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(playListActivity, response.getString("message"), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);

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

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(request);


    }


    private void intializeAdapter() {
        adapter = new RvPlaylistAdapter(list, playListActivity);
        rvPlayList.setAdapter(adapter);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AudioPlayerActivity.isPlaying) {
            ComonHelper.timer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (isEditPressed){
            isEditPressed = false;
            ivEdit.setImageResource(R.drawable.edit_playlist);
            intializeAdapter();


            /*if (RvPlaylistAdapter.checkList.size()!=0) {
                deletionString = TextUtils.join(",", RvPlaylistAdapter.checkList);
                System.out.println("IDDDSSS STRING" + deletionString);
                if (ComonHelper.checkConnection(playListActivity)) {
                    performeDeletion();
                } else {
                    Toast.makeText(playListActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            }
            isEditPressed = false;
            ivEdit.setImageResource(R.drawable.edit_playlist);*/
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               if (isEditPressed){
                   isEditPressed = false;
                   ivEdit.setImageResource(R.drawable.edit_playlist);
                   intializeAdapter();

               }else {
                   onBackPressed();
               }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2)
        {Log.w(TAG,"result is 2");}
       else if(requestCode==1)
        {
            getData();
        }
    }

    @Override
    public void onPlayListTakeoverClick(int playlistId, String playlistname, String thumbnail) {
        Bundle bundle=new Bundle();
        bundle.putInt("playlistId",playlistId);
        bundle.putString("playlistName",playlistname);
        bundle.putString("thumbnail", thumbnail);
        Intent i = new Intent(PlayListActivity.this, PlaylistTakeOverActivity.class);
        i.putExtras(bundle);
        startActivityForResult(i,1);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bottomPlayerListner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomPlayerListner();
    }
}
