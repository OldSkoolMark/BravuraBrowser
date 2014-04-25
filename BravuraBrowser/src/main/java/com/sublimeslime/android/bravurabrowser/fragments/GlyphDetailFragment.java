package com.sublimeslime.android.bravurabrowser.fragments;

import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sublimeslime.android.bravurabrowser.FontMetadata;
import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication;

public class GlyphDetailFragment extends Fragment {

    public interface IParentActivity {
        public float getFontSize();
        public Typeface getTypeface();
    }
    public GlyphDetailFragment() {  }
    private  enum IntentKey {POSITION};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        IParentActivity parent = (IParentActivity)getActivity();
        Bundle b = getArguments();
        String glyphName = ((ViewSMuFLFontApplication)getActivity().getApplication()).getGlyphNameList().get(b.getInt(IntentKey.POSITION.name()));
        FontMetadata.Glyph glyph = FontMetadata.getInstance().getGlyphByName(glyphName);
        View rootView = inflater.inflate(R.layout.fragment_glyph_detail, container, false);
        // glyph name
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(glyphName);
        // glyph
        TextView glyphTv = (TextView) rootView.findViewById(R.id.glyph);
        FontMetadata.getInstance().displayGlyph(glyphTv,glyph.codepoint, parent.getFontSize(),parent.getTypeface());
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

    public static GlyphDetailFragment newGlyphFragmentInstance(int position) {
        Bundle b = new Bundle();
        b.putInt(IntentKey.POSITION.name(), position);
        GlyphDetailFragment fragment = new GlyphDetailFragment();
        fragment.setArguments(b);
        return fragment;
    }

}
