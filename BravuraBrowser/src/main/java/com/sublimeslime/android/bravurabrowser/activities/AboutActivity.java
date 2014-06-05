package com.sublimeslime.android.bravurabrowser.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sublimeslime.android.bravurabrowser.R;
import com.sublimeslime.android.bravurabrowser.data.Utils;

import java.io.IOException;
import java.io.InputStream;

public class AboutActivity extends ActionBarActivity {

    public static void start(Context c){
        Intent i = new Intent(c, AboutActivity.class);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AboutTextFragment())
                    .commit();
        }
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

    public static class AboutTextFragment extends Fragment {

        public AboutTextFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            Activity a = getActivity();
            View rootView = inflater.inflate(R.layout.fragment_about, container, false);
            ((TextView)rootView.findViewById(R.id.aboutTv)).setText(getHtmlReadme());
            String fontName = Utils.getFontnamePreference(getActivity());
            String fontVersion = a.getResources().getString(R.string.bravura_version);
            a.getActionBar().setTitle(fontName + " v"+fontVersion);

            a.getActionBar().setSubtitle("SMuFL v"+a.getResources().getString(R.string.smufl_version));
            return rootView;
        }
        private String getHtmlReadme(){
            try {
                InputStream input = getActivity().getAssets().open("main.html");
                int size = input.available();
                byte[] buffer = new byte[size];
                input.read(buffer);
                input.close();
                String text = new String(buffer);
                return Html.fromHtml(text).toString();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return "";
            }
        }
    }

    private final static String TAG  = AboutActivity.class.getCanonicalName();
}
