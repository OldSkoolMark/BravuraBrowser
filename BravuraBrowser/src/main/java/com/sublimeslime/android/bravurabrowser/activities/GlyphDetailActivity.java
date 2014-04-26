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
import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;
import com.sublimeslime.android.bravurabrowser.fragments.GlyphDetailFragment;


public class GlyphDetailActivity extends ActionBarActivity implements GlyphDetailFragment.IParentActivity {
    // IParentActivity contract
    @Override
    public ArrayList<Glyph> getGlyphs(){
        return mGlyphs;
    }
    @Override
    public float getFontSize() {
        return mFontSize;
    }

    @Override
    public Typeface getTypeface() {
        return mTypeface;
    }

    public enum IntentKey { POSITION, ARRAY_LIST_KEY }
    // start activity convenience method
    public final static void start(Context c, int position, Long arrayListKey ) {
        Intent i = new Intent(c, GlyphDetailActivity.class);
        i.putExtra(IntentKey.POSITION.name(), position);
        i.putExtra(IntentKey.ARRAY_LIST_KEY.name(), arrayListKey);
        c.startActivity(i);
    }

    private GlyphDetailPagerAdapter mGlyphDetailPagerAdapter;
    private ViewPager mViewPager;
    private int mGlyphPosition;
    private ArrayList<Glyph> mGlyphs;
    private Typeface mTypeface;
    private float mFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glyph_detail);
        mTypeface = ((ViewSMuFLFontApplication) getApplication()).getTypeface();
        mFontSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.DETAIL_FONT_SIZE.toString(), "128.0f"));
        mGlyphPosition = getIntent().getIntExtra(IntentKey.POSITION.name(), 0);

        // Set up the ViewPager
        mGlyphs = ((ViewSMuFLFontApplication) getApplication()).getGlyphArrayList(getIntent().getLongExtra(IntentKey.ARRAY_LIST_KEY.name(),0L));
        mGlyphDetailPagerAdapter = new GlyphDetailPagerAdapter(getSupportFragmentManager(), mGlyphs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mGlyphDetailPagerAdapter);
        mViewPager.setCurrentItem(mGlyphPosition);

    }

    private class GlyphDetailPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Glyph> mGlyphs;

        public GlyphDetailPagerAdapter(FragmentManager fm, ArrayList<Glyph> glyphs) {
            super(fm);
            mGlyphs = glyphs;
        }

        @Override
        public Fragment getItem(int position) {
            return GlyphDetailFragment.newGlyphFragmentInstance(position);
        }

        @Override
        public int getCount() {
            return mGlyphs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mGlyphs.get(position).description;
        }
    }
}
