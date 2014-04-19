package com.sublimeslime.android.bravurabrowser;


import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
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
import android.support.v7.widget.PopupMenu;
import android.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import com.sublimeslime.android.bravurabrowser.FontMetadata.*;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private Typeface mTypeface;
    private float mGridFontSize;
    private boolean mGridFontSizeChanged = false;
    private float mDetailFontSize;
    private ArrayAdapter<String> mCategoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate()");
        mDetailFontSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.DETAIL_FONT_SIZE.toString(),"128.0f"));
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mCategoryAdapter = new CategoryListAdapter(MainActivity.this);
        mDrawerList.setAdapter(mCategoryAdapter);

        mTypeface = Typeface.createFromAsset(getAssets(), "bravura/Bravura.otf");
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
 //       getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("no category selected");

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
    private enum InstanceStateKey { CATEGORY_POSITION }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart()");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume()");
        // Font size setting was changed. Set flag so that when returning to the grid view it gets redrawn.
        float fontSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.Settings.GRID_FONT_SIZE.toString(),"64.0f"));
        mGridFontSizeChanged = fontSize != mGridFontSize ? true : false;
        mGridFontSize = fontSize;
     }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        ComponentName cn = getComponentName();
        SearchableInfo si = searchManager.getSearchableInfo(cn);
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

    private void replaceGridViewFragment(String tag, float fontSize){
        if( tag != null ) {
            getActionBar().setTitle(tag);
            Fragment fragment = new GridFragment();
            Bundle b = new Bundle();
            b.putString("category", tag);
            b.putFloat("font_size", fontSize);
            fragment.setArguments(b);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, tag);
            ft.addToBackStack(tag);
            ft.commit();
        }
    }
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        List<String> categories = FontMetadata.getInstance().getCategories();
        if( position < categories.size()){
            String glyphClassName = categories.get(position);
            replaceGridViewFragment(glyphClassName, mGridFontSize);
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
  //      setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
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

    public class GridFragment extends Fragment implements AdapterView.OnItemClickListener {
        private float mFontSize;
        private String mCategory;
        public GridFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
            Bundle b = getArguments();
            mCategory = b.getString("category");
            mFontSize = b.getFloat("font_size");
            GridView gridView = (GridView)rootView.findViewById(R.id.gridview);
            gridView.setAdapter(new GlyphListAdapter(mCategory));
            gridView.setOnItemClickListener(this);
            return rootView;
        }
        @Override
        public void onResume(){
            super.onResume();
            Log.d(TAG,"GridFragment.onResume()");
            if( mGridFontSizeChanged ){
                mGridFontSizeChanged = false;
                replaceGridViewFragment(mCategory, mGridFontSize);
            }
        }

        /**
         * Launch GlyphDetailActivity on clicking a glyph
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GlyphListAdapter gla = (GlyphListAdapter)parent.getAdapter();
            Glyph g = (Glyph)gla.getItem(position);
            String glyphName = FontMetadata.getInstance().lookupGlyphKeyByCodepoints(g.codepoint, g.alternateCodepoint);
 //           Log.d(TAG,"clicked glyph: " + glyphName);
            GlyphDetailActivity.start(getActivity(), glyphName);
        }
    }

    public class GlyphListAdapter extends ArrayAdapter<FontMetadata.Glyph>{
        public GlyphListAdapter( String glyphCategory){
            super( MainActivity.this, R.layout.glyph);
            for(String name : FontMetadata.getInstance().getGlyphsNamesForCategory(glyphCategory))
            {
                add(FontMetadata.getInstance().getGlyphByName(name));
            }
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView == null ?
                    ((LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.glyph, null)
                    : convertView;
            TextView tv = (TextView)convertView;
            FontMetadata.Glyph g = (FontMetadata.Glyph)getItem(position);
            tv.setTypeface(mTypeface);
            String uniCode = FontMetadata.getInstance().parseGlyphCodepoint(g.codepoint);
            tv.setText(uniCode);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mGridFontSize);
            return tv;
        }
    }

      public class CategoryListAdapter extends ArrayAdapter<String>{
        public CategoryListAdapter(Context c){
            super(c, R.layout.drawer_list_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }

    private class LoadGlyphsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                FontMetadata.getInstance().parseGlyphNames(MainActivity.this, "bravura/glyphnames.json");
                FontMetadata.getInstance().parseGlyphCategories(MainActivity.this, "bravura/classes.json");
            } catch( IOException e){
                Log.e(TAG, "error parsing glyph names: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupCategoryAdapter();
        }
    }
    private void setupCategoryAdapter(){
        mCategoryAdapter.addAll(FontMetadata.getInstance().getCategories());
        mCategoryAdapter.notifyDataSetChanged();
    }
    private final static String TAG = MainActivity.class.getCanonicalName();
}