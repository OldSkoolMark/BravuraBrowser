package com.sublimeslime.android.bravurabrowser.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sublimeslime.android.bravurabrowser.FontMetadata;
import com.sublimeslime.android.bravurabrowser.R;

public class GlyphDetailFragment extends Fragment {
    public GlyphDetailFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        String glyphName = b.getString(GlyphDetailPagerAdapter.GLYPH_NAME);
        float fontSize = b.getFloat(GlyphDetailPagerAdapter.FONT_SIZE);
        String typefaceName = b.getString(GlyphDetailPagerAdapter.TYPEFACE_NAME);
        // TODO: get typeface from activity instead of for each fragment
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), typefaceName);

        FontMetadata.Glyph glyph = FontMetadata.getInstance().getGlyphByName(glyphName);
        View rootView = inflater.inflate(R.layout.fragment_glyph_detail, container, false);
        // glyph name
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(glyphName);
        // glyph
        TextView glyphTv = (TextView) rootView.findViewById(R.id.glyph);
        FontMetadata.getInstance().displayGlyph(glyphTv,glyph.codepoint, fontSize,typeface);
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
    public static class GlyphDetailPagerAdapter extends FragmentStatePagerAdapter {
        private String[] mGlyphNames;
        private String mTypeface;
        private float mFontsize;

        public static final String GLYPH_NAME = "glyph_name";
        public static final String FONT_SIZE  = "font_size";
        public static final String TYPEFACE_NAME  = "typeface_name";

        public GlyphDetailFragment newGlyphFragmentInstance(int sectionNumber) {
            GlyphDetailFragment fragment = new GlyphDetailFragment();
            String glyphName = mGlyphNames[sectionNumber];

            Bundle args = new Bundle();
            args.putString(GLYPH_NAME, glyphName);
            args.putFloat(FONT_SIZE, mFontsize);
            args.putString(TYPEFACE_NAME, mTypeface);
            fragment.setArguments(args);
            return fragment;
        }
        public GlyphDetailPagerAdapter(FragmentManager fm, String[] glyphNames, String typefaceAsset, float fontsize )
        {
            super(fm);
            mGlyphNames = glyphNames;
            mTypeface = typefaceAsset;
            mFontsize = fontsize;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a GlyphDetailFragment (defined as a static inner class below).
            return newGlyphFragmentInstance(position);
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
