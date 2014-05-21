package com.sublimeslime.android.bravurabrowser.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.sublimeslime.android.bravurabrowser.data.FontMetadata;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;
import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication;
import com.sublimeslime.android.bravurabrowser.fragments.GridFragment;

import java.util.ArrayList;

public class SearchResultsActivity extends ActionBarActivity implements GridFragment.IParentActivity {

    // IParentActivity contract
    @Override
    public ArrayList<Glyph> getGlyphs() {
        return mCurrentGlyphs;
    }

    @Override
    public Typeface getTypeface() {
        return ((ViewSMuFLFontApplication)getApplication()).getTypeface();
    }



    @Override
    public void onGridItemClick(int position) {
        Long key = ((ViewSMuFLFontApplication)getApplication()).addGlyphArrayList(mCurrentGlyphs);

        GlyphDetailActivity.start(this, position, key);
    }

    // Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            new QueryTask().execute(query.toUpperCase());
        }
    }

    private ArrayList<Glyph> mCurrentGlyphs;
    // Async search and result display
    private class QueryTask extends AsyncTask<String, Void, ArrayList<Glyph>>{
        private String mQuery;
        @Override
        protected ArrayList<Glyph> doInBackground(String... query) {
            return doQuery(query[0]);
        }
        private ArrayList<Glyph> doQuery(String query){
            mQuery = query;
            if( mQuery.startsWith("U+")) {
                return FontMetadata.getInstance().getGlyphByMatchingCodepoint(query);
            }else{
                return FontMetadata.getInstance().getGlyphsByMatchingDescription(mQuery);
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Glyph> results) {
            mCurrentGlyphs = results;
            if( mCurrentGlyphs.size()>0) {
                getActionBar().setTitle(mQuery);
                Fragment fragment = new GridFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, fragment, mQuery);
                ft.addToBackStack(mQuery);
                ft.commit();
            } else {
                getActionBar().setTitle(getResources().getString(R.string.not_found));
            }
        }
    }
    private final static String TAG = SearchResultsActivity.class.getCanonicalName();
}
