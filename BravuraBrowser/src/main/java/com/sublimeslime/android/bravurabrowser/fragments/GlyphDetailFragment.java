package com.sublimeslime.android.bravurabrowser.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication;
import com.sublimeslime.android.bravurabrowser.activities.SettingsActivity;
import com.sublimeslime.android.bravurabrowser.data.Utils;
import com.sublimeslime.android.bravurabrowser.views.GlyphView;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;
import com.sublimeslime.android.bravurabrowser.R;

import java.util.ArrayList;

public class GlyphDetailFragment extends Fragment {
    private Typeface mTypeface;
    private float mFontSize;

    public interface IParentActivity {
        public ArrayList<Glyph> getGlyphs();
    }
    public GlyphDetailFragment() {  }
    private  enum IntentKey {POSITION};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mFontSize = Float.parseFloat( prefs.getString(SettingsActivity.Settings.DETAIL_FONT_SIZE.toString(), "128.0f"));
        mTypeface = Utils.getTypefacePreference(getActivity());

        IParentActivity parent = (IParentActivity)getActivity();
        int position = getArguments().getInt(IntentKey.POSITION.name());
        Glyph glyph = parent.getGlyphs().get(position);
        View rootView = inflater.inflate(R.layout.fragment_glyph_detail, container, false);
        // glyph
        GlyphView glyphTv = (GlyphView) rootView.findViewById(R.id.glyph);
        FontMetadata.getInstance().displayGlyph(glyphTv, glyph.codepoint, mFontSize, mTypeface);
        // codepoint
        TextView cpTv = (TextView)rootView.findViewById(R.id.codepoint);
        cpTv.setText( glyph.codepoint );
        // SMuFL version
        TextView smuflTv = (TextView)rootView.findViewById(R.id.tv_smufl);
        smuflTv.setText("SMuFL v"+getResources().getString(R.string.smufl_version));
        // Font name and version
        TextView smuflFontTv = (TextView)rootView.findViewById(R.id.tv_smufl_font);
        ViewSMuFLFontApplication.SMuFLFont font = ViewSMuFLFontApplication.getSMuFLFont(Utils.getFontnamePreference(getActivity()));
        smuflFontTv.setText(font.getDescription()+ " v"+font.getVersion());
        return rootView;
    }

    public static GlyphDetailFragment newGlyphFragmentInstance(int position) {
        Bundle b = new Bundle();
        b.putInt(IntentKey.POSITION.name(), position);
        GlyphDetailFragment fragment = new GlyphDetailFragment();
        fragment.setArguments(b);
        return fragment;
    }
    private final static String TAG = GlyphDetailFragment.class.getCanonicalName();
}
