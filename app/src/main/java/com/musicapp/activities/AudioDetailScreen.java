package com.musicapp.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by PalseeTrivedi on 1/6/2017.
 */
public class AudioDetailScreen extends Activity {

    public static RelativeLayout rlDescription;
    public static ImageView ivDesSongImage;
    public static TextView tvDesSongName, tvDesAlbumName;
    LinearLayout lnrCancel;
    WebView webView;
    String description, albumName, songName, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detail);
        Bundle bundle = getIntent().getExtras();
        description = bundle.getString("des");
        System.out.println("Description" + description);
        albumName=bundle.getString("albumName");
        songName=bundle.getString("songName");
        imageUrl=bundle.getString("imageUrl");

        System.out.println("WB SONG NAME" + songName + " " + albumName);


        initialize();
        setupViewAction();

    }


    private void initialize() {
        ivDesSongImage = (ImageView) findViewById(R.id.ivDesSongImage);
        tvDesSongName = (TextView) findViewById(R.id.tvDesSongName);
        tvDesAlbumName = (TextView) findViewById(R.id.tvDesAlbumName);
         lnrCancel = (LinearLayout) findViewById(R.id.lnrCancel);
        webView = (WebView) findViewById(R.id.webView);
        webView.setBackgroundColor(Color.parseColor("#1c1c1c"));
        if (description.matches("")) {
            webView.setVisibility(View.GONE);
        }
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", description, mimeType, encoding, "");
        if (!imageUrl.matches("") && imageUrl!=null){
            Picasso.with(AudioDetailScreen.this).load(imageUrl).into(ivDesSongImage);
        }
        tvDesSongName.setText(songName);
        tvDesAlbumName.setText(albumName);
    }
    private void setupViewAction(){
        lnrCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
