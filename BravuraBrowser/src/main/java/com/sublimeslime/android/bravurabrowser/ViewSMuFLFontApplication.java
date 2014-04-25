package com.sublimeslime.android.bravurabrowser;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

public class ViewSMuFLFontApplication extends Application {
    private final static String TAG = ViewSMuFLFontApplication.class.getCanonicalName();
    private Typeface mTypeface;
    public final Typeface getTypeface(){
        return mTypeface;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mTypeface = Typeface.createFromAsset(getAssets(), "bravura/Bravura.otf");
    }
}
