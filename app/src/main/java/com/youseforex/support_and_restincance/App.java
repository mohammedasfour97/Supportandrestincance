package com.youseforex.support_and_restincance;

import android.app.Application;
import android.content.Intent;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Cairo-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        startService(new Intent(this, BackgroundService.class));
    }
}