package com.musicapp.activities;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by PalseeTrivedi on 12/29/2016.
 */
public class TermsActivity extends AppCompatActivity {
   ImageView ivUp;

    @Bind(R.id.webview) WebView webview;
    String termConditions;
    String type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
         Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            type=bundle.getString("type");
            if (type.matches("term")) {
                termConditions = bundle.getString("term");
                getSupportActionBar().setTitle(getResources().getString(R.string.txt_terms));
             //   tvTitle.setText(getResources().getString(R.string.txt_terms));
            }else {
                termConditions = bundle.getString("privacy");
               // tvTitle.setText(getResources().getString(R.string.txt_privacy));
                getSupportActionBar().setTitle(getResources().getString(R.string.txt_privacy));
            }
        }
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webview=(WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadDataWithBaseURL("", termConditions, mimeType, encoding, "");

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
}
