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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sublimeslime.android.bravurabrowser.FontMetadata;
import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication;
import com.sublimeslime.android.bravurabrowser.fragments.GridFragment;

import java.util.ArrayList;


public class SearchResultsActivity extends ActionBarActivity implements GridFragment.IParentData{
    private ArrayList<String> mSearchResultGlyphNames;
    private String mQuery = "";
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
            Log.d(TAG, "query string: " + query);
            mQuery = query;
            new QueryTask().execute(query);
        }
    }

    @Override
    public ArrayList<String> getGlyphNames() {
        return mSearchResultGlyphNames;
    }

    @Override
    public Typeface getTypeface() {
        return ((ViewSMuFLFontApplication)getApplication()).getTypeface();
    }

    @Override
    public float getFontSize() {
        return Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.GRID_FONT_SIZE.toString(), "64.0f"));
    }

    @Override
    public void onGridItemClick(String glyphName) {

    }

    private class QueryTask extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... query) {
            return doQuery(query[0]);
        }
        private ArrayList<String> doQuery(String query){
            String glyphNames[] = FontMetadata.getInstance().getAllGlyphNames();
            ArrayList<String> matches = new ArrayList<String>();
            for( String name : glyphNames){
                if( name.matches(query)){
                    matches.add(name);
                }
            }
            return matches;
        }
        @Override
        protected void onPostExecute(ArrayList<String> results) {
            mSearchResultGlyphNames = results;
            getActionBar().setTitle(mQuery);
            Fragment fragment = new GridFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, fragment, mQuery);
            ft.addToBackStack(mQuery);
            ft.commit();
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    private final static String TAG = SearchResultsActivity.class.getCanonicalName();
}
