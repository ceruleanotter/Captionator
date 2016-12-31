package io.github.ceruleanotter.captionator;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by lyla on 12/30/16.
 */

public class CaptionatorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
