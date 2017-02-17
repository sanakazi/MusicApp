package com.musicapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.musicapp.R;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AllGenreStartActivity extends AppCompatActivity {
    @Bind(R.id.btn_continue)
    TextView btn_continue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_genre_start);
        ButterKnife.bind(this);
        events();
    }

    private  void events(){
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllGenreStartActivity.this,AllGenreActivity.class);
                intent.putExtra("from",0);
                startActivity(intent);

            }
        });
    }
}
