package com.sublimeslime.android.bravurabrowser.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication.SMuFLFont;
import com.sublimeslime.android.bravurabrowser.activities.SettingsActivity;

import static com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication.getSMuFLFont;

public class Utils {
    private Utils(){}
    public static Typeface getTypefacePreference(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String fontName = prefs.getString(SettingsActivity.Settings.FONT.toString(), context.getResources().getString(R.string.default_font_name));
        SMuFLFont sf = getSMuFLFont(fontName);
        return Typeface.createFromAsset(context.getAssets(), sf.getFontAsset());
    }
    public static String getFontnamePreference(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(SettingsActivity.Settings.FONT.toString(), context.getResources().getString(R.string.default_font_name));
    }

}
