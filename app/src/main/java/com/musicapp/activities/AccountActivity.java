package com.musicapp.activities;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.musicapp.R;
import com.musicapp.singleton.PreferencesManager;

public class AccountActivity extends AppCompatActivity {

    RelativeLayout btn_account;
    RelativeLayout btn_subscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_account = (RelativeLayout)findViewById(R.id.btn_account);
        btn_subscription = (RelativeLayout)findViewById(R.id.btn_subscription);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Account");
        events();
    }

    private void events()
    {
        btn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PreferencesManager.getInstance(AccountActivity.this).isSocialLogin()==1){
                    Toast.makeText(AccountActivity.this,"This option is not available for login through social media",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(AccountActivity.this,ChangePasswordActivity.class);
                    startActivity(intent);}
            }
        });

        btn_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,SubscriptionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }


}
