package com.akapps.puzka;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;

public class LinedEdittext extends androidx.appcompat.widget.AppCompatEditText{
    Rect mRect;
    Paint mPaint;

    public LinedEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.parseColor("#BDBDBD"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setLineHeight(100);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int line_height = getLineHeight();
        int count = height / line_height;

        if (getLineCount() > count)
            count = getLineCount();

        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r) + 20;

        for (int i = 0; i < count; i++) {
            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
            baseline += getLineHeight();
        }
        super.onDraw(canvas);
    }
}
