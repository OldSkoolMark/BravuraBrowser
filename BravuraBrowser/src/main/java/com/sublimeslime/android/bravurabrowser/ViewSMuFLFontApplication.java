package com.sublimeslime.android.bravurabrowser;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sublimeslime.android.bravurabrowser.activities.SettingsActivity;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;
import com.sublimeslime.android.bravurabrowser.data.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ViewSMuFLFontApplication extends Application {
    private final static String TAG = ViewSMuFLFontApplication.class.getCanonicalName();


    @Override
    public void onCreate() {
        super.onCreate();
        initAvailableFonts(getResources());
    }

    /**
     * The GridView in MainActivity and SearchResultsDisplay activities share a list of glyphs with
     * the ViewPager in GlyphDetailActivity via the WeakHashMap below
     */
    private static volatile long sGlyphArrayListKey = 0;
    private Map<Long, ArrayList<Glyph>> mGlyphArrayListMap = new WeakHashMap<Long, ArrayList<Glyph>>();
    public ArrayList<Glyph> getGlyphArrayList( Long id){
        return mGlyphArrayListMap.get(id);
    }
    public Long addGlyphArrayList(ArrayList<Glyph> list){
        Long id = sGlyphArrayListKey++;
        mGlyphArrayListMap.put(id, new ArrayList<Glyph>(list));
        return id;
    }

    /**
     * Font asset configuration
     */

    public static class SMuFLFont {
        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getDescription() {
            return description;
        }

        public String getFontAsset() {
            return fontAsset;
        }

        private final String name;
        private final String version;
        private final String description;
        private final String fontAsset;
        public SMuFLFont(String name, String version, String description, String fontAsset){
            this.name = name;
            this.version = version;
            this.description = description;
            this.fontAsset = fontAsset;
        }
    }
    /**
     * Available fonts
     */
    private void initAvailableFonts(Resources r){
        TypedArray ta = r.obtainTypedArray(R.array.available_fonts);
        int n = ta.length();
        String[][] array = new String[n][];
        for (int i = 0; i < n; ++i) {
            int id = ta.getResourceId(i, 0);
            if (id > 0) {
                array[i] = r.getStringArray(id);
                availableFonts.add(new SMuFLFont(array[i][0],array[i][1],array[i][2],array[i][3]));
            } else {
                Log.e(TAG,"error parsing available fonts array resources");
            }
        }
        ta.recycle();
    }


    private final static ArrayList<SMuFLFont> availableFonts = new ArrayList<SMuFLFont>();
 /*   static  {
        availableFonts.add(new SMuFLFont("Bravura", "0.99","Bravura","bravura_0_99/Bravura.otf" ));
        availableFonts.add(new SMuFLFont("BravuraText", "0.99","Bravura Text","bravura_0_99/BravuraText.otf"));
    }*/
    public final static SMuFLFont getSMuFLFont( String name ){
        for( SMuFLFont s : availableFonts){
            if( s.name.equals(name))
                return s;
        }
        return null;
    }

}
