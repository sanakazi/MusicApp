package com.musicapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.musicapp.R;
import com.musicapp.adapters.RVBrowsAdapter;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.SearchListItem;
import com.musicapp.pojos.SearchListSectionItem;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements RVBrowsAdapter.LikeUnlikeSongListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = SearchActivity.class.getSimpleName();
    View view;
    EditText edtSearch;
    ImageView ivSearch;
    RecyclerView rvSearchList;
    RVBrowsAdapter adapter;
    LinearLayout lnrtvSearch, lnrNoSearch;
    ArrayList<SearchListItem> list = new ArrayList<SearchListItem>();
    ArrayList<SearchListSectionItem> item;
    ProgressBar progressBar;
    String from = "browse", url, deviceId, search;
    int userId, playlistId;
    String playlistName;
    int item_list_value = 0;

    //bottom Player
    public static RelativeLayout bottomPlayerView;
    SeekBar seekView;
    TextView tvPlayName;
    ImageView ivUp;
    public static ImageView ivBottomPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializer();
        userId = PreferencesManager.getInstance(SearchActivity.this).getUserId();
        deviceId = PreferencesManager.getInstance(SearchActivity.this).getDeviceId();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getString("from");
            if (from.matches("playlist")) {
                playlistId = bundle.getInt("playlistId");
                playlistName = bundle.getString("playlistName");
                if (ComonHelper.checkConnection(SearchActivity.this)) {
                    getData();
                } else {
                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            } else if (from.matches("browse")) {
                search = bundle.getString("search");
                if (!search.isEmpty()) {
                    edtSearch.setText(String.valueOf(search));
                    edtSearch.setSelection(edtSearch.getText().length());
                    if (ComonHelper.checkConnection(SearchActivity.this)) {
                        getData();
                    } else {
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        setupViewAction();
        events();
    }

    private void initializer() {
        edtSearch = (EditText) findViewById(R.id.edtSerach);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        lnrNoSearch = (LinearLayout) findViewById(R.id.lnrNoSearch);
        lnrtvSearch = (LinearLayout) findViewById(R.id.lnrtvSearch);
        rvSearchList = (RecyclerView) findViewById(R.id.rvSearchList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        rvSearchList.setLayoutManager(mLayoutManager);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }


    private void events() {
        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
        seekView = (SeekBar) findViewById(R.id.seekView);
        tvPlayName = (TextView) findViewById(R.id.tvPlayName);
        ivUp = (ImageView) findViewById(R.id.ivUp);


        System.out.println("PLAYINGGG" + AudioPlayerActivity.isPlaying);
        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, SearchActivity.this);
        } else {

            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, SearchActivity.this);
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
                    ComonHelper.updateSeekProgressTimer(seekBar, SearchActivity.this);
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

    private void setupViewAction() {


        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edtSearch.getText().toString().trim().matches("")) {
                        edtSearch.setError(getResources().getString(R.string.error_search_input));
                        edtSearch.requestFocus();
                    } else {
                        edtSearch.setError(null);

                        if (ComonHelper.checkConnection(SearchActivity.this)) {
                            getData();
                        } else {
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                        }

                    }
                    return true;
                }
                return false;
            }
        });


        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSearch.getText().toString().trim().matches("")) {
                    edtSearch.setError(getResources().getString(R.string.error_search_input));
                    edtSearch.requestFocus();
                } else {
                    edtSearch.setError(null);

                    if (ComonHelper.checkConnection(SearchActivity.this)) {
                        getData();
                    } else {
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }


                }
            }
        });
    }

    private void getData() {
        item_list_value = 0;
        list.clear();
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        if (from.matches("playlist")) {
            url = Utility.search + "searchText=" + "" + "&UserId=" + userId + "&DeviceId=" + deviceId;

        } else {
            url = Utility.search + "searchText=" + edtSearch.getText().toString().trim() + "&UserId=" + userId + "&DeviceId=" + deviceId;
        }
        Log.w(TAG, "Search url is" + url);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {

                        System.out.println("RESPONNNSEEE" + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String id = jsonObject.getString("id");
                            if (id.matches("1")) {
                                rvSearchList.setVisibility(View.VISIBLE);
                                lnrtvSearch.setVisibility(View.GONE);
                                JSONArray categoryJsonArray = jsonObject.getJSONArray("categories");
                                for (int i = 0; i < categoryJsonArray.length(); i++) {

                                    JSONObject jCategoryObj = categoryJsonArray.getJSONObject(i);
                                    SearchListItem searchListItem = new SearchListItem();
                                    searchListItem.setCategoryName(jCategoryObj.getString("CategoryName"));
                                    searchListItem.setCategoryId(jCategoryObj.getString("CategoryId"));
                                    String categoryId = jCategoryObj.getString("CategoryId");

                                    item = new ArrayList<SearchListSectionItem>();
                                    JSONArray jItemArray = jCategoryObj.getJSONArray("dataList");

                                    for (int j = 0; j < jItemArray.length(); j++) {
                                        item_list_value++;
                                        JSONObject jItemObject = jItemArray.getJSONObject(j);
                                        JSONObject jExtraObj = jItemObject.getJSONObject("columns");
                                        SearchListSectionItem searchListSectionItem = new SearchListSectionItem();
                                        searchListSectionItem.setTypeName(jExtraObj.getString("TypeName"));
                                        searchListSectionItem.setId(jExtraObj.getString("TypeId"));
                                        searchListSectionItem.setSongTypeId(jExtraObj.getInt("SongTypeId"));
                                        searchListSectionItem.setSongUrl(jExtraObj.getString("SongURL"));
                                        searchListSectionItem.setCoverImage(jExtraObj.getString("CoverImage"));
                                        searchListSectionItem.setCategoryId(categoryId);
                                        searchListSectionItem.setDescription("Description");
                                        searchListSectionItem.setLike(jExtraObj.getString("Like"));
                                        String imagePath = jExtraObj.getString("ThumbnailImage");
                                        if (imagePath.matches("") || imagePath == null || imagePath.matches("thumbnailImage")) {
                                            imagePath = "";
                                        }
                                        searchListSectionItem.setThumbnailImage(imagePath);
                                        item.add(searchListSectionItem);
                                    }

                                    searchListItem.setSectionList(item);
                                    list.add(searchListItem);
                                    intializeAdapter();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                System.out.println("Volley submit error" + error);
                if (null != error.networkResponse) {
                    System.out.println("Volley submit error" + error);
                }
            }
        });

        MySingleton.getInstance(SearchActivity.this).getRequestQueue().add(request);


    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = SearchActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void intializeAdapter() {
        Log.w("list size is  ", item_list_value + "");
        if (item_list_value == 0) {
            Log.w("list size is ", "Inside if " + item_list_value);
            lnrNoSearch.setVisibility(View.VISIBLE);
        } else {
            Log.w("list size is ", "Inside else " + item_list_value);
            lnrNoSearch.setVisibility(View.GONE);
        }
        adapter = new RVBrowsAdapter(list, SearchActivity.this, from, playlistId, playlistName);
        rvSearchList.setAdapter(adapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                //  overridePendingTransition(0, R.anim.push_right);
                break;
        }
        return true;
    }

    @Override
    public void onSongLikeUnlike(int playlistId, String from, String songName, String albumName, String thumbnail, int songId, String like, int songTypeId) {
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


        Bundle bundle = new Bundle();
        bundle.putInt("playlistId", playlistId);
        bundle.putString("from", from);
        bundle.putString("songName", songName);
        bundle.putString("albumName", albumName);
        bundle.putString("thumbnail", thumbnail);
        bundle.putInt("songId", songId);
        bundle.putString("Like", like);
        bundle.putInt("type", songTypeId);
        Intent i = new Intent(SearchActivity.this, SongTakeoverActivity.class);
        i.putExtras(bundle);
        startActivityForResult(i, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            Log.w(TAG, "result is 2");
        } else if (requestCode == 1) {
            Log.w(TAG, "request is 1");
            getData();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("RESTART CALLLED");
        events();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("RESUME  CALLLED");
        events();
    }
}
