package com.musicapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.musicapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AllArtistsStartActivity extends AppCompatActivity {
    @Bind(R.id.btn_continue)
    TextView btn_continue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_artists_start);
        ButterKnife.bind(this);
        events();
    }
    private  void events()
    {
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(AllArtistsStartActivity.this,AllArtistsActivity.class);
                intent.putExtra("from",0);
                startActivity(intent);

            }
        });
    }
}
