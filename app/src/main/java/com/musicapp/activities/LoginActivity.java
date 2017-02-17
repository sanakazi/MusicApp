package com.musicapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.OfflineArtistItem;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


//submit data is for login through social media
//perform login is for normal login
public class LoginActivity extends AppCompatActivity {
    private TwitterAuthClient client;
    RelativeLayout cardFb, cardTwit;
    EditText edtUserName, edtPwd;
    Button tvBtnLogin;
    LoginActivity loginActivity;
    private static Dialog dialog;
    TextView lnrForgot;
    Toolbar toolbar;

    // ImageView ivBack;

    //facebook login
    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    String userEmail, password = "", registerType, userName, dob, gender, deviceId, profilePic = "", userSocialId;
    private static final String TAG = LoginActivity.class.getSimpleName();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;

    //popup view
    TextView tvOk, tvCancel, edtPopupMail;
    PopupWindow popupWindow;
    String emailVerify;
    ProgressBar popupProgressBar;

    //popup for rset
    PopupWindow popupResetWindow;
    EditText edtOne, edtTwo, edtThree, edtFour, edtFive, edtSix;
    TextView tvResend, tvResetOk;
    String otp;
    ProgressBar resetpopupProgressBar;

    //for recentlyplayed songs
    private ArrayList<HomeDetailsJson.DataList> offlineSongArrayList = new ArrayList<>();
    private ArrayList<OfflineArtistItem> offlineArtistArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region facebook login
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());
                                        System.out.println("FACEBOOKKKK IN TRY" + response.toString());
                                        // Application code
                                        try {
                                            userSocialId = object.getString("id");
                                            String name = object.getString("first_name") + object.getString("last_name");
                                            userName = name;
                                            gender = object.getString("gender");
                                            userEmail = "";
                                            password = "";
                                            registerType = "1";
                                            dob = "";
                                            profilePic = "https://graph.facebook.com/" + userSocialId + "/picture?type=large";

                                            submitdata();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
//endregion
        //main content
        setContentView(R.layout.new_activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //for recentlyplayed songs
        boolean isOfflineSongCreated = PreferencesManager.getInstance(loginActivity).getIsOfflineSongCreated();
        if (!isOfflineSongCreated) {
            Gson gson = new Gson();
            String jsonOfflineSong = gson.toJson(offlineSongArrayList);
            PreferencesManager.getInstance(loginActivity).saveOfflineSong(jsonOfflineSong);
            PreferencesManager.getInstance(loginActivity).saveIsOfflineSongCreated(true);
        }
        boolean isArtistCreated = PreferencesManager.getInstance(loginActivity).getIsOfflineArtistCreated();
        if (!isArtistCreated) {
            Gson gsonArtist = new Gson();
            String jsnArtist = gsonArtist.toJson(offlineArtistArrayList);
            PreferencesManager.getInstance(loginActivity).saveRecentlyPlayedArtist(jsnArtist);
            PreferencesManager.getInstance(loginActivity).saveIsOfflineArtistCreated(true);
        }


/*
        boolean isOfflineArtistCreated = PreferencesManager.getInstance(loginActivity).getIsOfflineArtistCreated();
//for recentlyplayed artist
        if (!isOfflineArtistCreated) {
            Gson gsonArtist = new Gson();
            String jsonOfflineArtist = gsonArtist.toJson(offlineArtistArrayList);
            PreferencesManager.getInstance(loginActivity).saveOfflineArtist(jsonOfflineArtist);
            PreferencesManager.getInstance(loginActivity).saveIsOfflineArtistCreated(true);
        }*/
        loginActivity = this;
        deviceId = Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        PreferencesManager.getInstance(loginActivity).saveDeviceId(deviceId);
        initialize();
        setupViewAction();
    }

    private void initialize() {
        cardFb = (RelativeLayout) findViewById(R.id.cardFb);
        cardTwit = (RelativeLayout) findViewById(R.id.cardTwit);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPwd = (EditText) findViewById(R.id.edtPwd);
        tvBtnLogin = (Button) findViewById(R.id.tvBtnLogin);
        // tvForgotPwd = (TextView) findViewById(R.id.tvForgotPwd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        lnrForgot = (TextView) findViewById(R.id.tvForgotPwd);
        //  ivBack=(ImageView) findViewById(R.id.ivBack);
        initTwitter();
    }

    private void setupViewAction() {
     /*   ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
        lnrForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                openPopupForEmail(v);
            }
        });


        tvBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtUserName.getText().toString().trim().matches("")) {
                    edtUserName.setError(getResources().getString(R.string.error_input_glob));
                    edtUserName.requestFocus();
                } else if (edtUserName.getText().toString().trim().matches("")) {
                    edtUserName.setError(getResources().getString(R.string.error_input_glob));
                    edtUserName.requestFocus();
                } else {
                    if (ComonHelper.checkConnection(loginActivity)) {
                        userName = edtUserName.getText().toString().trim();
                        password = edtPwd.getText().toString().trim();
                        performLogin();
                    } else {
                        Toast.makeText(loginActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        cardFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LoginManager.getInstance().logInWithReadPermissions(loginActivity, Arrays.asList("public_profile", "user_friends"));

            }
        });

     /*   cardTwit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    /*    twitterlogin.onActivityResult(requestCode, resultCode, data);*/
        (new TwitterLoginButton(this)).onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    private void submitdata() {

        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {

            jsonParams.put("userEmail", userEmail);
            jsonParams.put("password", password);
            jsonParams.put("registerType", registerType);
            jsonParams.put("userName", userName);
            jsonParams.put("dob", dob);
            jsonParams.put("gender", gender);
            jsonParams.put("deviceId", deviceId);
            jsonParams.put("profilePic", profilePic);
            if (registerType.matches("1") || registerType.matches("2")) {
                jsonParams.put("uniqueId", userSocialId);
            }
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.register, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        Log.w(TAG, response.toString());
                        try {
                            int id = response.getInt("id");
                            if (id == 1) {
                                Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                PreferencesManager.getInstance(loginActivity).saveDeviceId(deviceId);
                                PreferencesManager.getInstance(loginActivity).saveUserId(response.getInt("userId"));
                                if (registerType.matches("1") || registerType.matches("2")) {
                                    PreferencesManager.getInstance(loginActivity).saveIsSocial(1);
                                }

                                if (response.getString("isNewUser") == "Y") {
                                    Intent i = new Intent(loginActivity, AllGenreStartActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Intent i = new Intent(loginActivity, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }


                            } else if (id == (-101)) {
                                Log.w(TAG, response.toString());
                                showPopup(response.getString("userId"), 1);

                            } else {
                                Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(loginActivity).getRequestQueue().add(request);
       /* RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(request);*/

    }

    private void performLogin() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userName", userName);
            jsonParams.put("password", password);
            jsonParams.put("deviceId", deviceId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.login, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            Log.w("login", response.toString());
                            int id = response.getInt("id");
                            if (id == 1) {
                                Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                PreferencesManager.getInstance(loginActivity).saveDeviceId(deviceId);
                                PreferencesManager.getInstance(loginActivity).saveUserId(response.getInt("userId"));
                               /* editor.putString("userId", response.getString("userId"));
                                editor.commit();*/

                                if (response.getString("isNewUser").equals("Y")) {
                                    Intent i = new Intent(loginActivity, AllGenreStartActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

                                    finish();
                                } else {
                                    Intent i = new Intent(loginActivity, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                                finish();

                            } else if (id == (-101)) {
                                Log.w(TAG, response.toString());
                                showPopup(response.getString("userId"), 0);

                            } else {
                                Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(loginActivity).getRequestQueue().add(request);
       /* RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(request);*/
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void openPopupForEmail(View anchorView) {
        final View popupView = loginActivity.getLayoutInflater().inflate(R.layout.popup_forgot_email, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        tvOk = (TextView) popupView.findViewById(R.id.tvOk);
        tvCancel = (TextView) popupView.findViewById(R.id.tvCancel);
        edtPopupMail = (EditText) popupView.findViewById(R.id.edtPopupMail);
        popupProgressBar = (ProgressBar) popupView.findViewById(R.id.popupProgressBar);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPopupMail.getText().toString().trim().matches("")) {
                    //edtPopupMail.setError("Please fill all mendatory fields");
                    Toast.makeText(loginActivity, "Please fill all mendatory fields", Toast.LENGTH_LONG).show();
                } else {

                    if (ComonHelper.checkConnection(loginActivity)) {
                        performeEmailVerification(v);

                    } else {
                        Toast.makeText(loginActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
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


    public void openResetPasswordPopup(View anchorView) {
        final View popupResetView = loginActivity.getLayoutInflater().inflate(R.layout.popup_reset_password, null);

        popupResetWindow = new PopupWindow(popupResetView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupResetWindow.setBackgroundDrawable(new BitmapDrawable());
        popupResetWindow.setOutsideTouchable(true);
        edtOne = (EditText) popupResetView.findViewById(R.id.edtOne);
        edtTwo = (EditText) popupResetView.findViewById(R.id.edtTwo);
        edtThree = (EditText) popupResetView.findViewById(R.id.edtThree);
        edtFour = (EditText) popupResetView.findViewById(R.id.edtFour);
        edtFive = (EditText) popupResetView.findViewById(R.id.edtFive);
        edtSix = (EditText) popupResetView.findViewById(R.id.edtSix);
        tvResend = (TextView) popupResetView.findViewById(R.id.tvResend);
        tvResetOk = (TextView) popupResetView.findViewById(R.id.tvResetOk);
        resetpopupProgressBar = (ProgressBar) popupResetView.findViewById(R.id.resetpopupProgressBar);
        edtOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("")){
                    edtTwo.requestFocus();
                }

            }
        });

        edtTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("")){
                    edtThree.requestFocus();
                }
            }
        });
        edtThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("")){
                    edtFour.requestFocus();
                }
            }
        });
        edtFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("")){
                    edtFive.requestFocus();
                }
            }
        });

        edtFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("")){
                    edtSix.requestFocus();
                }
            }
        });

        tvResetOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPopupMail.getText().toString().trim().matches("")) {
                    edtPopupMail.setError(getResources().getString(R.string.error_input_glob));
                } else {
                    popupWindow.dismiss();

                    if (edtOne.getText().toString().trim().matches("")) {
                        edtOne.setError(getResources().getString(R.string.error_input_glob));
                        edtOne.requestFocus();
                    } else if (edtTwo.getText().toString().trim().matches("")) {
                        edtTwo.setError(getResources().getString(R.string.error_input_glob));
                        edtTwo.requestFocus();
                    } else if (edtThree.getText().toString().trim().matches("")) {
                        edtThree.setError(getResources().getString(R.string.error_input_glob));
                        edtThree.requestFocus();
                    } else if (edtFour.getText().toString().trim().matches("")) {
                        edtFour.setError(getResources().getString(R.string.error_input_glob));
                        edtFour.requestFocus();
                    } else if (edtFive.getText().toString().trim().matches("")) {
                        edtFive.setError(getResources().getString(R.string.error_input_glob));
                        edtFive.requestFocus();
                    } else if (edtSix.getText().toString().trim().matches("")) {
                        edtSix.setError(getResources().getString(R.string.error_input_glob));
                        edtSix.requestFocus();
                    } else {
                        if (ComonHelper.checkConnection(loginActivity)) {
                            otp = edtOne.getText().toString().trim() +
                                    edtTwo.getText().toString().trim() +
                                    edtThree.getText().toString().trim() +
                                    edtFour.getText().toString().trim() +
                                    edtFive.getText().toString().trim() +
                                    edtSix.getText().toString().trim();
                            sendOtp();
                        } else {
                            Toast.makeText(loginActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                        }

                    }


                }
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ComonHelper.checkConnection(loginActivity)) {
                    popupResetWindow.dismiss();
                    openPopupForEmail(v);
                } else {
                    Toast.makeText(loginActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }

            }
        });

        // If the PopupWindow should be focusable
        popupResetWindow.setFocusable(true);

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupResetWindow.showAtLocation(anchorView, Gravity.CENTER,
                0, 0);
    }

    private void performeEmailVerification(final View anchorView) {
        hideKeyboard();
        popupProgressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userEmail", edtPopupMail.getText().toString().trim());
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.forgotPassword, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        Log.w(TAG, "forgot password URL " + Utility.forgotPassword);
                        Log.w(TAG, "forgot password response " + response);
                        try {
                            int id = response.getInt("id");
                            Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                            if (id == 1) {
                                emailVerify = edtPopupMail.getText().toString().trim();
                                Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                popupWindow.dismiss();
                                openResetPasswordPopup(anchorView);
                            } else {
                                Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();

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

        request.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(loginActivity).getRequestQueue().add(request);

    }

    private void sendOtp() {
        hideKeyboard();
        resetpopupProgressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userEmail", emailVerify);
            jsonParams.put("otp", otp);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.verifyOtp, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            int id = response.getInt("id");
                            Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                            if (id == 1) {
                                Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                PreferencesManager.getInstance(loginActivity).saveUserId(response.getInt("userId"));
                                popupResetWindow.dismiss();
                                Intent i = new Intent(loginActivity, ResetPasswordActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(loginActivity, response.getString("message"), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        resetpopupProgressBar.setVisibility(View.GONE);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resetpopupProgressBar.setVisibility(View.GONE);
                        System.out.println("Volley submit error" + error);
                        if (null != error.networkResponse) {
                            System.out.println("Volley submit error" + error);
                        }
                    }
                });

        MySingleton.getInstance(loginActivity).getRequestQueue().add(request);

    }

    private void initTwitter() {
       /* TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));*/


        cardTwit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new TwitterAuthClient();
                client.authorize(LoginActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        String username = twitterSessionResult.data.getUserName();
                        long userId = twitterSessionResult.data.getUserId();
                        Log.d("Success", "UserName is" + username);
                        Log.d("twitter ", "Id" + userId);
                        long twitter_id = userId;
                        String twitter_name = username;

                        userSocialId = String.valueOf(twitter_id);
                        userName = twitter_name;
                        gender = "";
                        userEmail = "";
                        password = "";
                        registerType = "2";
                        dob = "";
                        profilePic = "";
                        submitdata();
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(LoginActivity.this, "failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void showPopup(final String userId, final int isSocialLogin) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You are already logged in from another device." + "\n" + "Are you sure you want to continue?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Toast.makeText(LoginActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                        continueLoginWebservice(userId, isSocialLogin);
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void continueLoginWebservice(final String userId, final int isSocialLogin) {
        Log.w(TAG, Utility.CONTINUE_LOGIN + "UserId=" + userId + "&DeviceId=" + deviceId);
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.CONTINUE_LOGIN + "UserId=" + userId + "&DeviceId=" + deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("id") == 1) {
                                PreferencesManager.getInstance(loginActivity).saveDeviceId(deviceId);
                                PreferencesManager.getInstance(loginActivity).saveUserId(jsonObject.getInt("userId"));
                                if (isSocialLogin == 1) {
                                    PreferencesManager.getInstance(loginActivity).saveIsSocial(isSocialLogin);
                                }
                                if (jsonObject.getString("isNewUser").equals("Y")) {
                                    Intent intent1 = new Intent(LoginActivity.this, AllGenreStartActivity.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent1);
                                    finish();
                                } else {
                                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent1);
                                    finish();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {


        };

        MySingleton.getInstance(LoginActivity.this).getRequestQueue().add(stringRequest);
    }
}

