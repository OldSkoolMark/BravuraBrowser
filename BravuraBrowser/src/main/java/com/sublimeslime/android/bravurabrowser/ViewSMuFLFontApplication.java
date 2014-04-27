package com.sublimeslime.android.bravurabrowser;

import android.app.Application;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sublimeslime.android.bravurabrowser.activities.SettingsActivity;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewSMuFLFontApplication extends Application {
    private final static String TAG = ViewSMuFLFontApplication.class.getCanonicalName();
    private Typeface mTypeface;
    public final Typeface getTypeface(){
        return mTypeface;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String fontName = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.FONT.toString(),"Bravura");
        SMuFLFont sf = FontMetadata.getSMuFLFont(fontName);
        mTypeface = Typeface.createFromAsset(getAssets(), sf.getFontAsset());
        Log.i(TAG, "** SMuFL font: "+sf.getFontAsset());
    }
    private static volatile long sGlyphArrayListKey = 0;
    private Map<Long,WeakReference<ArrayList<Glyph>>> mGlyphArrayListMap
            = new HashMap<Long, WeakReference<ArrayList<Glyph>>>();
    public ArrayList<Glyph> getGlyphArrayList( Long id){
        return mGlyphArrayListMap.get(id).get();
    }
    public Long addGlyphArrayList(ArrayList<Glyph> list){
        Long id = sGlyphArrayListKey++;
        mGlyphArrayListMap.put(id, new WeakReference<ArrayList<Glyph>>(list));
        return id;
    }


}
