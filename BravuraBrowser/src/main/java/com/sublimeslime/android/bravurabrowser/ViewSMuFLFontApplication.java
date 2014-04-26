package com.sublimeslime.android.bravurabrowser;

import android.app.Application;
import android.graphics.Typeface;

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
/*
    private ArrayList<Glyph> mDetailGlyphs = new ArrayList<Glyph>();
    public final void setDetailGlyphs(ArrayList<Glyph> glyphs){
        mDetailGlyphs = glyphs;
    }
    public final ArrayList<Glyph> getDetailGlyphs(){
        return mDetailGlyphs;
    }
*/
    @Override
    public void onCreate() {
        super.onCreate();
//        mTypeface = Typeface.createFromAsset(getAssets(), "bravura_0_9/Bravura.otf");
        mTypeface = Typeface.createFromAsset(getAssets(), "bravura_0_9/BravuraText.otf");
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
