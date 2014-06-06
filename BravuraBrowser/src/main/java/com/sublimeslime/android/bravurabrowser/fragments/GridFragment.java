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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.AbsListView.LayoutParams;


import com.sublimeslime.android.bravurabrowser.data.Utils;
import com.sublimeslime.android.bravurabrowser.views.GlyphView;
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
        public void onGridItemClick( int position );
    }

    private float mFontSize;
    private Typeface mTypeFace;


    public GridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTypeFace = Utils.getTypefacePreference(getActivity());
        mFontSize = Float.parseFloat(prefs.getString(SettingsActivity.Settings.GRID_FONT_SIZE.toString(),"128.0f"));
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int colWidth = computeColumnWidth();
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
        IParentActivity parent = (IParentActivity)getActivity();
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setColumnWidth(colWidth);
        mGridView.setAdapter(new GlyphListAdapter(getActivity(),
                parent.getGlyphs(),
                mFontSize,
                mTypeFace));
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
                mGridView.setAdapter(new GlyphListAdapter(getActivity(),parent.getGlyphs(), mFontSize, mTypeFace));
            }
        } if(key.equals(SettingsActivity.Settings.FONT.toString())){
            mTypeFace = Utils.getTypefacePreference(getActivity());
            mGridView.setAdapter(new GlyphListAdapter(getActivity(),parent.getGlyphs(), mFontSize, mTypeFace));
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
            GlyphView gv = (GlyphView)convertView;
            Glyph g = getItem(position);
            gv.setTypeface(mGlyphTypeface);
            String uniCode = FontMetadata.getInstance().parseGlyphCodepoint(g.codepoint);
            gv.setText(uniCode);
            gv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mGlyphFontSize);
            return gv;
        }
    }

    // Compute column width off screen
    private int computeColumnWidth(){
        FrameLayout buffer = new FrameLayout( getActivity() );
        android.widget.AbsListView.LayoutParams layoutParams = new  android.widget.AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
        int maxWidth = 0;
        for( Glyph g : ((IParentActivity)getActivity()).getGlyphs() ){
            GlyphView gv = (GlyphView)((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.glyph, null);
            gv.setTypeface(mTypeFace);
            String uniCode = FontMetadata.getInstance().parseGlyphCodepoint(g.codepoint);
            gv.setText(uniCode);
            gv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mFontSize);
            int w = measureCellWidth(getActivity(), gv, buffer, layoutParams);
            maxWidth = w > maxWidth ? w : maxWidth;
        }
        return maxWidth;
    }

    private int measureCellWidth( Context context, View cell, FrameLayout buffer, LayoutParams layoutParams )
    {
        buffer.addView( cell, layoutParams);
        cell.forceLayout();
        cell.measure(1000, 1000);
        int width = cell.getMeasuredWidth();
        buffer.removeAllViews();
        return width;
    }
    private final static String TAG = GridFragment.class.getCanonicalName();
}