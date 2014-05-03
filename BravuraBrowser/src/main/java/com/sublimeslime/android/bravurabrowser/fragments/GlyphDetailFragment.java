package com.sublimeslime.android.bravurabrowser.fragments;

        import android.support.v4.app.Fragment;
        import android.graphics.Typeface;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.sublimeslime.android.bravurabrowser.data.FontMetadata;
        import com.sublimeslime.android.bravurabrowser.data.FontMetadata.*;
        import com.sublimeslime.android.bravurabrowser.R;

        import java.util.ArrayList;

public class GlyphDetailFragment extends Fragment {

    public interface IParentActivity {
        public ArrayList<Glyph> getGlyphs();
        public float getFontSize();
        public Typeface getTypeface();
    }
    public GlyphDetailFragment() {  }
    private  enum IntentKey {POSITION};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IParentActivity parent = (IParentActivity)getActivity();
        int position = getArguments().getInt(IntentKey.POSITION.name());
        Glyph glyph = parent.getGlyphs().get(position);
        View rootView = inflater.inflate(R.layout.fragment_glyph_detail, container, false);
        // glyph
        TextView glyphTv = (TextView) rootView.findViewById(R.id.glyph);
        FontMetadata.getInstance().displayGlyph( glyphTv, glyph.codepoint, parent.getFontSize(), parent.getTypeface());
        // codepoint
        StringBuffer codepointLabel = new StringBuffer(getActivity().getResources().getString(R.string.codepoint));
        TextView cpTv = (TextView)rootView.findViewById(R.id.codepoint);
        cpTv.setText(codepointLabel.append( glyph.codepoint));
        return rootView;
    }

    public static GlyphDetailFragment newGlyphFragmentInstance(int position) {
        Bundle b = new Bundle();
        b.putInt(IntentKey.POSITION.name(), position);
        GlyphDetailFragment fragment = new GlyphDetailFragment();
        fragment.setArguments(b);
        return fragment;
    }
    private final static String TAG = GlyphDetailFragment.class.getCanonicalName();
}
