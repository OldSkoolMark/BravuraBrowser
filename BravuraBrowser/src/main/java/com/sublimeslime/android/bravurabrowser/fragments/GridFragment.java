package com.sublimeslime.android.bravurabrowser.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sublimeslime.android.bravurabrowser.data.FontMetadata;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;
import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.activities.SettingsActivity;

import java.util.ArrayList;

public class GridFragment extends Fragment implements AdapterView.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener{
    private GridView mGridView;
    /**
     * Parent activity support for grid adapters and views
     */
    public interface IParentActivity {
        public ArrayList<Glyph> getGlyphs();
        public Typeface getTypeface();
        public float getFontSize();
        public void onGridItemClick( int position );
    }

    private float mFontSize; // needed for pref change check

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
        // Parent activity contract with
        IParentActivity parent = (IParentActivity)getActivity();
        mFontSize = parent.getFontSize();
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(new GlyphListAdapter(getActivity(),
                parent.getGlyphs(),
                mFontSize,
                parent.getTypeface()));
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
        FontMetadata.Glyph g = gla.getItem(position);
        ((IParentActivity)getActivity()).onGridItemClick(position);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        IParentActivity parent = (IParentActivity)getActivity();
        if (key.equals(SettingsActivity.Settings.GRID_FONT_SIZE.toString())) {
            float newFontSize = Float.parseFloat(
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingsActivity.Settings.GRID_FONT_SIZE.toString(),
                    "64.0f"));
            if (mFontSize != newFontSize) {
                mFontSize = newFontSize;
                mGridView.setAdapter(new GlyphListAdapter(getActivity(),parent.getGlyphs(), mFontSize, parent.getTypeface()));
            }
        } if(key.equals(SettingsActivity.Settings.FONT.toString())){
            mGridView.setAdapter(new GlyphListAdapter(getActivity(),parent.getGlyphs(), mFontSize, parent.getTypeface()));
        }
    }


    public static class GlyphListAdapter extends ArrayAdapter<FontMetadata.Glyph> {
        private final float mGlyphFontSize;
        private final Typeface mGlyphTypeface;

        public GlyphListAdapter(Context context, ArrayList<Glyph> glyphs, float fontSize, Typeface typeFace) {
            super(context, R.layout.glyph);
            mGlyphFontSize = fontSize;
            mGlyphTypeface = typeFace;
            addAll(glyphs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView == null ?
                    ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.glyph, null)
                    : convertView;
            TextView tv = (TextView) convertView;
            Glyph g = getItem(position);
//            Log.d(TAG, g.description + " :"+g.codepoint );
            tv.setTypeface(mGlyphTypeface);
            String uniCode = FontMetadata.getInstance().parseGlyphCodepoint(g.codepoint);
            tv.setText(uniCode);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mGlyphFontSize);
            return tv;
        }
    }
    private final static String TAG = GridFragment.class.getCanonicalName();
}