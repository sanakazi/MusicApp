package com.musicapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.musicapp.R;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity  extends AppCompatActivity {
    EditText edtCurPassword, edtNewPwd, edtConfirmPwd;
    CardView crdSubmit;
    ChangePasswordActivity changePassword;
    ProgressBar progressBar;
    RelativeLayout bottomPlayerView;
    SeekBar seekView;
    SharedPreferences sharedPreferences;
    int userId;

    ImageView ivUp, ivBottomPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        changePassword = this;

       /* sharedPreferences=getSharedPreferences("myPrefs",MODE_PRIVATE);
        userId=sharedPreferences.getString("userId","");*/
        userId = PreferencesManager.getInstance(ChangePasswordActivity.this).getUserId();


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.txt_change_password));
        initialize();
        setupViewAction();

        //for bottom player
        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, changePassword);
        } else {
            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, changePassword);
            } else {
                bottomPlayerView.setVisibility(View.GONE);
            }
        }


    }

    private void initialize() {
        edtCurPassword = (EditText) findViewById(R.id.edtCurPassword);
        edtNewPwd = (EditText) findViewById(R.id.edtNewPwd);
        edtConfirmPwd = (EditText) findViewById(R.id.edtConfirmPwd);
        crdSubmit = (CardView) findViewById(R.id.crdSubmit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //    tvTitle = (TextView) findViewById(R.id.tvTitle);
        // tvTitle.setText(getResources().getString(R.string.txt_change_password));

        //for bottom view
        bottomPlayerView=(RelativeLayout) findViewById(R.id.bottomPlayerView);
        seekView=(SeekBar) findViewById(R.id.seekView) ;
        ivUp=(ImageView) findViewById(R.id.ivUp);
        ivBottomPlay=(ImageView) findViewById(R.id.ivBottomPlay);
    }

    private void setupViewAction() {
        crdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtCurPassword.getText().toString().trim().matches("")) {
                    edtCurPassword.setError(getResources().getString(R.string.error_input_glob));
                    edtCurPassword.requestFocus();
                } else if (edtNewPwd.getText().toString().trim().matches("")) {
                    edtNewPwd.setError(getResources().getString(R.string.error_input_glob));
                    edtNewPwd.requestFocus();
                } else if (edtConfirmPwd.getText().toString().trim().matches("")) {
                    edtConfirmPwd.setError(getResources().getString(R.string.error_input_glob));
                    edtConfirmPwd.requestFocus();
                } else if (!edtConfirmPwd.getText().toString().trim().matches(edtNewPwd.getText().toString().trim())) {
                    edtConfirmPwd.setError(getResources().getString(R.string.error_pwd_match));
                    edtConfirmPwd.requestFocus();
                } else {
                    if (ComonHelper.checkConnection(changePassword)) {
                        submitData();
                    } else {
                        Toast.makeText(changePassword, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }
                }


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
                ComonHelper.updateSeekProgressTimer(seekView, changePassword);
                AudioPlayerActivity.timer.cancel();
                AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                audioPlayerActivity.updateProgressBar();
            }
        });


    }

    private void submitData() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            jsonParams.put("oldPassword", edtCurPassword.getText().toString().trim());
            jsonParams.put("newPassword", edtConfirmPwd.getText().toString().trim());
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.resetPwd, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            int id = response.getInt("id");
                            if (id==1) {
                                Toast.makeText(changePassword, response.getString("message"), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else
                            { Toast.makeText(changePassword, response.getString("message"), Toast.LENGTH_LONG).show();}
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
        MySingleton.getInstance(changePassword).getRequestQueue().add(request);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = changePassword.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) changePassword.getSystemService(Context.INPUT_METHOD_SERVICE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }

}
