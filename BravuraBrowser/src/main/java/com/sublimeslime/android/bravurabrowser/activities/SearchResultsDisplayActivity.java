package com.sublimeslime.android.bravurabrowser.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata.Glyph;
import com.sublimeslime.android.bravurabrowser.fragments.GridFragment;

import java.util.ArrayList;

public class SearchResultsDisplayActivity extends ActionBarActivity implements GridFragment.IParentActivity {
    private Long mGlyphArrayListKey;
    private String mSearchQuery;
    public enum IntentKey { GLYPH_ARRAY_LIST_KEY, SEARCH_QUERY }
    public final static void start(Context c, Long glyphArrayListKey, String query ){
        Intent i = new Intent(c,SearchResultsDisplayActivity.class);
        i.putExtra(IntentKey.GLYPH_ARRAY_LIST_KEY.name(), glyphArrayListKey);
        i.putExtra(IntentKey.SEARCH_QUERY.name(), query);
        c.startActivity(i);
    }


    // IParentActivity contract

    @Override
    public ArrayList<Glyph> getGlyphs() {
        ViewSMuFLFontApplication a = (ViewSMuFLFontApplication)getApplication();
        return a.getGlyphArrayList(mGlyphArrayListKey);
    }

    @Override
    public void onGridItemClick(int position) {
        GlyphDetailActivity.start(this, position, mGlyphArrayListKey);
    }

    // Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        mGlyphArrayListKey = getIntent().getLongExtra(IntentKey.GLYPH_ARRAY_LIST_KEY.name(),-1L);
        mSearchQuery = getIntent().getStringExtra(IntentKey.SEARCH_QUERY.name());
        setContentView(R.layout.activity_search_results_display);
        getActionBar().setTitle(mSearchQuery);
        Fragment fragment = new GridFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment, mSearchQuery);
        ft.addToBackStack(mSearchQuery);
        ft.commit();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private final static String TAG = SearchResultsDisplayActivity.class.getCanonicalName();
}
