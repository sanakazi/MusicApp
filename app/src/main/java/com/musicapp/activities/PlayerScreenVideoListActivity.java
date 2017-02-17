package com.musicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.adapters.PlayerScreenListAdapter;
import com.musicapp.adapters.PlayerScreenListVideoAdapter;
import com.musicapp.pojos.HomeDetailsJson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by PalseeTrivedi on 1/4/2017.
 */
public class PlayerScreenVideoListActivity extends AppCompatActivity {

    @Bind(R.id.latest_song_name)
    TextView latest_song_name;
    @Bind(R.id.album_name)
    TextView album_name;
    @Bind(R.id.slide_down)
    ImageView slide_down;
   public static TextView tvLabelNext;
    ImageView ivBackward, ivPrevious, ivPlay, ivNext, ivForward;
    RecyclerView recyclerView;
    PlayerScreenListVideoAdapter myAdapter;
    ArrayList<HomeDetailsJson.DataList> detail_categories_list;
    private static final String TAG = PlayerScreenListActivity.class.getSimpleName();
    public static final String ALBUM_NAME = "album_name";
    String albumName;
    int index;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_video_screen_list);
        ButterKnife.bind(this);
        Bundle b = this.getIntent().getExtras();
        detail_categories_list = b.getParcelableArrayList("categories");
        index = getIntent().getIntExtra("index", 0);
        albumName = getIntent().getStringExtra(ALBUM_NAME);
        ivBackward = (ImageView) findViewById(R.id.ivBackward);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivForward = (ImageView) findViewById(R.id.ivForward);
        tvLabelNext=(TextView) findViewById(R.id.tvLabelNext);
        recyclerView = (RecyclerView) findViewById(R.id.playerlist_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        events();


    }

    private void events() {
        album_name.setText(albumName.toString());
        latest_song_name.setText(detail_categories_list.get(index).getColumns().getSongName());
        myAdapter = new PlayerScreenListVideoAdapter(this,detail_categories_list, index);
        recyclerView.setAdapter(myAdapter);
        slide_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VideoPlayerActivity.isUrlChange=false;

                Intent i=new Intent(PlayerScreenVideoListActivity.this,VideoPlayerActivity.class);
                setResult(1,i);
                finish();
                //overridePendingTransition(0, R.anim.push_down);
            }
        });

        ivBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "backward";
                Bundle bundle = new Bundle();
                bundle.putInt("index", index - 1);
                bundle.putString("flag", flag);
                Intent i = new Intent(PlayerScreenVideoListActivity.this, VideoPlayerActivity.class);
                i.putExtras(bundle);
                setResult(0, i);
                finish();

            }
        });
        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "forward";
                Bundle bundle = new Bundle();
                bundle.putInt("index", index + 1);
                bundle.putString("flag", flag);
                Intent i = new Intent(PlayerScreenVideoListActivity.this, VideoPlayerActivity.class);
                i.putExtras(bundle);
                setResult(0, i);
                finish();
            }
        });
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "play";
                Bundle bundle = new Bundle();
                bundle.putInt("index", index + 1);
                bundle.putString("flag", flag);
                Intent i = new Intent(PlayerScreenVideoListActivity.this, VideoPlayerActivity.class);
                i.putExtras(bundle);
                setResult(0, i);
                finish();
            }
        });
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "next";
                Bundle bundle = new Bundle();
                bundle.putInt("index", index + 1);
                bundle.putString("flag", flag);
                Intent i = new Intent(PlayerScreenVideoListActivity.this, VideoPlayerActivity.class);
                i.putExtras(bundle);
                setResult(0, i);
                finish();
            }
        });
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "previuos";
                Bundle bundle = new Bundle();
                bundle.putInt("index", index + 1);
                bundle.putString("flag", flag);
                Intent i = new Intent(PlayerScreenVideoListActivity.this, VideoPlayerActivity.class);
                i.putExtras(bundle);
                setResult(0, i);
                finish();
            }
        });

    }


}
