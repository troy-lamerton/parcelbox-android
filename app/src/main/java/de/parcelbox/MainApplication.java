package de.parcelbox;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // init calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/CabinSketch-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
