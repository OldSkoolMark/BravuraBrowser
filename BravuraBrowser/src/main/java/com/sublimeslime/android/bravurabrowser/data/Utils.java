package com.sublimeslime.android.bravurabrowser.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.activities.SettingsActivity;

public class Utils {
    private Utils(){}
    public static Typeface getTypefacePreference(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String fontName = prefs.getString(SettingsActivity.Settings.FONT.toString(), context.getResources().getString(R.string.default_font_name));
        FontMetadata.SMuFLFont sf = FontMetadata.getSMuFLFont(fontName);
        return Typeface.createFromAsset(context.getAssets(), sf.getFontAsset());
    }
}
