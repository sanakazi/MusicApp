package com.musicapp.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.adapters.ScreenSlidePagerAdapter;
import com.musicapp.custom.AutoScrollViewPager;
import com.musicapp.custom.CirclePageIndicator;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pushnotification.Config;
import com.musicapp.pushnotification.NotificationUtils;
import com.musicapp.singleton.PreferencesManager;

import java.util.ArrayList;


public class AppIntroActivityNew  extends Activity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static final String TAG = AppIntroActivityNew.class.getSimpleName();
    private TextView getStartedBtn;
    private AutoScrollViewPager viewPager;
    private CirclePageIndicator circlePageIndicator;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private TextView tvSignUp;
    TextView btn_signup;
    boolean isLogedin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_intro_activity_new);
     //   sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
     //   isLogedin=sharedPreferences.getBoolean("isLogedin",false);
        initialize();
        deviceTokencode();
    }

    private void initialize() {
        isLogedin= PreferencesManager.getInstance(AppIntroActivityNew.this).isLoggedIn();
        if (isLogedin){
            Intent i=new Intent(AppIntroActivityNew.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        Log.i(TAG, "TrainningSlidesActivity");
        viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);
        circlePageIndicator = (CirclePageIndicator)  findViewById(R.id.indicator);
        getStartedBtn =(TextView) findViewById(R.id.btn_login);
        btn_signup =(TextView) findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(AppIntroActivityNew.this, SignupActivity.class));

            }
        });

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(AppIntroActivityNew.this, LoginActivity.class));

            }
        });
        mPagerAdapter = new ScreenSlidePagerAdapter(AppIntroActivityNew.this);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

            @Override
            public void transformPage(View page, float position) {
                // TODO Auto-generated method stub
            }

        });
        circlePageIndicator.setViewPager(viewPager);
        viewPager.setInterval(5000);
        viewPager.startAutoScroll();
    }

    private void deviceTokencode() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    //   Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    Log.w(TAG, "Push notification: " + message);

                }
            }
        };
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void
    displayFirebaseRegId() {
        //  SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        // String regId = pref.getString("regId", null);
        String regId = PreferencesManager.getInstance(getApplicationContext()).getDeviceToken();

        Log.w(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.w(TAG, "Firebase Reg Id: " + regId);
        else
            Log.w(TAG, "Firebase Reg Id is not received yet!");
    }



    @Override
    protected void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        viewPager.stopAutoScroll();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start auto scroll when onResume
        viewPager.startAutoScroll();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
//		FlurryAgent.onStartSession(this, Apis.FLURRY_KEY);
//		FlurryAgent.logEvent(TrainningSlidesActivity.class.getSimpleName());
    }
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
//		FlurryAgent.endTimedEvent(TrainningSlidesActivity.class.getSimpleName());
//		FlurryAgent.onEndSession(this);
    }


}
