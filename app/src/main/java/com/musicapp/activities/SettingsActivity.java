package com.musicapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.musicapp.R;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    RelativeLayout btn_account, btn_about;
    Button btn_logout;
    ProgressBar progressBar;
    int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_account = (RelativeLayout) findViewById(R.id.btn_account);
        btn_about = (RelativeLayout) findViewById(R.id.btn_about);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userId = PreferencesManager.getInstance(this).getUserId();
        events();

    }

    private void events()
    {
        btn_account.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
            startActivity(intent);
        }
    });
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SettingsActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ComonHelper.checkConnection(SettingsActivity.this)){
                    performeLogout();
                }else {
                    Toast.makeText(SettingsActivity.this,getResources().getString(R.string.error_no_internet),Toast.LENGTH_LONG).show();
                }



            }
        });
    }


    private void performeLogout(){
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.logout, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        System.out.println("Volley submit param" + response.toString());
                        try {
                            String id = response.getString("id");
                            if (id.matches("1")) {

                                if(PreferencesManager.getInstance( SettingsActivity.this).isSocialLogin()==1) {
                                    FacebookSdk.sdkInitialize(getApplicationContext());
                                    LoginManager.getInstance().logOut();
                                    TwitterCore.getInstance().logOut();
                                }

                                PreferencesManager.getInstance( SettingsActivity.this).clearUserPreferences();
                                if(!PreferencesManager.getInstance( SettingsActivity.this).isLoggedIn()) {
                                    Intent intent = new Intent( SettingsActivity.this, AppIntroActivityNew.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    SettingsActivity.this.finish();
                                }
                                Toast.makeText(SettingsActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            { Toast.makeText(SettingsActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();}
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
        MySingleton.getInstance(SettingsActivity.this).getRequestQueue().add(request);
    }

    private void hideKeyboard() {
        // Check if no   has focus:
        View v  = SettingsActivity.this.getCurrentFocus();
        if ( v != null) {
            InputMethodManager inputManager = (InputMethodManager)SettingsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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