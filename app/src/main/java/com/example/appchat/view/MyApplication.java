package com.example.appchat.view;

import android.app.Application;

import com.example.appchat.R;
import com.parse.Parse;
import com.parse.ParseACL;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize( new
                Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id)).clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
