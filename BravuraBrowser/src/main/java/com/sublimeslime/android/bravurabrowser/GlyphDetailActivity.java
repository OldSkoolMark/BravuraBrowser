package com.sublimeslime.android.bravurabrowser;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
    GlyphDetailPagerAdapter mGlyphDetailPagerAdapter;

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
        mGlyphDetailPagerAdapter = new GlyphDetailPagerAdapter(getSupportFragmentManager());
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
    private class GlyphDetailPagerAdapter extends FragmentStatePagerAdapter {

        public GlyphDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a GlyphDetailFragment (defined as a static inner class below).
            return newGlyphFragmentInstance(position);
        }

        @Override
        public int getCount() {
            return mCategoryGlyphNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCategoryGlyphNames[position];
        }
    }

    private static final String GLYPH_INDEX = "glyph_index";

    public GlyphDetailFragment newGlyphFragmentInstance(int sectionNumber) {
        GlyphDetailFragment fragment = new GlyphDetailFragment();
        Bundle args = new Bundle();
        args.putInt(GLYPH_INDEX, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    private class GlyphDetailFragment extends Fragment {
        public GlyphDetailFragment() {  }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            String glyphName = mCategoryGlyphNames[getArguments().getInt(GLYPH_INDEX)];
            FontMetadata.Glyph glyph = FontMetadata.getInstance().getGlyphByName(glyphName);
            View rootView = inflater.inflate(R.layout.fragment_glyph_detail, container, false);
            // glyph name
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(glyphName);
            // glyph
            TextView glyphTv = (TextView) rootView.findViewById(R.id.glyph);
            FontMetadata.getInstance().displayGlyph(glyphTv,glyph.codepoint, mFontSize,mTypeface);
            // codepoint
            String codepointLabel = getActivity().getResources().getString(R.string.codepoint);
            TextView cpTv = (TextView)rootView.findViewById(R.id.codepoint);
            cpTv.setText(codepointLabel + glyph.codepoint);
            // alt codepoint if present
            if( glyph.alternateCodepoint != null ){
                String alternateLabel = getActivity().getResources().getString(R.string.alternate);
                TextView altTv = (TextView)rootView.findViewById(R.id.alternate_codepoint);
                altTv.setText(alternateLabel + glyph.alternateCodepoint);
            }

            return rootView;
        }
    }

}
