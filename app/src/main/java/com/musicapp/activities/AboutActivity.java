package com.musicapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by PalseeTrivedi on 12/29/2016.
 */

public class AboutActivity extends AppCompatActivity {

    RelativeLayout bottomPlayerView; SeekBar seekView; TextView tvPlayName; ImageView ivUp;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rlTerm)
    RelativeLayout rlTerm;
    @Bind(R.id.rlPrivacy)
    RelativeLayout rlPrivacy;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    public static ImageView ivBottomPlay;
    AboutActivity aboutActivity;

    String termsCondition, privacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        aboutActivity = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");
        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        ivBottomPlay=(ImageView) findViewById(R.id.ivBottomPlay);
        seekView = (SeekBar)findViewById(R.id.seekView);
        tvPlayName=(TextView) findViewById(R.id.tvPlayName);
        ivUp=(ImageView) findViewById(R.id.ivUp);
        setupViewAction();
        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, aboutActivity);
        } else {
            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, aboutActivity);
            } else {
                bottomPlayerView.setVisibility(View.GONE);
            }
        }
        if (ComonHelper.checkConnection(aboutActivity)) {

            getData();

        } else {
            Toast.makeText(aboutActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }

    }


    private void setupViewAction() {

        rlTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("term", termsCondition);
                bundle.putString("type", "term");
                Intent i = new Intent(aboutActivity, TermsActivity.class);
                i.putExtras(bundle);
                startActivityForResult(i, 0);
            }
        });

        rlPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("privacy", privacyPolicy);
                bundle.putString("type", "privacy");
                Intent i = new Intent(aboutActivity, TermsActivity.class);
                i.putExtras(bundle);
                startActivityForResult(i, 0);
            }
        });
        seekView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BackgroundSoundService.mPlayer.seekTo(seekView.getProgress());
                ComonHelper.timer.cancel();
                ComonHelper.updateSeekProgressTimer(seekView, aboutActivity);
                AudioPlayerActivity.timer.cancel();
                AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                audioPlayerActivity.updateProgressBar();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AudioPlayerActivity.isPlaying) {
            ComonHelper.timer.cancel();
        }
    }

    private void getData() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, Utility.term,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String id = jsonObject.getString("id");
                            if (id.matches("1")) {
                                termsCondition = jsonObject.getString("termAndCondition");
                                privacyPolicy = jsonObject.getString("privacyPolicy");

                            } else {
                                Toast.makeText(aboutActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(aboutActivity).getRequestQueue().add(request);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = aboutActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) aboutActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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

}
