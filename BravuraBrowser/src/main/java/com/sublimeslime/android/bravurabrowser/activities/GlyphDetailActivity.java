package com.sublimeslime.android.bravurabrowser.activities;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication;
import com.sublimeslime.android.bravurabrowser.fragments.GlyphDetailFragment;


public class GlyphDetailActivity extends ActionBarActivity implements GlyphDetailFragment.IParentActivity {
    // IParentActivity contract
    @Override
    public float getFontSize() {
        return mFontSize;
    }

    @Override
    public Typeface getTypeface() {
        return mTypeface;
    }

    // start activity convenience method
    public final static void start(Context c) {
        Intent i = new Intent(c, GlyphDetailActivity.class);
        c.startActivity(i);
    }

    private GlyphDetailPagerAdapter mGlyphDetailPagerAdapter;
    private ViewPager mViewPager;
    private String mGlyphName;
    private String mGlyphNames[];
    private Typeface mTypeface;
    private float mFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glyph_detail);
        mTypeface = ((ViewSMuFLFontApplication) getApplication()).getTypeface();
        mFontSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.DETAIL_FONT_SIZE.toString(), "128.0f"));
        mGlyphName = ((ViewSMuFLFontApplication) getApplication()).getmGlyphNameListLabel();

        // Set up the ViewPager
        ArrayList<String> names = ((ViewSMuFLFontApplication) getApplication()).getGlyphNameList();
        mGlyphNames = names.toArray(new String[names.size()]);
        mGlyphDetailPagerAdapter = new GlyphDetailPagerAdapter(getSupportFragmentManager(), mGlyphNames);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mGlyphDetailPagerAdapter);
        scrollViewPagerTo(mGlyphName);

    }

    private void scrollViewPagerTo(String glyphName) {
        int i;
        for (i = 0; i < mGlyphNames.length; i++) {
            if (mGlyphNames[i].equals(glyphName)) {
                break;
            }
        }
        mViewPager.setCurrentItem(i);
    }

    private class GlyphDetailPagerAdapter extends FragmentStatePagerAdapter {
        private String[] mGlyphNames;

        public GlyphDetailPagerAdapter(FragmentManager fm, String[] glyphNames) {
            super(fm);
            mGlyphNames = glyphNames;
        }

        @Override
        public Fragment getItem(int position) {
            mGlyphName = GlyphDetailActivity.this.mGlyphNames[position];
            return GlyphDetailFragment.newGlyphFragmentInstance(position);
        }

        @Override
        public int getCount() {
            return mGlyphNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mGlyphNames[position];
        }
    }
}
