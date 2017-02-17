package com.musicapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.musicapp.R;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PalseeTrivedi on 1/12/2017.
 */

public class PlaylistTakeOverActivity extends Activity {

    LinearLayout lnrCancel, lnrRename, lnrDelete;
    ImageView ivThumbnail;
    TextView tvPlaylistName, tvDetail;
    PlaylistTakeOverActivity playlistTakeOverActivity;
    ProgressBar progressBar;
    String deviceId,playlistName,thumbnail;
    int userId,playlistId;
    private ImageLoader mImageLoader;

    //for create list popup
    EditText edtPopupName;
    TextView tvOk, tvCancel;
    PopupWindow popupWindow;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_takeover);
        playlistTakeOverActivity = this;
        userId = PreferencesManager.getInstance(playlistTakeOverActivity).getUserId();
        deviceId = PreferencesManager.getInstance(playlistTakeOverActivity).getDeviceId();
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            playlistId=bundle.getInt("playlistId");
            playlistName=bundle.getString("playlistName");
            thumbnail=bundle.getString("thumbnail");
        }
        initialize();
        setupViewAction();
    }

    private void initialize() {
        lnrCancel = (LinearLayout) findViewById(R.id.lnrCancel);
        lnrRename = (LinearLayout) findViewById(R.id.lnrRename);
        lnrDelete = (LinearLayout) findViewById(R.id.lnrDelete);
        mImageLoader = MySingleton.getInstance(this).getImageLoader();
        ivThumbnail = (ImageView) findViewById(R.id.ivThumbnail);
        if (!thumbnail.matches("") || thumbnail!=null) {
         //   ivThumbnail.setImageUrl(thumbnail, mImageLoader);
         //   Picasso.with(playlistTakeOverActivity).load(thumbnail).into(ivThumbnail);
        }
        tvPlaylistName = (TextView) findViewById(R.id.tvPlaylistName);
        tvPlaylistName.setText(playlistName);
        tvDetail = (TextView) findViewById(R.id.tvDetail);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
    }

    private void setupViewAction() {
        lnrCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(2);
                finish();
            }
        });

        lnrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ComonHelper.checkConnection(playlistTakeOverActivity)) {
                    performDelete();
                } else {
                    Toast.makeText(playlistTakeOverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });
        lnrRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ComonHelper.checkConnection(playlistTakeOverActivity)) {
                  openPopupforRename(v);
                } else {
                    Toast.makeText(playlistTakeOverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void performDelete() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId",userId);
            jsonParams.put("userPlaylistId",playlistId);
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
                                Toast.makeText(playlistTakeOverActivity, response.getString("message"), Toast.LENGTH_LONG).show();

                              //  Intent i=new Intent();
                                setResult(1);
                                finish();
                            } else {
                                Toast.makeText(playlistTakeOverActivity, response.getString("message"), Toast.LENGTH_LONG).show();

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

    private void openPopupforRename(View anchorView) {

        final View popupView = playlistTakeOverActivity.getLayoutInflater().inflate(R.layout.popup_rename_playlist, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        tvOk = (TextView) popupView.findViewById(R.id.tvOk);
        tvCancel = (TextView) popupView.findViewById(R.id.tvCancel);
        edtPopupName = (EditText) popupView.findViewById(R.id.edtPopupName);
        edtPopupName.setText(playlistName.toString());
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPopupName.getText().toString().trim().matches("")) {
                    edtPopupName.setError(getResources().getString(R.string.error_input_glob));
                } else {

                    if (ComonHelper.checkConnection(playlistTakeOverActivity)) {
                        popupWindow.dismiss();
                        renameList();

                    } else {
                        Toast.makeText(playlistTakeOverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }


                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.CENTER,
                0, 0);

    }

    private void renameList() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("UserId", userId);
            jsonParams.put("PlayListName", edtPopupName.getText().toString().trim());
            jsonParams.put("DeviceId", deviceId);
            jsonParams.put("PlayListId", playlistId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.renamePlaylist, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            int id = response.getInt("id");
                            if (id == 1) {
                                Toast.makeText(playlistTakeOverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                              /* Intent i=new Intent(playlistTakeOverActivity,PlayListActivity.class);
                                startActivity(i);
                                finish();*/
                                setResult(1);
                                finish();

                            } else {
                                Toast.makeText(playlistTakeOverActivity, response.getString("message"), Toast.LENGTH_LONG).show();

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
    
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
