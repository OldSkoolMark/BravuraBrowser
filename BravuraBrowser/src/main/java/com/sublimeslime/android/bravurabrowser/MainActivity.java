package com.sublimeslime.android.bravurabrowser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle =  "foobar";
    private FontMetadata mFontMetadata;
    private Typeface mTypeface;
    private ArrayAdapter<String> mCategoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        mFontMetadata = FontMetadata.getInstance();
        new LoadGlyphsTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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
/*            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
                */
            default:
                return super.onOptionsItemSelected(item);
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
            // update the main content by replacing fragments
            Bundle b = new Bundle();
            b.putString("category", glyphClassName);
            Fragment fragment = new GridFragment();
            fragment.setArguments(b);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
  //      setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public class GridFragment extends Fragment {
        public GridFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
            Bundle b = getArguments();
            String category = b.getString("category");
            mGridView = (GridView)rootView.findViewById(R.id.gridview);
            mGridView.setAdapter(new GlyphListAdapter(category));
            return rootView;
        }

        private GridView mGridView;
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
            String uniCode = g.codepoint.replace("U+","\\u");
            tv.setText(uniCode);
            return tv;
        }
    }

      public class CategoryListAdapter extends ArrayAdapter<String>{
        private List<String> mCategoryTitles;
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
                mFontMetadata.parseGlyphNames(MainActivity.this, "bravura/glyphnames.json");
                mFontMetadata.parseGlyphClasses(MainActivity.this, "bravura/classes.json");
            } catch( IOException e){
                Log.e(TAG, "error parsing glyph names: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mCategoryAdapter.addAll(mFontMetadata.getCategories());
            mCategoryAdapter.notifyDataSetChanged();
 //           Log.d(TAG,"num categories = "+mFontMetadata.getCategories().size()+" count = "+mCategoryAdapter.getCount());
        }
    }
    private final static String TAG = MainActivity.class.getCanonicalName();
}