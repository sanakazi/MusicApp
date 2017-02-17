package com.musicapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.pojos.LivestreamJson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LiveStreamVideoActivity extends AppCompatActivity {
    @Bind(R.id.live_stream_title) TextView live_stream_title;
    @Bind(R.id.artistname) TextView artistname;
    @Bind(R.id.downs) ImageView downs;
    @Bind(R.id.tvDetail) ImageView tvDetail;
    @Bind(R.id.video_desctiption) RelativeLayout video_desctiption;
    @Bind(R.id.cancel) Button cancel;
    @Bind(R.id.tv_desc_SongName) TextView tv_desc_SongName;
    @Bind(R.id.tv_desc_Detail) TextView tv_desc_Detail;
    @Bind(R.id.total_time) TextView desc_total_time;
    @Bind(R.id.description) TextView description;
    private ArrayList<LivestreamJson.DataListClass> itemsList;
    public static final String LIVE_ARRAY = "LIVE_ARRAY";
    int position;

    private static final String TAG= LiveStreamVideoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream_video);
        ButterKnife.bind(this);
        Intent intent= getIntent();
        itemsList = intent.getParcelableArrayListExtra(LIVE_ARRAY);
        position=intent.getIntExtra("position",0);
        initialize();

    }

    private void initialize(){
        video_desctiption.setVisibility(View.GONE);
        Log.w(TAG,itemsList.get(position).getConcertTitle());
        live_stream_title.setText(itemsList.get(position).getConcertTitle());
        artistname.setText(itemsList.get(position).getConcertTitle());
        tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_desctiption.setVisibility(View.VISIBLE);
                tv_desc_SongName.setText(itemsList.get(position).getConcertTitle().toString());
                tv_desc_Detail.setText(itemsList.get(position).getConcertDate().toString());
                description.setText(itemsList.get(position).getDescription().toString());
            }
        });
        downs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_desctiption.setVisibility(View.GONE);

            }
        });
    }
}
