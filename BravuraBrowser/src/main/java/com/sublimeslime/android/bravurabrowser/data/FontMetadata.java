package com.sublimeslime.android.bravurabrowser.data;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sublimeslime.android.bravurabrowser.views.GlyphView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mark on 4/1/14.
 */
public class FontMetadata {
    public final static FontMetadata getInstance(){
        mThis = mThis == null ? new FontMetadata() : mThis;
        return mThis;
    }

    /**
     * Parses the Smufl  glyphnames.json file. 0.85 was not well formed for gson and required
     * hand editing as follows:
     *
     * Beginning of file: { map :
     * End of file: }
     *
     * @param context that can provide assets
     * @param jsonAssetFilename name of file in assets/ directory.
     * @throws IOException
     */
    public void parseGlyphNames(Context context, String jsonAssetFilename ) throws IOException {
        InputStream is = context.getAssets().open(jsonAssetFilename);
        Reader r = new InputStreamReader(is);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        mGlyphMap = gson.fromJson(r, GlyphMap.class);
//        Log.i(TAG, mGlyphMap.toString());
    }

    /**
     * Translate string representation of codepoint (e.g. U+e054) into unicode
     * @param codepoint
     * @return string containing a single unicode character
     */
    public String parseGlyphCodepoint(String codepoint){
        String hex = codepoint.replace("U+","");
        int intValue = Integer.parseInt(hex, 16);
        char uniChar = (char)intValue;
        return Character.toString(uniChar);
    }

    /**
     * Return the metadata associate with a glyph
     * @param name of glyph
     * @return metadata
     */
    public Glyph getGlyphByName(String name){
        return mGlyphMap.map.get(name);
    }

    /**
     * Parses the Smufl ranges.json file. Not gson friendly and required hand editing as
     * follows:
     *
     * Beginning of file: { map :
     * End of file: }
     * @param context that can provide assets
     * @param jsonAssetFilename name of file in assets directory.
     * @throws IOException
     */
    public void parseGlyphRanges(Context context, String jsonAssetFilename) throws IOException {
        InputStream is = context.getAssets().open(jsonAssetFilename);
        Reader r = new InputStreamReader(is);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        mGlyphRanges = gson.fromJson(r, GlyphRanges.class);
        mGlyphRangeMapKeys.addAll(mGlyphRanges.map.keySet());
        Collections.sort(mGlyphRangeMapKeys);
//        Log.i(TAG, mGlyphRanges.toString());
    }

    /**
     * Get names of all glyph categories
     * @return list of names
     */
    public List<String> getRangeNames(){
        return mGlyphRangeMapKeys;
    }

    public GlyphRange getGlyphRange(String rangeName) {
        return mGlyphRanges.map.get(rangeName);
    }

    // Singleton
    private static FontMetadata mThis;
    private FontMetadata(){}
    private final static String TAG = FontMetadata.class.getCanonicalName();
    public class GlyphRange {
        public String description;
        public String[] glyphs;
        public String range_end;
        public String range_start;
    }
    public static class GlyphRanges {
        public Map<String, GlyphRange> map;
        @Override
        public String toString(){
            return "map = "+ map;
        }
    }
    private GlyphRanges mGlyphRanges;

    public ArrayList<GlyphRange> getGlyphRanges(){
        if( mGlyphRanges == null){
            return new ArrayList<GlyphRange>();
        } else {
            ArrayList<String> sortedKeys = new ArrayList<String>(mGlyphRanges.map.keySet());
            Collections.sort(sortedKeys);
            ArrayList<GlyphRange> ranges = new ArrayList<GlyphRange>(sortedKeys.size());
            for (String key : sortedKeys) {
                ranges.add(mGlyphRanges.map.get(key));
            }
            return ranges;
        }
    }

    public ArrayList<Glyph> getGlyphsForRange(String rangeName){
        GlyphRange range = getGlyphRange(rangeName);
        ArrayList<Glyph> glyphs = new ArrayList<Glyph>(range.glyphs.length);
        for( String glyphName : range.glyphs ){
            glyphs.add(getGlyphByName(glyphName));
        }
        return glyphs;
    }

    public ArrayList<Glyph> getGlyphsByMatchingDescription( String desc ){
        ArrayList<Glyph> matches = new ArrayList<Glyph>();
        for( Map.Entry<String,Glyph> e : mGlyphMap.map.entrySet()){
            Glyph g = e.getValue();
            if(g.description.toUpperCase().matches(desc))
                matches.add(g);
        }
        return matches;
    }
    public ArrayList<Glyph> getGlyphsByMatchingCodepoint(String codepoint){
        ArrayList<Glyph> matches = new ArrayList<Glyph>();
        for( Map.Entry<String,Glyph> e : mGlyphMap.map.entrySet()){
            Glyph g = e.getValue();
            if(g.codepoint.toUpperCase().matches(codepoint))
                matches.add(g);
        }
        return matches;
    }
    /**
     * Glyph
     */
    public static class Glyph {
        public String codepoint;
        public String description;
        public Glyph(){}
        @Override
        public String toString() {
            return "[ "+description + " " + codepoint + " ]" ;
        }
    }
    /**
     * Result of parsing the glyph json file. Categorizes glyphs by name
     */
    public static class GlyphMap {
        TreeMap<String,Glyph> map;
        public String[] getAllGlyphNames(){
            return map.keySet().toArray(new String[map.size()]);
        }
        @Override
        public String toString() {
            return "GlyphMap =" + map;
        }
        public String lookupGlyphKeyByCodepoints(String codepoint){
            for( String key : map.keySet()){
                if( codepoint != null && codepoint.toUpperCase().equals(map.get(key).codepoint)) {
                    return key;
                }
            }
            return "";
        }
    }
    private GlyphMap mGlyphMap;
    private List<String> mGlyphRangeMapKeys = new ArrayList<String>();

    /**
     * Split string into actionbar title and subtitle
     * @param s
     * @return String[0]=title and String[1]=subtitle
     */
    public static String[] get2LineDescription(String s){
        String[] line = new String[2];
        // everything after ( is line 2
        int i = s.indexOf('(');
        line[1]= i > 0 ? s.substring(i) : null ;
        if( s.length() > 0){
            int j = i == -1 ? s.length() : i-1;
            line[0] = s.substring(0,j);
        }
        return line;
    }

    public static void displayGlyph(GlyphView gv, String codepoint, float fontSize, Typeface face){
        gv.setTypeface(face);
        String uniCode = FontMetadata.getInstance().parseGlyphCodepoint(codepoint);
        gv.setText(uniCode);
        gv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }
}


