package com.sublimeslime.android.bravurabrowser.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sublimeslime.android.bravurabrowser.FontMetadata;
import com.sublimeslime.android.bravurabrowser.activities.GlyphDetailActivity;
import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.activities.SettingsActivity;

import java.util.ArrayList;

public class GridFragment extends Fragment implements AdapterView.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private float mFontSize;
    private String mCategory;
    private GridView mGridView;
    private Typeface mTypeface;

    public GridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
        Bundle b = getArguments();
        mCategory = b.getString("category");
        mFontSize = b.getFloat("font_size");
        mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "bravura/Bravura.otf");
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(new GlyphListAdapter(getActivity(), FontMetadata.getInstance().getGlyphsNamesForCategory(mCategory), mFontSize, mTypeface));
        mGridView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onDestroy() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    /**
     * Launch GlyphDetailActivity on clicking a glyph
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GlyphListAdapter gla = (GlyphListAdapter) parent.getAdapter();
        FontMetadata.Glyph g = (FontMetadata.Glyph) gla.getItem(position);
        String glyphName = FontMetadata.getInstance().lookupGlyphKeyByCodepoints(g.codepoint, g.alternateCodepoint);
        //           Log.d(TAG,"clicked glyph: " + glyphName);
        GlyphDetailActivity.start(getActivity(), glyphName, mCategory);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsActivity.Settings.GRID_FONT_SIZE.toString())) {
            float fontSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingsActivity.Settings.GRID_FONT_SIZE.toString(), "64.0f"));
            if (mFontSize != fontSize) {
                mFontSize = fontSize;
                mGridView.setAdapter(new GlyphListAdapter(getActivity(), FontMetadata.getInstance().getGlyphsNamesForCategory(mCategory), mFontSize, mTypeface));
            }
        }
    }


    public static class GlyphListAdapter extends ArrayAdapter<FontMetadata.Glyph> {
        private final float mFontSize;
        private final Typeface mTypeface;

        public GlyphListAdapter(Context context, ArrayList<String> glyphs, float fontSize, Typeface typeFace) {
            super(context, R.layout.glyph);
            mFontSize = fontSize;
            mTypeface = typeFace;
            for (String name : glyphs) {
                add(FontMetadata.getInstance().getGlyphByName(name));
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView == null ?
                    ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.glyph, null)
                    : convertView;
            TextView tv = (TextView) convertView;
            FontMetadata.Glyph g = (FontMetadata.Glyph) getItem(position);
            tv.setTypeface(mTypeface);
            String uniCode = FontMetadata.getInstance().parseGlyphCodepoint(g.codepoint);
            tv.setText(uniCode);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mFontSize);
            return tv;
        }
    }
}