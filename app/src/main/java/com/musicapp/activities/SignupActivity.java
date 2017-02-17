package com.musicapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.OfflineArtistItem;
import com.musicapp.singleton.MySingleton;
import com.musicapp.others.Utility;
import com.musicapp.singleton.PreferencesManager;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by PalseeTrivedi on 12/21/2016.
 */

public class SignupActivity extends AppCompatActivity {
    private TwitterAuthClient client;
    RelativeLayout cardFb, cardTwit;
    TwitterLoginButton login_twitter;
    EditText edtEmail, edtPwd, edtUserName, edtDob, edtGender;
    TextView tvTermCon, tvBtnSignup;
    SignupActivity signupActivity;
    RelativeLayout rlTerm;
    private static final String TAG = SignupActivity.class.getSimpleName();
    int temp_social_var = 0;
   // ImageView ivBack;

    //facebook login
    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    String userEmail, password = "", registerType, userName, dob, gender, deviceId = "", profilePic = "", userSocialId;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ProgressBar progressBar;
    Toolbar toolbar;


    Calendar myCalendar;
    private DatePickerDialog fromDatePickerDialog;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");


    //for recentlyplayed songs
    private ArrayList<HomeDetailsJson.DataList> offlineSongArrayList = new ArrayList<>();
    private ArrayList<OfflineArtistItem> offlineArtistArrayList = new ArrayList<>();
    String termsCondition, privacyPolicy;

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
                                        Log.v("SignupActivity", response.toString());
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
                        Toast.makeText(SignupActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SignupActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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

        //activity content
        setContentView(R.layout.signup_new_activity);

        signupActivity = this;
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                Intent i=new Intent(signupActivity,AppIntroActivityNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });

        deviceId = Secure.getString(SignupActivity.this.getContentResolver(),
                Secure.ANDROID_ID);


        //for recentlyplayed songs
        Gson gsonSong = new Gson();
        String jsonOfflineSong = gsonSong.toJson(offlineSongArrayList);
        PreferencesManager.getInstance(signupActivity).saveOfflineSong(jsonOfflineSong);
        PreferencesManager.getInstance(signupActivity).saveIsOfflineSongCreated(true);


        // for recently played artist

        Gson artistGson=new Gson();
        String strForArtist=artistGson.toJson(offlineArtistArrayList);
        PreferencesManager.getInstance(signupActivity).saveRecentlyPlayedArtist(strForArtist);
        PreferencesManager.getInstance(signupActivity).saveIsOfflineArtistCreated(true);




    /*    //for recentlyplayed artist
        Gson gsonArtist = new Gson();
        String jsonOfflineArtist = gsonArtist.toJson(offlineArtistArrayList);
        PreferencesManager.getInstance(signupActivity).saveOfflineArtist(jsonOfflineArtist);
        PreferencesManager.getInstance(signupActivity).saveIsOfflineArtistCreated(true);*/


