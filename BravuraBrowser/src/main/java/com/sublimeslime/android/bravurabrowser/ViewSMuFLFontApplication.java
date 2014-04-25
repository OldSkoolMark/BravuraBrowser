package com.sublimeslime.android.bravurabrowser;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

import java.util.ArrayList;

public class ViewSMuFLFontApplication extends Application {
    private final static String TAG = ViewSMuFLFontApplication.class.getCanonicalName();
    private Typeface mTypeface;
    private ArrayList<String> mGlyphNameList = new ArrayList<String>();
    private String mGlyphNameListLabel = "";
    public final Typeface getTypeface(){
        return mTypeface;
    }
    public final void setGlyphNameList(ArrayList<String> glyphNameList){
        mGlyphNameList = glyphNameList;
    }
    public final ArrayList<String> getGlyphNameList(){
        return mGlyphNameList;
    }
    public final void setGlyphNameListLabel(String label){
        mGlyphNameListLabel = label;
    }
    public final String getmGlyphNameListLabel(){
        return mGlyphNameListLabel;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mTypeface = Typeface.createFromAsset(getAssets(), "bravura/Bravura.otf");
    }
}
