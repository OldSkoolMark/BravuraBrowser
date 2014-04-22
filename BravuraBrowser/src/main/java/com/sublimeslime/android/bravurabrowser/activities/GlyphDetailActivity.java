package com.sublimeslime.android.bravurabrowser.activities;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.sublimeslime.android.bravurabrowser.FontMetadata;
import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.fragments.GlyphDetailFragment;


public class GlyphDetailActivity extends ActionBarActivity {
    public enum IntentKey { GLYPH_NAME, GLYPH_CATEGORY }
    public final static void start(Context c, String glyphName, String glyphCategory){
        Intent i = new Intent(c, GlyphDetailActivity.class);
        i.putExtra(IntentKey.GLYPH_NAME.name(), glyphName);
        i.putExtra(IntentKey.GLYPH_CATEGORY.name(), glyphCategory);
        c.startActivity(i);
    }
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentStatePagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    GlyphDetailFragment.GlyphDetailPagerAdapter mGlyphDetailPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private String mInitialGlyphName;
    private String mGlyphCategory;
    private String mCategoryGlyphNames[];
    private Typeface mTypeface;
    private float mFontSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glyph_detail);
        Intent intent = getIntent();
        mTypeface = Typeface.createFromAsset(getAssets(), "bravura/Bravura.otf");
        mFontSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.DETAIL_FONT_SIZE.toString(),"128.0f"));
        mInitialGlyphName = intent.getStringExtra(IntentKey.GLYPH_NAME.name());
        mGlyphCategory = intent.getStringExtra(IntentKey.GLYPH_CATEGORY.name());
        ArrayList<String> names = FontMetadata.getInstance().getGlyphsNamesForCategory(mGlyphCategory);
        mCategoryGlyphNames = names.toArray(new String[names.size()]);

        // Set up the ViewPager
        mGlyphDetailPagerAdapter = new GlyphDetailFragment.GlyphDetailPagerAdapter(getSupportFragmentManager(), mCategoryGlyphNames,
                "bravura/Bravura.otf",
                mFontSize );
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mGlyphDetailPagerAdapter);
        //
        int i;
        for( i=0; i<mCategoryGlyphNames.length; i++){
            if( mCategoryGlyphNames[i].equals(mInitialGlyphName)){
                break;
            }
        }
        mViewPager.setCurrentItem(i);
    }
    /*
       @Override
       public boolean onCreateOptionsMenu(Menu menu) {

           // Inflate the menu; this adds items to the action bar if it is present.
           getMenuInflater().inflate(R.menu.glyph_detail, menu);
           return true;
       }

       @Override
       public boolean onOptionsItemSelected(MenuItem item) {
           // Handle action bar item clicks here. The action bar will
           // automatically handle clicks on the Home/Up button, so long
           // as you specify a parent activity in AndroidManifest.xml.
           int id = item.getItemId();
           if (id == R.id.action_settings) {
               return true;
           }
           return super.onOptionsItemSelected(item);
       }

     */

}
