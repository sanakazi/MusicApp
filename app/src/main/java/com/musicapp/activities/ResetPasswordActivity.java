package com.musicapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.musicapp.R;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v7.appcompat.R.styleable.Toolbar;

/**
 * Created by PalseeTrivedi on 12/26/2016.
 */
public class ResetPasswordActivity extends AppCompatActivity {
    EditText edtPwd, edtConfirmPwd;
    Button tvBtnReset;
    ResetPasswordActivity resetPasswordActivity;
ProgressBar progressBar;
    int userId;
    String deviceId;
    android.support.v7.widget.Toolbar toolbar;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        resetPasswordActivity=this;
        userId=PreferencesManager.getInstance(resetPasswordActivity).getUserId();
        deviceId=PreferencesManager.getInstance(resetPasswordActivity).getDeviceId();

        initialize();
        setupViewAction();
    }

    private void initialize(){
        edtPwd=(EditText) findViewById(R.id.edtPwd);
        edtConfirmPwd=(EditText) findViewById(R.id.edtConfirmPwd);
        tvBtnReset=(Button) findViewById(R.id.tvBtnReset);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupViewAction(){

        tvBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPwd.getText().toString().trim().matches("")){
                    edtPwd.setError(getResources().getString(R.string.error_input_glob));
                    edtPwd.requestFocus();
                }else if (edtConfirmPwd.getText().toString().trim().matches("")){
                    edtConfirmPwd.setError(getResources().getString(R.string.error_input_glob));
                    edtConfirmPwd.requestFocus();
                }else if (!edtPwd.getText().toString().trim().matches(edtConfirmPwd.getText().toString().trim())){
                    edtConfirmPwd.setError(getResources().getString(R.string.error_pwd_match));
                    edtConfirmPwd.requestFocus();
                }else {
                    if (ComonHelper.checkConnection(resetPasswordActivity)) {
                        performResetPassword();
                    }else {
                        Toast.makeText(resetPasswordActivity,getResources().getString(R.string.error_no_internet),Toast.LENGTH_LONG).show();
                    }


                }
            }
        });

    }

    private void performResetPassword() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            jsonParams.put("newPassword", edtPwd.getText().toString().trim());
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.newPassword, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            String id = response.getString("id");
                            Toast.makeText(resetPasswordActivity, response.getString("message"), Toast.LENGTH_LONG).show();

                            if (id.matches("1")) {
                                Toast.makeText(resetPasswordActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                PreferencesManager.getInstance(resetPasswordActivity).saveDeviceId(deviceId);
                                Intent i = new Intent(resetPasswordActivity, MainActivity.class);
                                startActivity(i);
                                finish();

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
        MySingleton.getInstance(resetPasswordActivity).getRequestQueue().add(request);
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
