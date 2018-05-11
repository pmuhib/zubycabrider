package com.zuby.user.zubbyrider.utils;

import android.app.Application;


/**
 * Created by Omar on 04/06/2016.
 */
public class MyApp extends Application {
    private static MyApp app;;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
       // draggerComponenet = DaggerImageDownloaderComponent.builder().imageDownloaderModule(new DraggerDownloaderModule(this)).build();
    }

    public static MyApp app(){
        return app;
    }

}
