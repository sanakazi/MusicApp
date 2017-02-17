package com.musicapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.musicapp.R;
import com.musicapp.fragments.BrowseFragment;
import com.musicapp.others.ComonHelper;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.singleton.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/13/2017.
 */
public class EditPlaylistActivity extends Activity {
  //  RecyclerView rvList;
    EditPlaylistActivity editPlaylistActivity;
    ProgressBar progressBar;
    ArrayList<PlaylistItem> list = new ArrayList<PlaylistItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_playlist);
        editPlaylistActivity = this;
        initialize();
        setupViewAction();

        if (ComonHelper.checkConnection(editPlaylistActivity)) {
            getData();
        } else {
            Toast.makeText(editPlaylistActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }

    }

    private void initialize() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
     /*   rvList = (RecyclerView) findViewById(R.id.rvList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(editPlaylistActivity);
        rvList.setLayoutManager(mLayoutManager);*/
    }

    private void setupViewAction() {

    }

    private void getData() {

        hideKeyboard();

        for (int i = 0; i < 5; i++) {
            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setPlaylistName("Playlist ABC");
            list.add(playlistItem);

        }
        //intializeAdapter();


     /*  list.clear();
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
      //  String url = Utility.search + "searchText=" + edtSearch.getText().toString().trim();
        StringRequest request = new StringRequest(Request.Method.GET, *//*url*//*"",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {

                        System.out.println("RESPONNNSEEE" + response);

                       *//* try {
                            JSONObject jsonObject = new JSONObject(response);
                            String id = jsonObject.getString("id");
                            if (id.matches("1")) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*//*
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

        MySingleton.getInstance(editPlaylistActivity).getRequestQueue().add(request);*/
    }


    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
