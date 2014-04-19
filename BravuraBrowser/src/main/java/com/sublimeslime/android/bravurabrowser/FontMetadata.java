package com.sublimeslime.android.bravurabrowser;

import android.content.Context;
import android.util.Log;
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
import java.util.Collections;
import java.util.HashMap;
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
     * @param alternateCodepoint
     * @return key
     */
    public String lookupGlyphKeyByCodepoints( String codepoint, String alternateCodepoint){
        return mGlyphMap.lookupGlyphKeyByCodepoints(codepoint, alternateCodepoint);
    }

    /**
     * Parses the Smufl classes.json file. 0.85 was not gson friendly and required hand editing as
     * follows:
     *
     * Beginning of file: { glyphClasses :
     * End of file: }
     * @param context that can provide assets
     * @param jsonAssetFilename name of file in assets directory.
     * @throws IOException
     */
    public void parseGlyphCategories(Context context, String jsonAssetFilename) throws IOException {
        InputStream is = context.getAssets().open(jsonAssetFilename);
        Reader r = new InputStreamReader(is);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        mGlyphClasses = gson.fromJson(r, GlyphClasses.class);

        Set<String> keySet = mGlyphClasses.glyphClasses.keySet();
        mGlyphClassNames.addAll(keySet);
        Collections.sort(mGlyphClassNames);
//        Log.i(TAG, mGlyphClasses.toString());
    }

    /**
     * Get names of all glyph categories
     * @return list of names
     */
    public List<String> getCategories(){
        return mGlyphClassNames;
    }

    /**
     * Get names of all glyphs in a category
     * @param category of glyphs
     * @return list of names in category
     */
    public ArrayList<String> getGlyphsNamesForCategory(String category){
        return mGlyphClasses.glyphClasses.get(category);
    }

    public String[] getAllGlyphNames(){
        return mGlyphMap.getAllGlyphNames();
    }

    // Singleton
    private static FontMetadata mThis;
    private FontMetadata(){}
    private final static String TAG = FontMetadata.class.getCanonicalName();

    private GlyphClasses mGlyphClasses;
    public static class GlyphClasses {
        public Map<String, ArrayList<String>> glyphClasses;
         @Override
        public String toString(){
            return "glyphClasses = "+ glyphClasses;
        }
    }

    /**
     * Result of parsing the glyph json file. Categorizes glyphs by name
     */
    private GlyphMap mGlyphMap;
    private List<String> mGlyphClassNames = new ArrayList<String>();
    public static class GlyphMap {
        TreeMap<String,Glyph> glyphMap;
        public String[] getAllGlyphNames(){
            return glyphMap.keySet().toArray(new String[glyphMap.size()]);
        }
        @Override
        public String toString() {
            return "GlyphMap =" + glyphMap ;
        }
        public String lookupGlyphKeyByCodepoints(String codepoint, String alternateCodepoint){
            for( String key : glyphMap.keySet()){
                if( codepoint != null && codepoint.equals( glyphMap.get(key).codepoint)) {
                    if( alternateCodepoint == null || alternateCodepoint.equals( glyphMap.get(key).alternateCodepoint)){
                        return key;
                    }
                }
            }
            return "";
        }
      }
    public static class Glyph {
        public String codepoint;
        public String alternateCodepoint;
        public Glyph(){}
        @Override
        public String toString() {
            return "[ " + codepoint + "," + (alternateCodepoint == null ? "" : alternateCodepoint) + " ]" ;
        }
    }

    public static void displayGlyph(TextView tv, String codepoint, float fontSize, Typeface face){
        tv.setTypeface(face);
        String uniCode = FontMetadata.getInstance().parseGlyphCodepoint(codepoint);
        tv.setText(uniCode);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }
}
