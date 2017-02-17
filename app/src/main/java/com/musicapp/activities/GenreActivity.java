package com.musicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.adapters.GenreAdapter;
import com.musicapp.fragments.GenreFragment;
import com.musicapp.others.ComonHelper;
import com.musicapp.service.BackgroundSoundService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GenreActivity extends AppCompatActivity implements GenreAdapter.GenreClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.btn_manage_genres) Button btn_manage_genres;

    private static final String TAG=GenreActivity.class.getSimpleName();
    RelativeLayout bottomPlayerView;
    SeekBar seekView;
    TextView tvPlayName;
    ImageView ivUp;
    public static ImageView ivBottomPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        events();

        GenreFragment genreFragment = new GenreFragment();
        addFragment(genreFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            GenreFragment genreFragment = new GenreFragment();
            addFragment(genreFragment);
        }
    }

    private  void events(){


        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
        seekView = (SeekBar) findViewById(R.id.seekView);
        tvPlayName = (TextView) findViewById(R.id.tvPlayName);
        ivUp = (ImageView) findViewById(R.id.ivUp);


        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, GenreActivity.this);
        } else {
            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, GenreActivity.this);
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
                BackgroundSoundService.mPlayer.seekTo(seekView.getProgress());
                ComonHelper.timer.cancel();
                ComonHelper.updateSeekProgressTimer(seekView, GenreActivity.this);
                AudioPlayerActivity.timer.cancel();
                AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                audioPlayerActivity.updateProgressBar();
            }
        });







        btn_manage_genres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GenreActivity.this, AllGenreActivity.class);
                Bundle bund = new Bundle();
                bund.putInt("from",1);
                intent.putExtras(bund);
                startActivityForResult(intent,1);
            }
        });
    }

    public void addFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().add(R.id.genre_container, fragment).commit();
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


    @Override
    public void onGenreClick(int catId, int typeId, String typeName, String coverImage) {
        Intent intent = new Intent(GenreActivity.this, HomeItemClickDetailsActivity.class);
        intent.putExtra(HomeItemClickListActivity.CAT_ID,catId);
        intent.putExtra(HomeItemClickListActivity.TYPE_ID, typeId);
        intent.putExtra(HomeItemClickListActivity.TYPE_NAME, typeName);
        intent.putExtra(HomeItemClickListActivity.IMAGE_URL,coverImage);
        startActivity(intent);
    }
}

