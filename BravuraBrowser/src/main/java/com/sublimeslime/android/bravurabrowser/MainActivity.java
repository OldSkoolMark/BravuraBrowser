package com.sublimeslime.android.bravurabrowser;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private FontMetadata mFontMetadata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFontMetadata = FontMetadata.getInstance();
        new LoadGlyphsTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    private class LoadGlyphsTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                mFontMetadata.parseGlyphNames(MainActivity.this, "glyphnames.json");
                mFontMetadata.parseGlyphClasses(MainActivity.this, "classes.json");
            } catch( IOException e){
                Log.e(TAG, "error parsing glyph names: " + e.getMessage());
            }
            return null;
        }
    }
    private final String TAG = MainActivity.class.getCanonicalName();
}
