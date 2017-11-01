package com.musicapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.fragments.BrowseFragment;
import com.musicapp.fragments.HomeFragment;
import com.musicapp.fragments.LibraryFragment;
import com.musicapp.fragments.LivestreamFragment;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.PlayerConstants;
import com.musicapp.service.BackgroundSoundService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG= MainActivity.class.getSimpleName();
    //  @Bind(R.id.main_viewpager) ViewPager main_viewpager;
    //  @Bind(R.id.tabLayout) TabLayout tabLayout;
    @Bind(R.id.buttonHome)
    LinearLayout buttonHome;
    @Bind(R.id.img_home)
    ImageView img_home;
    @Bind(R.id.txt__home)
    TextView txt__home;
    @Bind(R.id.bar_home)
    LinearLayout bar_home;
    @Bind(R.id.buttonBrowse)
    LinearLayout buttonBrowse;
    @Bind(R.id.img_browse)
    ImageView img_browse;
    @Bind(R.id.txt__browse)
    TextView txt__browse;
    @Bind(R.id.bar_browse)
    LinearLayout bar_browse;
    @Bind(R.id.buttonLibrary)
    LinearLayout buttonLibrary;
    @Bind(R.id.img_library)
    ImageView img_library;
    @Bind(R.id.txt__library)
    TextView txt__library;
    @Bind(R.id.bar_library)
    LinearLayout bar_library;

    @Bind(R.id.buttonLivestream)
    LinearLayout buttonLivestream;
    @Bind(R.id.img_livestream)
    ImageView img_livestream;
    @Bind(R.id.txt__livestream)
    TextView txt__livestream;
    @Bind(R.id.bar_livestream)
    LinearLayout bar_livestream;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    public static RelativeLayout bottomPlayerView;
    SeekBar seekView;
    TextView tvPlayName;
    ImageView ivUp;
    public static ImageView ivBottomPlay;

    public static final String HOME_FRAGMENT = "HOME_FRAGMENT";
    public static final String BROWSE_FRAGMENT = "BROWSE_FRAGMENT";
    public static final String LIBRARY_FRAGMENT = "LIBRARY_FRAGMENT";
    public static final String LIVESTREAM_FRAGMENT = "LIVESTREAM_FRAGMENT";

    private int[] tabIcons = {
            R.mipmap.home_orange,
            R.mipmap.browse_wight,
            R.mipmap.settings
    };
    String from = "",playlistName="";
    int playlistId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getString("from");
            playlistId=bundle.getInt("playlistId");
            playlistName=bundle.getString("playlistName");
        }


   /*     if(checkSubscription().equalsIgnoreCase("Sucess"))
        {
            addDefaultFragment();
            events();
        }
        else if (checkSubscription().equalsIgnoreCase("Not Subscribed User"))
        {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            }
            */


        addDefaultFragment();
        events();
    }

    private void events() {
        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
        seekView = (SeekBar) findViewById(R.id.seekView);
        tvPlayName = (TextView) findViewById(R.id.tvPlayName);
        ivUp = (ImageView) findViewById(R.id.ivUp);


        System.out.println("PLAYINGGG" + AudioPlayerActivity.isPlaying);
        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, MainActivity.this);
        } else {

            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, MainActivity.this);
            } else {
                bottomPlayerView.setVisibility(View.GONE);
            }
        }


        seekView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (ComonHelper.timer != null) {
                        ComonHelper.timer.cancel();
                        ComonHelper.timer = null;
                    }
                    BackgroundSoundService.mPlayer.seekTo(seekBar.getProgress());
                    ComonHelper.updateSeekProgressTimer(seekBar, MainActivity.this);
                    if (AudioPlayerActivity.timer != null) {
                        AudioPlayerActivity.timer.cancel();
                        AudioPlayerActivity.timer = null;
                    }
                    AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                    audioPlayerActivity.updateProgressBar();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                HomeFragment homeFragment = new HomeFragment();
                replaceFragment(homeFragment, HOME_FRAGMENT, false);
                changePressedState(img_home, txt__home, bar_home);
            }
        });

        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                BrowseFragment browseFragment = new BrowseFragment();
                replaceFragment(browseFragment, BROWSE_FRAGMENT, false);
                changePressedState(img_browse, txt__browse, bar_browse);
            }
        });

        buttonLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LibraryFragment libraryFragment = new LibraryFragment();
                replaceFragment(libraryFragment, LIBRARY_FRAGMENT, false);
                changePressedState(img_library, txt__library, bar_library);
            }
        });

        buttonLivestream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                LivestreamFragment livestreamFragment = new LivestreamFragment();
                replaceFragment(livestreamFragment, LIVESTREAM_FRAGMENT, false);
                changePressedState(img_livestream, txt__livestream, bar_livestream);
            }
        });


    }


    public void changePressedState(ImageView img_view, TextView txt_view, LinearLayout bar_view) {


        ContextCompat.getColor(MainActivity.this, R.color.white);
        img_home.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        txt__home.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        bar_home.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.tab_bg));


        img_browse.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        txt__browse.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        bar_browse.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.tab_bg));

        img_library.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        txt__library.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        bar_library.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.tab_bg));

        img_livestream.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        txt__livestream.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        bar_livestream.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.tab_bg));

        img_view.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.home_icons_yellow),
                PorterDuff.Mode.SRC_ATOP);
        txt_view.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_icons_yellow));
        bar_view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.home_icons_yellow));

    }

    private void addDefaultFragment() {

        if (from.matches("playlist")) {
            BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);

            if (browseFragment == null) {
                Bundle bundle = new Bundle();
                bundle.putString("from",from);
                bundle.putString("playlistName",playlistName);
                bundle.putInt("playlistId",playlistId);
                browseFragment = new BrowseFragment();
                browseFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.container, browseFragment, HOME_FRAGMENT).commit();
            } else {
                replaceFragment(browseFragment, HOME_FRAGMENT, false);
            }
            changePressedState(img_browse, txt__browse, bar_browse);

        } else {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);

            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.container, homeFragment, HOME_FRAGMENT).commit();
            } else {
                replaceFragment(homeFragment, HOME_FRAGMENT, false);
            }
            changePressedState(img_home, txt__home, bar_home);

        }
    }

    private void replaceFragment(Fragment fragment, String tag, boolean addtoBackStack) {
        if (addtoBackStack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag).addToBackStack(null).commit();
//
//
//
//
//
//
//
// toolbarNAvigationListener();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag).commit();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("RESTART CALLLED");
        events();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("RESUME  CALLLED");
        events();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AudioPlayerActivity.isPlaying) {
            if (ComonHelper.timer != null) {
                ComonHelper.timer.cancel();
            }

            if (AudioPlayerActivity.timer != null) {
                AudioPlayerActivity.timer.cancel();
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (AudioPlayerActivity.isPlaying) {
            if (ComonHelper.timer != null) {
                ComonHelper.timer.cancel();
            }

            if (AudioPlayerActivity.timer != null) {
                AudioPlayerActivity.timer.cancel();
            }

        }
        // finish();
        finishAffinity();
    }


    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void changeBottomUI() {
        if (PlayerConstants.SONG_PAUSED) {
            ivBottomPlay.setImageResource(R.drawable.pause_orange);
        } else {
            ivBottomPlay.setImageResource(R.drawable.play_orange);
        }
    }

   /* @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp,tvPlayName, MainActivity.this);
        } else {

            if (AudioPlayerActivity.isPause)
             {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp,tvPlayName, MainActivity.this);
            }
            else
             {
                bottomPlayerView.setVisibility(View.GONE);
            }
        }
    }*/
}