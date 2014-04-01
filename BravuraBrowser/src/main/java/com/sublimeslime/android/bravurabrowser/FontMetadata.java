package com.sublimeslime.android.bravurabrowser;

import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
     * @param context
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
     * Parses the Smufl classes.json file. 0.85 was not gson friendly and required hand editing as
     * follows:
     *
     * Beginning of file: { glyphClasses :
     * End of file: }
     * @param context
     * @param jsonAssetFilename name of file in assets directory.
     * @throws IOException
     */
    public void parseGlyphClasses(Context context, String jsonAssetFilename) throws IOException {
        InputStream is = context.getAssets().open(jsonAssetFilename);
        Reader r = new InputStreamReader(is);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        mGlyphClasses = gson.fromJson(r, GlyphClasses.class);
//        Log.i(TAG, mGlyphClasses.toString());
    }
    // Singleton
    private static FontMetadata mThis;
    private FontMetadata(){}
    private final static String TAG = FontMetadata.class.getCanonicalName();

    private GlyphClasses mGlyphClasses;
    public static class GlyphClasses {
        Map<String, ArrayList<String>> glyphClasses;
        @Override
        public String toString(){
            return "glyphClasses = "+ glyphClasses;
        }
    }

    /**
     * Result of parsing the glyph json file
     */
    private GlyphMap mGlyphMap;

    public static class GlyphMap {
        Map<String,Glyph> glyphMap;
        @Override
        public String toString() {
            return "GlyphMap =" + glyphMap ;
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
}
