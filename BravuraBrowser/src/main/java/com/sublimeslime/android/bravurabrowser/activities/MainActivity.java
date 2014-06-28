package com.sublimeslime.android.bravurabrowser.activities;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
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
import com.sublimeslime.android.bravurabrowser.data.Utils;
import com.sublimeslime.android.bravurabrowser.fragments.GridFragment;
import com.sublimeslime.android.bravurabrowser.views.GlyphView;

public class MainActivity extends Activity implements GridFragment.IParentActivity, SharedPreferences.OnSharedPreferenceChangeListener {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout mContentFrame;
    private TextView mMainText;
    private CharSequence mTitle;
    private Typeface mTypeface;
    private float mGridFontSize;
    private ArrayAdapter<GlyphRange> mRangeAdapter;
    private String mSelectedRange;
    private Long mGlyphArrayListKey;


    // GridFragment.IParentActivity implementation

    @Override
    public ArrayList<Glyph> getGlyphs() {
        return ((ViewSMuFLFontApplication) getApplication()).getGlyphArrayList(mGlyphArrayListKey);
    }

    @Override
    public void onGridItemClick(int position) {
        GlyphDetailActivity.start(this, position, mGlyphArrayListKey);
    }

    // Lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mContentFrame = (RelativeLayout) findViewById(R.id.content_frame);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mGridFontSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.GRID_FONT_SIZE.toString(), "64.0f"));
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        ArrayList<GlyphRange> grList = new ArrayList<GlyphRange>();
        mRangeAdapter = new RangeListAdapter(MainActivity.this, grList);
        mDrawerList.setAdapter(mRangeAdapter);

        mMainText = (TextView) findViewById(R.id.main_text);

        mTypeface = Utils.getTypefacePreference(this);
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.no_category_selected);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close)  /* "close drawer" description for accessibility */ {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if ((FontMetadata.getInstance().getGlyphRanges().size() > 0)) {
            mRangeAdapter.addAll(FontMetadata.getInstance().getGlyphRanges());
            // restore last selected range on configuration change
            if (savedInstanceState != null) {
                mSelectedRange = savedInstanceState.getString(InstanceStateKey.SELECTED_RANGE.name());
                replaceGridViewFragment(mSelectedRange);
            }
        } else {
            new LoadGlyphsTask().execute();
        }
    }

    private enum InstanceStateKey { SELECTED_RANGE };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(InstanceStateKey.SELECTED_RANGE.name(), mSelectedRange);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
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
            case R.id.action_github:
                String url = getString(R.string.bravura_browser_github_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsActivity.Settings.GRID_FONT_SIZE.toString())) {
            float newFontSize = Float.parseFloat(
                    PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.GRID_FONT_SIZE.toString(),
                            "64.0f"));
            Log.d(TAG,"on pref change old="+mGridFontSize+ " new="+newFontSize);
            if (mGridFontSize != newFontSize) {
                mGridFontSize = newFontSize;
            }
        } if(key.equals(SettingsActivity.Settings.FONT.toString())){
//            mGridView.setAdapter(new GlyphListAdapter(getActivity(),parent.getGlyphs(), mFontSize, mTypeface));
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
            mSelectedRange = ranges.get(position);
            replaceGridViewFragment(mSelectedRange);
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void replaceGridViewFragment(String rangeName){
        if( rangeName != null ) {
            ArrayList<Glyph> glyphs = FontMetadata.getInstance().getGlyphsForRange(rangeName);
            mGlyphArrayListKey = ((ViewSMuFLFontApplication)getApplication()).addGlyphArrayList(glyphs);
            String rangeDescription = FontMetadata.getInstance().getGlyphRange(rangeName).description;
            String[] statusLine = FontMetadata.get2LineDescription(rangeDescription);
            if( statusLine[0] != null ){
                getActionBar().setTitle(statusLine[0]);
                if( statusLine[1] != null ){
                    getActionBar().setSubtitle(statusLine[1]);
                }
            }
            Fragment fragment = new GridFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
            ft.replace(R.id.content_frame, fragment, rangeName);
            ft.commit();
        }
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
            if( convertView == null) {
                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.drawer_list_item,null);
                TextView tv1 = (TextView)convertView.findViewById(R.id.text1);
                TextView tv2 = (TextView)convertView.findViewById(R.id.text2);
                convertView.setTag(new RangeListItemViewHolder(tv1, tv2));
            }
            RangeListItemViewHolder vh = (RangeListItemViewHolder)convertView.getTag();
            String line[] = FontMetadata.get2LineDescription(getItem(position).description);
            if( line[0] != null){
                vh.tv1.setText(line[0]);
            }
            if( line[1] != null){
                vh.tv2.setText(line[1]);
                vh.tv2.setVisibility(View.VISIBLE);
            } else {
                vh.tv2.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    public class RangeListItemViewHolder {
        public RangeListItemViewHolder(TextView line1, TextView line2){
            tv1 = line1;
            tv2 = line2;
        }
        public final TextView tv1;
        public final TextView tv2;
    }

    private class LoadGlyphsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                FontMetadata.getInstance().parseGlyphNames(MainActivity.this, getString(R.string.smufl_glyphnames_asset));
                FontMetadata.getInstance().parseGlyphRanges(MainActivity.this, getString(R.string.smufl_ranges_asset));
            } catch( IOException e){
                Log.e(TAG, "error parsing glyph names: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mRangeAdapter.addAll(FontMetadata.getInstance().getGlyphRanges());
            mRangeAdapter.notifyDataSetChanged();
            setProgressBarIndeterminateVisibility(false);
        }
    }



    private final static String TAG = MainActivity.class.getCanonicalName();
}