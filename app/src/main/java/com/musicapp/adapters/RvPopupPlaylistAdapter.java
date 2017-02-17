package com.musicapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.musicapp.R;
import com.musicapp.activities.PlayListActivity;
import com.musicapp.activities.SongTakeoverActivity;
import com.musicapp.others.Utility;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/18/2017.
 */
public class RvPopupPlaylistAdapter extends RecyclerView.Adapter<RvPopupPlaylistAdapter.PersonViewHolder> {

    static Context context;
    static ArrayList<PlaylistItem> playList;
    int size;
    Typeface Roboto_Regular;
    static int songId;
    static int userId;
    static int playlistId;
    static String deviceId;



    public static class PersonViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView tvName;
        PersonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }

        @Override
        public void onClick(View v) {
            int position = getPosition();
            if (SongTakeoverActivity.dim_frame!=null) {
              //  SongTakeoverActivity.dim_frame.getForeground().setAlpha(0);
                SongTakeoverActivity.playlistId=playList.get(position).getPlaylistId();
                SongTakeoverActivity.addtoPlaylist();
            }else {
                playlistId=playList.get(position).getPlaylistId();
                performAddtoPlaylist();

            }


        }
    }

    public RvPopupPlaylistAdapter(ArrayList<PlaylistItem> playList, Context context,int songId) {
        this.playList = playList;
        this.context = context;
        this.songId=songId;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_popup_playlist, viewGroup, false);
        PersonViewHolder personViewHolder = new PersonViewHolder(v);
        return personViewHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int position) {
        final PlaylistItem current = playList.get(position);
        personViewHolder.tvName.setText(current.getPlaylistName());
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }


    public static void performAddtoPlaylist() {
        userId = PreferencesManager.getInstance(context).getUserId();
        deviceId = PreferencesManager.getInstance(context).getDeviceId();
        hideKeyboard();
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
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            String id = response.getString("id");
                            if (id.matches("1")) {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(context, PlayListActivity.class);
                                context.startActivity(i);
                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(((Activity) context)).getRequestQueue().add(request);
    }

    private static void hideKeyboard() {
        // Check if no view has focus:
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) ((Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