        initialize();
        setupViewAction();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }



      /*  // region get keyhash
        try
        {

            PackageInfo info = getPackageManager().getPackageInfo( "com.musicapp",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());
                Log.w("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));//will give developer key hash

              //  Toast.makeText(getApplicationContext(),Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show(); //will give app key hash or release key hash

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        //endregion*/


    }

    private void initialize() {
        hideKeyboard();
        cardFb = (RelativeLayout) findViewById(R.id.cardFb);
        cardTwit = (RelativeLayout) findViewById(R.id.cardTwit);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPwd = (EditText) findViewById(R.id.edtPwd);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtDob = (EditText) findViewById(R.id.edtDob);
        edtGender = (EditText) findViewById(R.id.edtGender);
        tvTermCon = (TextView) findViewById(R.id.tvTermCon);
        tvBtnSignup = (TextView) findViewById(R.id.tvBtnSignup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
      //  ivBack=(ImageView) findViewById(R.id.ivBack);
        rlTerm=(RelativeLayout) findViewById(R.id.rlTerm);
        initTwitter();
    }

    private void setupViewAction() {
      /*  rlTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(signupActivity,TermsActivity.class);
            }
        });*/

        tvTermCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTermsConditionsData();
            }
        });

      /*  ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                Intent i=new Intent(signupActivity,AppIntroActivityNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });*/

        edtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                final String[] items = getResources().getStringArray(R.array.str_array_gender);

                AlertDialog.Builder builder = new AlertDialog.Builder(signupActivity);
                builder.setTitle("Select Gender");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        edtGender.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                fromDatePickerDialog.show();
            }
        });

        myCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(signupActivity, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar = Calendar.getInstance();
                myCalendar.set(year, monthOfYear, dayOfMonth);
                final Calendar c = Calendar.getInstance();
                if (myCalendar.compareTo(c) > 0) {
                    Toast.makeText(signupActivity,
                            "Invalid Date!", Toast.LENGTH_SHORT).show();
                } else if (myCalendar.compareTo(c) == 0) {
                    Toast.makeText(signupActivity,
                            "Invalid Date!", Toast.LENGTH_SHORT).show();
                } else {
                    edtDob.setText(dateFormatter.format(myCalendar
                            .getTime()));
                }

            }
        }, myCalendar.get(Calendar.YEAR), myCalendar
                .get(Calendar.MONTH), myCalendar
                .get(Calendar.DAY_OF_MONTH));


        tvBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if(!isValidEmail(edtEmail.getText().toString()))
                {
                    edtEmail.setError(getResources().getString(R.string.error_input_invalid));
                }

                else if (edtEmail.getText().toString().trim().matches("")) {
                    edtEmail.setError(getResources().getString(R.string.error_input_glob));
                    edtEmail.requestFocus();
                } else if (edtPwd.getText().toString().trim().matches("")) {
                    edtPwd.setError(getResources().getString(R.string.error_input_glob));
                    edtPwd.requestFocus();
                } else if (edtUserName.getText().toString().trim().matches("")) {
                    edtUserName.setError(getResources().getString(R.string.error_input_glob));
                    edtUserName.requestFocus();
                } else if (edtGender.getText().toString().trim().matches("")) {
                    edtGender.setError(getResources().getString(R.string.error_input_glob));
                    edtGender.requestFocus();
                } else {
                    if (ComonHelper.checkConnection(signupActivity)) {
                        userName = edtUserName.getText().toString().trim();
                        gender = edtGender.getText().toString().trim();
                        userEmail = edtEmail.getText().toString().trim();
                        password = edtPwd.getText().toString().trim();
                        registerType = "0";
                        dob = edtDob.getText().toString().trim();
                        profilePic = "";
                        System.out.println("Volley submit param" + userName + " " + gender + " " + userEmail + " " + password + " " + registerType + " " + dob + " " + profilePic);
                        submitdata();
                    } else {
                        Toast.makeText(signupActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        cardFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ComonHelper.checkConnection(signupActivity)) {
                    LoginManager.getInstance().logInWithReadPermissions(signupActivity, Arrays.asList("public_profile", "user_friends"));
                } else {
                    Toast.makeText(signupActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                        System.out.println("Volley submit error" + response.toString());
                        try {
                            String id = response.getString("id");
                            Toast.makeText(signupActivity, response.getString("message"), Toast.LENGTH_LONG).show();

                            if (id.matches("1")) {
                                Toast.makeText(signupActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                PreferencesManager.getInstance(signupActivity).saveDeviceId(deviceId);
                                PreferencesManager.getInstance(signupActivity).saveUserId(response.getInt("userId"));
                                if (registerType.matches("1") || registerType.matches("2")) {
                                    PreferencesManager.getInstance(signupActivity).saveIsSocial(1);
                                    temp_social_var = 1;
                                }

                                if (response.getString("isNewUser").equals("Y")) {
                                    Intent i = new Intent(signupActivity, AllGenreStartActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Intent i = new Intent(signupActivity, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }

                            } else if (id.matches("-101")) {
                                Log.w(TAG, response.toString());
                                showPopup(response.getString("userId"), temp_social_var);
                            } else {
                                Toast.makeText(signupActivity, response.getString("message"), Toast.LENGTH_LONG).show();
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

    /*RequestQueue queue = Volley.newRequestQueue(this);

    queue.add(request);*/
        MySingleton.getInstance(signupActivity).getRequestQueue().add(request);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void initTwitter() {
       /* TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));*/


        cardTwit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new TwitterAuthClient();
                client.authorize(SignupActivity.this, new Callback<TwitterSession>() {
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
                        Toast.makeText(SignupActivity.this, "failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void showPopup(final String userId, final int isSocialLogin) {

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
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

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
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
                                PreferencesManager.getInstance(signupActivity).saveDeviceId(deviceId);
                                PreferencesManager.getInstance(signupActivity).saveUserId(jsonObject.getInt("userId"));
                                if (isSocialLogin == 1) {
                                    PreferencesManager.getInstance(signupActivity).saveIsSocial(isSocialLogin);
                                }
                                if (jsonObject.getString("isNewUser").equals("Y")) {
                                    Intent intent1 = new Intent(SignupActivity.this, AllGenreStartActivity.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent1);
                                    finish();
                                } else {
                                    Intent intent1 = new Intent(SignupActivity.this, MainActivity.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent1);
                                    finish();
                                }
                            } else {
                                Toast.makeText(SignupActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignupActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {


        };

        MySingleton.getInstance(SignupActivity.this).getRequestQueue().add(stringRequest);
    }

    private void switchActivity(String newUser) {
        if (newUser.equals("Y")) {
        } else {
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void getTermsConditionsData() {
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


                                Bundle bundle = new Bundle();
                                bundle.putString("term", termsCondition);
                                bundle.putString("type", "term");
                                Intent i = new Intent(SignupActivity.this, TermsActivity.class);
                                i.putExtras(bundle);
                                startActivity(i);


                            } else {
                                Toast.makeText(SignupActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(SignupActivity.this).getRequestQueue().add(request);
    }


}
