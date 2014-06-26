package com.sublimeslime.android.bravurabrowser.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.sublimeslime.android.bravurabrowser.R;


/**
 * TODO: document your custom view class.
 */
public class GlyphView extends View {
    private final static int WIDTH_FUDGE = 20; // neither bounds or width works.
    private final static int HEIGHT_FUDGE = 10;
    private int mLineColor = Color.RED; // TODO: use a default from R.color...
    private TextPaint mTextPaint;
    private Paint mLinePaint;
    private float mViewWidth;
    private float mTextWidth;
    private float mTextHeight;
    private float mTextSize;
    private boolean mDrawLines = false;
    Path mBaselinePath = new Path();

    private final static String TAG = GlyphView.class.getCanonicalName();
    private CharSequence mText = "";

    public GlyphView(Context context) {
        super(context);
        init(null, 0);
    }

    public GlyphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GlyphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GlyphView, defStyle, 0);

        if( a.hasValue(R.styleable.GlyphView_LineColor)) {
            mLineColor = a.getColor(R.styleable.GlyphView_LineColor, mLineColor);
            mDrawLines = true;
        }
         a.recycle();
        if( mDrawLines) {
            // setup paint for baseline
            mLinePaint = new Paint();
            mLinePaint.setStrokeWidth(2.0f);
            mLinePaint.setStyle(Paint.Style.STROKE);
        }
        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        if( mText != null ) {
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setColor(0xff000000);
            mTextWidth = mTextPaint.measureText(mText.toString());

            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mTextHeight = fontMetrics.bottom;
        }
    }

    /** @inheritdoc
     *
     * Measure the view based on the glyph. Neither TextPaint.getBounds or TextPaint.getWidth returns
     * enough space for glyphs that draw something left of the origin, so we use a fudge factor
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(mText.toString(),0,1,bounds);
        int desiredWidth = bounds.width()+ WIDTH_FUDGE;
        int desiredHeight = (int)(fm.bottom - fm.top + HEIGHT_FUDGE); // top is negative
 //       Log.d(TAG, "font bottom: " + (int) mTextPaint.getFontMetrics().bottom + "font top: " + (int) mTextPaint.getFontMetrics().top);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int start = getWidth() > 2 * (int)mTextWidth ? getWidth()/ 2 : WIDTH_FUDGE;
        // Draw font metrics lines
        if( mDrawLines ) {
            drawVerticalLine(canvas, start, getResources().getColor(R.color.start_color));
            // Draw top
            //           drawHorizontalLine(canvas, 0.0f, 0, getResources().getColor(R.color.top_color));
            // Draw ascent
            drawHorizontalLine(canvas, -mTextPaint.getFontMetrics().top + mTextPaint.getFontMetrics().ascent, 180, getResources().getColor(R.color.ascent_color));
            // Draw baseline
            drawHorizontalLine(canvas, -mTextPaint.getFontMetrics().top, 0, getResources().getColor(R.color.baseline_color));
            // Draw descent
            drawHorizontalLine(canvas, -mTextPaint.getFontMetrics().top + mTextPaint.getFontMetrics().descent, 0, getResources().getColor(R.color.descent_color));
            // Draw botton
//            drawHorizontalLine(canvas, -mTextPaint.getFontMetrics().top + mTextPaint.getFontMetrics().bottom, 180, getResources().getColor(R.color.bottom_color));
        }
        // Draw glyph
        canvas.drawText(mText.toString(),
                start,
                -mTextPaint.getFontMetrics().top,
                mTextPaint);

    }
    private void drawHorizontalLine(Canvas canvas, float y, int phase, int color){
        mBaselinePath.reset();
        mBaselinePath.moveTo(0, y);
        mBaselinePath.lineTo((float) getWidth(), y);
//        mLinePaint.setPathEffect(new DashPathEffect(new float[]{14.0f, 14.0f}, phase));
        mLinePaint.setColor(color);
        canvas.drawPath(mBaselinePath, mLinePaint);
    }
    private void drawVerticalLine(Canvas canvas, float x, int color){
        mBaselinePath.reset();
        mBaselinePath.moveTo(x, 0);
        mBaselinePath.lineTo(x, (float) getHeight());
        mLinePaint.setColor(color);
        canvas.drawPath(mBaselinePath, mLinePaint);
    }
    public void setTypeface(Typeface tf) {
        if (mTextPaint.getTypeface() != tf) {
            mTextPaint.setTypeface(tf);
            invalidateTextPaintAndMeasurements();
        }
    }
    public void setTextSize(int unit, float size) {
        mTextSize = size;
        Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        setRawTextSize(TypedValue.applyDimension(unit, size, r.getDisplayMetrics()));
    }

    private void setRawTextSize(float size) {
        if (size != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(size);
            invalidateTextPaintAndMeasurements();
        }
    }
    public void setText(CharSequence text){
        mText = text;
        invalidateTextPaintAndMeasurements();
    }
    /**
     * Gets the example color attribute value.
     * @return The example color attribute value.
     */
    public int getLineColor() {
        return mLineColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     * @param lineColor The example color attribute value to use.
     */
    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
        invalidateTextPaintAndMeasurements();
    }

    public void showFontMetrics(boolean show){
        mDrawLines = show;
    }
}
