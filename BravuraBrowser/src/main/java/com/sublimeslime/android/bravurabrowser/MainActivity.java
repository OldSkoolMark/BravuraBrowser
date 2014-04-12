package com.sublimeslime.android.bravurabrowser;


import java.io.IOException;
import java.util.List;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
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

public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle =  "foobar";
    private Typeface mTypeface;
    private float mFontSize = 64.0f;
    private ArrayAdapter<String> mCategoryAdapter;
    private String mCurrentFragmentTag;
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
            case R.id.action_fontsize:
                View v = findViewById(R.id.action_fontsize);
                PopupMenu pm = new PopupMenu(this, v);
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        mFontSize = Float.parseFloat(menuItem.getTitle().toString());
                        replaceGridViewFragment(mCurrentFragmentTag);
                        return true;
                    }
                });
                pm.inflate(R.menu.fontsize_menu);
                pm.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void replaceGridViewFragment(String tag){
        if( tag != null ) {
            Fragment fragment = new GridFragment();
            Bundle b = new Bundle();
            b.putString("category", tag);
            fragment.setArguments(b);
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, tag);
            ft.addToBackStack(tag);
            ft.commit();
            mCurrentFragmentTag = tag;
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
            replaceGridViewFragment(glyphClassName);
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

    public class GridFragment extends Fragment {
        public GridFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
            Bundle b = getArguments();
            String category = b.getString("category");
            GridView gridView = (GridView)rootView.findViewById(R.id.gridview);
            gridView.setAdapter(new GlyphListAdapter(category));
            return rootView;
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
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mFontSize);
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
            mCategoryAdapter.addAll(FontMetadata.getInstance().getCategories());
            mCategoryAdapter.notifyDataSetChanged();
        }
    }
    private final static String TAG = MainActivity.class.getCanonicalName();
}