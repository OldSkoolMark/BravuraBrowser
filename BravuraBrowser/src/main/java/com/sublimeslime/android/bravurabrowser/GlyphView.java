package com.sublimeslime.android.bravurabrowser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class GlyphView extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
//    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private Paint mLinePaint;
    private float mTextWidth;
    private float mTextHeight;
    private float mTextSize;
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

        mExampleString = a.getString(R.styleable.GlyphView_exampleString);
        mExampleColor = a.getColor( R.styleable.GlyphView_exampleColor, mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
//        mExampleDimension = a.getDimension(
//                R.styleable.GlyphView_exampleDimension,
//                mExampleDimension);

        if (a.hasValue(R.styleable.GlyphView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable( R.styleable.GlyphView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();
        mLinePaint = new Paint();
        mLinePaint.setColor(0xffff0000);
        mLinePaint.setStrokeWidth(2.0f);
        mLinePaint.setStyle(Paint.Style.STROKE);
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
            mTextPaint.setColor(mExampleColor);
            mTextWidth = mTextPaint.measureText(mText.toString());

            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mTextHeight = fontMetrics.bottom;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        float widths[] = new float[1];
        mTextPaint.getTextWidths(mText.toString(),widths);
        int desiredWidth = (int)widths[0];
        int desiredHeight = (int)(mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().top); // top is negative
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

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        // Draw the text.
        canvas.drawText(mText.toString(),
//                paddingLeft + (contentWidth - mTextWidth) / 2,
 //               paddingTop + (contentHeight + mTextHeight) / 2,
                0,
                -mTextPaint.getFontMetrics().top,
                mTextPaint);
        canvas.drawLine(0.0f,
                -mTextPaint.getFontMetrics().top,
                (float)mTextWidth,
                -mTextPaint.getFontMetrics().top, mLinePaint);
/*
        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
        */
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
     * Gets the example string attribute value.
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     * @return The example dimension attribute value.
     */
 /*   public float getExampleDimension() {
        return mExampleDimension;
    }
*/
    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     * @param exampleDimension The example dimension attribute value to use.
     */
  /*  public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }
*/
    /**
     * Gets the example drawable attribute value.
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
