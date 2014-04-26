package com.sublimeslime.android.bravurabrowser.activities;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;
import com.sublimeslime.android.bravurabrowser.data.FontMetadata;

import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.ViewSMuFLFontApplication;
import com.sublimeslime.android.bravurabrowser.fragments.GridFragment;

public class MainActivity extends Activity implements GridFragment.IParentActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private Typeface mTypeface;
    private float mGridFontSize;
    private ArrayAdapter<GlyphRange> mRangeAdapter;
    private ArrayList<Glyph> mCurrentGlyphs = new ArrayList<Glyph>();
    private Long mGlyphArrayListKey;


    // GridFragment.IParentActivity implementation

    @Override
    public ArrayList<Glyph> getGlyphs() {
        return mCurrentGlyphs;
    }

    @Override
    public Typeface getTypeface() {
        return mTypeface;
    }

    @Override
    public float getFontSize() {
        return mGridFontSize;
    }

    @Override
    public void onGridItemClick(int position){
        mGlyphArrayListKey = ((ViewSMuFLFontApplication)getApplication()).addGlyphArrayList(mCurrentGlyphs);
        GlyphDetailActivity.start(this, position, mGlyphArrayListKey);

    }

    // Lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate()");
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mGridFontSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.GRID_FONT_SIZE.toString(), "64.0f"));
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        ArrayList<GlyphRange> grList = new ArrayList<GlyphRange>();
        mRangeAdapter = new RangeListAdapter(MainActivity.this, grList);
        mDrawerList.setAdapter(mRangeAdapter);

        mTypeface = ((ViewSMuFLFontApplication)getApplication()).getTypeface();
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
 //       getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(R.string.no_category_selected);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close)  /* "close drawer" description for accessibility */
        {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        new LoadGlyphsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Handle nav drawer list item selection

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        List<String> ranges = FontMetadata.getInstance().getRangeNames();
        if( position < ranges.size()){
            replaceGridViewFragment(ranges.get(position));
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void replaceGridViewFragment(String rangeName){
        if( rangeName != null ) {

            mCurrentGlyphs = FontMetadata.getInstance().getGlyphsForRange(rangeName);
            getActionBar().setTitle(FontMetadata.getInstance().getGlyphRange(rangeName).description);
            Fragment fragment = new GridFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, rangeName);
            ft.addToBackStack(rangeName);
            ft.commit();
        }
    }
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * List adapter maps range name to range description
     */
    public class RangeListAdapter extends ArrayAdapter<GlyphRange>{

        public RangeListAdapter(Context c, List<GlyphRange> range ){
            super(c, R.layout.drawer_list_item);
            addAll(range);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = convertView == null ? inflater.inflate(R.layout.drawer_list_item,null) : convertView;
             ((TextView) convertView).setText(getItem(position).description);
            return convertView;
        }
    }

    private class LoadGlyphsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                FontMetadata.getInstance().parseGlyphNames(MainActivity.this, "SMuFL/glyphnames.json");
                FontMetadata.getInstance().parseGlyphRanges(MainActivity.this, "SMuFL/ranges.json");
            } catch( IOException e){
                Log.e(TAG, "error parsing glyph names: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mRangeAdapter.addAll(FontMetadata.getInstance().getGlyphRanges());
            mRangeAdapter.notifyDataSetChanged();
        }
    }

    private final static String TAG = MainActivity.class.getCanonicalName();
}