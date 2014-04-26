package com.sublimeslime.android.bravurabrowser.data;

/**
 * Created by mark on 4/25/14.
 */
public class SMuFLDbSchema {
    public enum Table { AVAILABLE_FONTS, FONT, GLYPH, CATEGORY }
    public enum AvailableFontsColumn {
        ID("_id"), // _id needed for CursorLoader
        NAME("name"),
        ASSETS_DIRECTORY("dir");
        private AvailableFontsColumn(String columnName){
            mName = columnName;
        }
        @Override
        public String toString(){
            return mName;
        }
        private final String mName;
    }

    public enum FontColumn {
        ID("_id"), // _id needed for CursorLoader
        LABEL("label");
        private FontColumn(String columnName){
            mName = columnName;
        }
        @Override
        public String toString(){
            return mName;
        }
        private final String mName;
    }

    public enum GlyphColumn {
        ID("_id"), // _id needed for CursorLoader
        LABEL("label");

        private GlyphColumn(String columnName){
            mName = columnName;
        }
        @Override
        public String toString(){
            return mName;
        }
        private final String mName;
    }

    public enum CategoryColumn {
        ID("_id"), // _id needed for CursorLoader
        LABEL("label");

        private CategoryColumn(String columnName){
            mName = columnName;
        }
        @Override
        public String toString(){
            return mName;
        }
        private final String mName;
    }

}
