BravuraBrowser
==============

Tool primarily for developers
--------------------------
BravuraBrowser is a native Android application that allows you to select and display Bravura music font glyphs. In addition to being an entertaining compendium of both common and arcane music notations,it  may be used by Android developers of notation-rich applications to find and use one of the 3000+ glyphs provided by Bravura.

Demo APK available from Play store
--------

The app is available on the Google Play Store:

http://play.google.com/store/apps/details?id=com.sublimeslime.android.bravurabrowser&hl=en

SMuFL and Bravura
-----------------
Bravura is the first SMuFL-compliant music font and is available under the SIL Open Font License. This application aids Android developers of music applications by providing allowing you to select and display Bravura glyphs in a range of sizes.

Selecting glyphs
----------------
The items in the navigation drawer list correspond to the glyph ranges specified in the ranges.json file that is part of the SMuFL specification. The search action applies to either glyph names or unicode codepoint values. Any Java regular expression can be used as a query. Queries prefixed by "U+" or "u+" are interpreted as codepoint searches, all others as glyph name searches.

Grid and detail pages
--------------
Glyphs selected via the navigation bar or search action are displayed in a GridView. Selections from this view are displayed as swipeable pages containing ascent, baseline, descent, and start font metrics.

Settings
----------
The settings action allows you to select font (either Bravura or BravuraText), and to seperately specify font sizes for the GridView and detail views.

References
----------

For comprehensive information about Bravura and SMuFL, please visit [www.smufl.org] .