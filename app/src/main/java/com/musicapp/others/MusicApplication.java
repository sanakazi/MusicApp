package com.musicapp.others;

import android.app.Application;

import com.musicapp.R;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by SanaKazi on 12/26/2016.
 */
public class MusicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(getResources().getString(R.string.twitter_consumer_key),
                        getResources().getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new TwitterCore(authConfig));

    }

}
