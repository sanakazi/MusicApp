package com.musicapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.musicapp.R;
import com.musicapp.adapters.RvPopupPlaylistAdapter;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.T4JTwitterFunctions;
import com.musicapp.others.Utility;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by PalseeTrivedi on 1/18/2017.
 */
public class SongTakeoverActivity extends Activity {
    static int songId, songTypeId;
    static int userId;
    public static int playlistId;
    String from;
    String songName;
    String thumbnail;
    String albumname;
    String Like;
    static String deviceId;
    LinearLayout lnrRemove, lnrAddtoPlaylist, lnrCancel, lnrLikeSong, lnrShare, lnrPost;
    ImageView ivThumbnail, ivLike;
    TextView tvSongName, tvDetail, tvLike;
    static SongTakeoverActivity songTakeoverActivity;
    static ProgressBar progressBar;
    int playlistSize;
    ArrayList<PlaylistItem> list = new ArrayList<PlaylistItem>();
    //for create list popup
    RecyclerView rvPlayList;
    PopupWindow popupWindow;
    RvPopupPlaylistAdapter adapter;
    public static FrameLayout dim_frame;
    ProgressBar popupProgressBar;
    private static final String TAG = SongTakeoverActivity.class.getSimpleName();
    private static Dialog dialog;

    //for facebook post
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    //for twitter post
    boolean isLogedInTwitter = false;
    Twitter twitter;
    static String TWITTER_CONSUMER_KEY = "OwC5JfQ4eo7UodNHF49yW6DrA";
    static String TWITTER_CONSUMER_SECRET = "ehFPTdxC2So8sVWlaK1LzQGr238ureggsRdsTDhv7ZMNPHkRbl";
    ProgressDialog pDialog;
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static final String TWITTER_CALLBACK_URL = "https://api.twitter.com/oauth/access_token";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifi";
    private static RequestToken requestToken;

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    //share functionality
    String urlToShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_overtake);
        songTakeoverActivity = this;
        mSharedPreferences = getApplicationContext().getSharedPreferences(
                "MyPref", 0);

        if (android.os.Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        userId = PreferencesManager.getInstance(songTakeoverActivity).getUserId();
        deviceId = PreferencesManager.getInstance(songTakeoverActivity).getDeviceId();
        playlistSize = PreferencesManager.getInstance(songTakeoverActivity).getPlayListSize();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            songId = bundle.getInt("songId");
            songTypeId = bundle.getInt("type");
            playlistId = bundle.getInt("playlistId");
            from = bundle.getString("from");
            albumname = bundle.getString("albumName");
            songName = bundle.getString("songName");
            thumbnail = bundle.getString("thumbnail");
            System.out.println("THUMBNAIL IMAGEEE" + thumbnail);
            Like = bundle.getString("Like");

        }


        //for facebook post

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        initialize();
        setupViewAction();
    }


    private void initialize() {
        lnrRemove = (LinearLayout) findViewById(R.id.lnrRemove);
        lnrCancel = (LinearLayout) findViewById(R.id.lnrCancel);
        lnrLikeSong = (LinearLayout) findViewById(R.id.lnrLikeSong);
        if (!from.matches("playlist")) {
            lnrRemove.setVisibility(View.GONE);
        }
        lnrAddtoPlaylist = (LinearLayout) findViewById(R.id.lnrAddtoPlaylist);
        lnrShare = (LinearLayout) findViewById(R.id.lnrShare);
        lnrPost = (LinearLayout) findViewById(R.id.lnrPost);
        ivThumbnail = (ImageView) findViewById(R.id.ivThumbnail);
        ivLike = (ImageView) findViewById(R.id.ivLike);
        Picasso.with(songTakeoverActivity).load(thumbnail).into(ivThumbnail);
        tvSongName = (TextView) findViewById(R.id.tvSongName);
        tvSongName.setText(songName);
        tvDetail = (TextView) findViewById(R.id.tvDetail);
        tvDetail.setText(albumname);
        tvLike = (TextView) findViewById(R.id.tvLike);

        if (Like.matches("1")) {
            ivLike.setImageResource(R.drawable.dislike);
            tvLike.setText("Unlike");
        } else {
            ivLike.setImageResource(R.drawable.like);
            tvLike.setText("Like");
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dim_frame = (FrameLayout) findViewById(R.id.dim_frame);
        //  dim_frame.getForeground().setAlpha(0);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isLogedInTwitter) {
            System.out.println("RESTART ACTIVITY CALLED");
            new updateTwitterStatus().execute("Playing with music app");
        }

    }

    private void setupViewAction() {

        lnrShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "MusicApp");
                /*check for app availability of app or not on another end make their url

                https://play.google.com/store/apps/details?id=com.dvpcindiaphone.vfs&hl=en&referrer=BlahBlah

                this way and put song id at the reffer part

                Also make a glob variable to check weather the data
                is coming from adapetr or not and as per that make variable true false on adapter click
                */

                if (songTypeId == 1) {
                    urlToShare = "http://musicapp-69a1.kxcdn.com/audio/deepDemo?songId=" + songId;
                    System.out.println("Deeplink song url" + urlToShare);
                } else if (songTypeId == 2) {

                    urlToShare = "http://musicapp-69a1.kxcdn.com/video/deepDemo?songId=" + songId;
                    System.out.println("Deeplink song url" + urlToShare);
                }

                i.putExtra(Intent.EXTRA_TEXT, urlToShare);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(Intent.createChooser(i, "choose one"));


            }
        });
        lnrPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(SongTakeoverActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                //  dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_window_dim);
                dialog.setContentView(R.layout.dialog_post);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(true);

                TextView tvSong = (TextView) dialog.findViewById(R.id.tvSong);
                TextView tvAlbum = (TextView) dialog.findViewById(R.id.tvAlbum);
                tvSong.setText("Now playing#" + songName);
                tvAlbum.setText(albumname);
                RelativeLayout rlFacebook = (RelativeLayout) dialog.findViewById(R.id.rlFacebook);
                RelativeLayout rlTwitter = (RelativeLayout) dialog.findViewById(R.id.rlTwitter);
                rlFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("MusicApp")
                                .setContentDescription(
                                        "Playing#" + songName)
                                .setContentUrl(Uri.parse("https://www.google.co.in/"))
                                .build();
                       /* http://musicapp-69a1.kxcdn.com//video//14_Kesariya%20Balam.mp4*/
                        shareDialog.show(linkContent);

                    }
                });


                rlTwitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            setUpViewsForTweetComposer("Playing#" + songName);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });

                dialog.show();
            }
        });
        lnrCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(2);
                finish();
            }
        });

        lnrLikeSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ComonHelper.checkConnection(songTakeoverActivity)) {
                    performeLike();
                } else {
                    Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });

        lnrRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ComonHelper.checkConnection(songTakeoverActivity)) {
                    performeDeletion();
                } else {
                    Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });

        lnrAddtoPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlistSize > 1) {
                    openPopupForlist(v);
                } else if (playlistSize < 1) {
                    Toast.makeText(songTakeoverActivity, "Please create playlist", Toast.LENGTH_LONG).show();
                } else {
                    if (ComonHelper.checkConnection(songTakeoverActivity)) {
                        addtoPlaylist();
                    } else {
                        Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void setUpViewsForTweetComposer(String urlName) throws MalformedURLException {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .url(new URL("https://www.google.co.in/"))
                // .text("https://www.google.co.in/");
                .text(urlName);
        builder.show();
        dialog.dismiss();
    }

    private void openPopupForlist(View anchorView) {
        dialog = new Dialog(SongTakeoverActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_window_dim);
        dialog.setContentView(R.layout.popup_playlist);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        rvPlayList = (RecyclerView) dialog.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(songTakeoverActivity);
        rvPlayList.setLayoutManager(mLayoutManager);
        popupProgressBar = (ProgressBar) dialog.findViewById(R.id.popupProgressBar);
        if (ComonHelper.checkConnection(songTakeoverActivity)) {
            getPlaylist();
            //  dim_frame.getForeground().setAlpha(220);
        } else {
            Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }

        dialog.show();
    }

   /* private void openPopupForlist(View anchorView) {

        final View popupView = songTakeoverActivity.getLayoutInflater().inflate(R.layout.popup_playlist, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        rvPlayList = (RecyclerView) popupView.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(songTakeoverActivity);
        rvPlayList.setLayoutManager(mLayoutManager);
        popupProgressBar = (ProgressBar) popupView.findViewById(R.id.popupProgressBar);
        if (ComonHelper.checkConnection(songTakeoverActivity)) {
            getPlaylist();
          //  dim_frame.getForeground().setAlpha(220);
        } else {
            Toast.makeText(songTakeoverActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

    }*/


    public static void addtoPlaylist() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
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
                        Log.w(TAG, "add to playlist" + response.toString());
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            String id = response.getString("id");
                            if (id.matches("1")) {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(songTakeoverActivity, PlayListActivity.class);
                                songTakeoverActivity.startActivity(i);
                                songTakeoverActivity.finish();

                            } else {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(songTakeoverActivity).getRequestQueue().add(request);
    }

    private void performeLike() {

        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            jsonParams.put("songId", songId);
            jsonParams.put("deviceId", deviceId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.songLike, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            String id = response.getString("id");
                            if (id.matches("1")) {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
                                setResult(1);
                                finish();
                            } else {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(songTakeoverActivity).getRequestQueue().add(request);


    }

    private void performeDeletion() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
            jsonParams.put("strSongId", songId);
            jsonParams.put("userPlaylistId", playlistId);
            jsonParams.put("deviceId", deviceId);
            System.out.println("Volley submit param" + jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.deletesongfromPlaylist, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            String id = response.getString("id");
                            if (id.matches("1")) {
                                setResult(1);
                                finish();
                            } else {
                                Toast.makeText(songTakeoverActivity, response.getString("message"), Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(songTakeoverActivity).getRequestQueue().add(request);
    }

    private void getPlaylist() {
        list.clear();
        hideKeyboard();
        String url = Utility.getplaylist + "UserId=" + userId + "&DeviceId=" + deviceId;
        popupProgressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {
                        Log.w(TAG, response.toString());
                        System.out.println("getlist" + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int id = jsonObject.getInt("id");
                            if (id == 1) {
                                JSONArray jPlaylistArray = jsonObject.getJSONArray("playList");
                                for (int i = 0; i < jPlaylistArray.length(); i++) {
                                    JSONObject jObj = jPlaylistArray.getJSONObject(i);
                                    PlaylistItem playlistItem = new PlaylistItem();
                                    playlistItem.setPlaylistName(jObj.getString("playListName"));
                                    playlistItem.setChecked(false);
                                    playlistItem.setImageUrl("");
                                    playlistItem.setPlaylistId(jObj.getInt("playListId"));
                                    playlistItem.setSongCount(jObj.getString("songCount"));
                                    list.add(playlistItem);
                                }
                                intializeAdapter();
                            } else {
                                Toast.makeText(songTakeoverActivity, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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

        MySingleton.getInstance(songTakeoverActivity).getRequestQueue().add(request);
    }

    private void intializeAdapter() {
        adapter = new RvPopupPlaylistAdapter(list, songTakeoverActivity, songId);
        rvPlayList.setAdapter(adapter);


    }


    private static void hideKeyboard() {
        // Check if no view has focus:
        View view = songTakeoverActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) songTakeoverActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Function to login twitter
     */
    private void loginToTwitter() {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
        Configuration configuration = builder.build();

        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);

            SharedPreferences.Editor e = mSharedPreferences.edit();

            // After getting access token, access token secret
            // store them in application preferences
            e.putString(PREF_KEY_OAUTH_TOKEN, "810754322556456960-wASW4ZEDbrN4C3f2JvzEIfnK7B3rX2Q");
            e.putString(PREF_KEY_OAUTH_SECRET,
                    "EULqJrYzEPtBR7QCx2NFWVVJLo5WAtEYHo4zz3oHo86s3");
            // Store login status - true
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.commit(); // save changes

            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse(requestToken.getAuthenticationURL())));
            isLogedInTwitter = true;
            // new updateTwitterStatus().execute("Playing with music app");

        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }


// for twitter post functionality

    /**
     * Function to update status
     */
    class updateTwitterStatus extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(songTakeoverActivity);
            pDialog.setMessage("Updating to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                twitter4j.Status response = twitter.updateStatus(status);

                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isLogedInTwitter = false;
                    Toast.makeText(getApplicationContext(),
                            "Status tweeted successfully", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  dim_frame.getForeground().setAlpha(0);
    }
}
