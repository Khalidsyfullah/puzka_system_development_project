package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;


public class SeekbarProgress extends AppCompatSeekBar {
    public SeekbarProgress (Context context) {
        super(context);
    }

    public SeekbarProgress (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SeekbarProgress (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        int thumb_x = (int) (( (double)this.getProgress()/this.getMax() ) * (double)this.getWidth());
        float middle = (float) (this.getHeight());

        @SuppressLint("DrawAllocation") Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        int prog = this.getProgress() + 5;
        c.drawText(""+prog, thumb_x, middle, paint);
    }
}