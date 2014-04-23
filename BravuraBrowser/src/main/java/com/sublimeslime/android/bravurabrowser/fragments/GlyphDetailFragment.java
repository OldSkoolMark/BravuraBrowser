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

    public interface IParentData {
        public String getGlyphName();
        public float getFontSize();
        public Typeface getTypeface();
    }
    public GlyphDetailFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        IParentData parent = (IParentData)getActivity();

        FontMetadata.Glyph glyph = FontMetadata.getInstance().getGlyphByName(parent.getGlyphName());
        View rootView = inflater.inflate(R.layout.fragment_glyph_detail, container, false);
        // glyph name
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(parent.getGlyphName());
        // glyph
        TextView glyphTv = (TextView) rootView.findViewById(R.id.glyph);
        FontMetadata.getInstance().displayGlyph(glyphTv,glyph.codepoint,
                parent.getFontSize(),parent.getTypeface());
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

    public static final String GLYPH_NAME = "glyph_name";
    public static GlyphDetailFragment newGlyphFragmentInstance(GlyphDetailFragment.IParentData parent, int sectionNumber) {
        GlyphDetailFragment fragment = new GlyphDetailFragment();
        return fragment;
    }

}
