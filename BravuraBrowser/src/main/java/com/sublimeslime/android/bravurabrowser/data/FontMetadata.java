package com.sublimeslime.android.bravurabrowser.data;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
     * Beginning of file: { glyphMap :
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
        return mGlyphMap.glyphMap.get(name);
    }

    /**
     * Return the key associated with a codepoint/alternatCodepoint value in the glyph map
     * @param codepoint
     * @return key
     */
    public String lookupGlyphKeyByCodepoints( String codepoint){
        return mGlyphMap.lookupGlyphKeyByCodepoints(codepoint);
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

    /**
     * Get names of all glyphs in a range
     * @param range of glyphs
     * @return list of names in range
     */
    public ArrayList<String> getGlyphNamesForRange(String range){
        GlyphRange aRange = mGlyphRanges.map.get(range);
        ArrayList<String> names = new ArrayList<String>();
        names.addAll(Arrays.asList(aRange.glyphs));
        return names;
    }

    public String[] getAllGlyphNames(){
        return mGlyphMap.getAllGlyphNames();
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
    private GlyphRanges mGlyphRanges;

    public ArrayList<GlyphRange> getGlyphRanges(){
        ArrayList<String> sortedKeys = new ArrayList<String>(mGlyphRanges.map.keySet());
        Collections.sort(sortedKeys);
        ArrayList<GlyphRange> ranges = new ArrayList<GlyphRange>(sortedKeys.size());
        for( String key : sortedKeys){
            ranges.add(mGlyphRanges.map.get(key));
        }
        return ranges;
    }
    public static class GlyphRanges {
        public Map<String, GlyphRange> map;
         @Override
        public String toString(){
            return "map = "+ map;
        }
    }

    /**
     * Result of parsing the glyph json file. Categorizes glyphs by name
     */
    private GlyphMap mGlyphMap;
    private List<String> mGlyphRangeMapKeys = new ArrayList<String>();
    public static class GlyphMap {
        TreeMap<String,Glyph> glyphMap;
        public String[] getAllGlyphNames(){
            return glyphMap.keySet().toArray(new String[glyphMap.size()]);
        }
        @Override
        public String toString() {
            return "GlyphMap =" + glyphMap ;
        }
        public String lookupGlyphKeyByCodepoints(String codepoint){
            for( String key : glyphMap.keySet()){
                if( codepoint != null && codepoint.equals( glyphMap.get(key).codepoint)) {
                    return key;
                }
            }
            return "";
        }
      }
    public static class Glyph {
        public String codepoint;
        public String description;
        public Glyph(){}
        @Override
        public String toString() {
            return "[ "+description + " " + codepoint + " ]" ;
        }
    }

    public static void displayGlyph(TextView tv, String codepoint, float fontSize, Typeface face){
        tv.setTypeface(face);
        String uniCode = FontMetadata.getInstance().parseGlyphCodepoint(codepoint);
        tv.setText(uniCode);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }
}
