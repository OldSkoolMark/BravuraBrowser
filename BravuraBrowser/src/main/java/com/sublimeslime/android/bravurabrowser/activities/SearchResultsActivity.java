package com.sublimeslime.android.bravurabrowser.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.sublimeslime.android.bravurabrowser.data.FontMetadata;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;
import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication;
import com.sublimeslime.android.bravurabrowser.fragments.GridFragment;

import java.util.ArrayList;

public class SearchResultsActivity extends Activity implements GridFragment.IParentActivity {
    private Long mGlyphResultsKey;
    // IParentActivity contract
    @Override
    public ArrayList<Glyph> getGlyphs() {
        return ((ViewSMuFLFontApplication)getApplication()).getGlyphArrayList(mGlyphResultsKey);
    }

    @Override
    public void onGridItemClick(int position) {
          GlyphDetailActivity.start(this, position, mGlyphResultsKey);
    }

    // Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        ColorDrawable colorDrawable = new ColorDrawable( Color.TRANSPARENT );
        getWindow().setBackgroundDrawable( colorDrawable );
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
            getActionBar().setTitle(getString(R.string.searching_for) + " " + query);
        }
    }

    /**
     * Async search task starts SearchResultDisplayActivity with results from
     * font name or codepoint regex. Queries prefixed with U+ or u+ are interpreted
     * as codepoint queries.
     */
    private class QueryTask extends AsyncTask<String, Void, ArrayList<Glyph>>{
        private String mQuery;
        @Override
        protected ArrayList<Glyph> doInBackground(String... query) {
            return doQuery(query[0]);
        }
        private ArrayList<Glyph> doQuery(String query){
            mQuery = query;
            if( mQuery.startsWith("U+")) {
                return FontMetadata.getInstance().getGlyphsByMatchingCodepoint(query.replace("U+","U\\+"));
            }else{
                return FontMetadata.getInstance().getGlyphsByMatchingDescription(mQuery);
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

         /**
          * Start search result display activity instead of directly installing the GridFragment here
          * to steer clear of fragment lifecycle issues.
          *
          * @param results list of glyphs
          */
        @Override
        protected void onPostExecute(ArrayList<Glyph> results) {
            ViewSMuFLFontApplication a = (ViewSMuFLFontApplication) getApplication();
            mGlyphResultsKey = a.addGlyphArrayList(results);
            SearchResultsDisplayActivity.start(SearchResultsActivity.this, mGlyphResultsKey, mQuery );
            finish();
        }
    }
    private final static String TAG = SearchResultsActivity.class.getCanonicalName();
}
