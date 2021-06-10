package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DrawingtoolView extends View {

    private Bitmap mBitmap;
    Canvas mCanvas;
    Paint   mBitmapPaint;

    Context context;
    boolean eraserOn = false;
    boolean newAdded = false;
    boolean allClear = false;
    Path drawPath;
    Paint drawPaint;

    MaskFilter mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
            0.4f, 6, 3.5f);

    MaskFilter  mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

    boolean penSelected = true,  eraserSelected = false;
    boolean isEmbrossed = false;
    boolean isBlur = false;

    private int currentColor = Color.BLACK;
    private int strokeWidth = 20;

    SharedPreferences sharedPreferences;

    ArrayList<Bitmap> bitmap = new ArrayList<>();
    ArrayList<Bitmap> undoBitmap = new ArrayList<>();

    public DrawingtoolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        drawPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setColor(currentColor);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(strokeWidth);
        sharedPreferences = new SecurePreferences(context);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mBitmap==null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    public void setColor(int color)
    {
        currentColor = color;
    }


    public void setStrokeWidth(int width)
    {
        strokeWidth = width;
    }

    public void onEraserController(boolean flag)
    {
        if (flag) {
            eraserOn = true;
            penSelected = false;
            eraserSelected = true;
            drawPaint.setColor(getResources().getColor(android.R.color.transparent, null));
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        }
        else {
            eraserOn = false;
            eraserSelected = true;
            penSelected = false;
            drawPaint.setColor(currentColor);
            drawPaint.setXfermode(null);
        }
    }

    public void setEmbrossed(boolean b)
    {
        isEmbrossed = b;
        if(isEmbrossed && isBlur)
        {
            isBlur = false;
        }
    }

    public String getBitmap()
    {
        ArrayList<String> arKL = new ArrayList<>();
        for(Bitmap bt : bitmap){
            arKL.add(getStringFromBitmap(bt));
        }
        return new Gson().toJson(arKL);
    }


    public String getUndoBitmap()
    {
        ArrayList<String> arKL = new ArrayList<>();
        for(Bitmap bt : undoBitmap){
            arKL.add(getStringFromBitmap(bt));
        }
        return new Gson().toJson(arKL);
    }


    public String getMainBitmap()
    {
        return getStringFromBitmap(mBitmap);
    }


    public void setValueFromString(String s1, String s2, String s3)
    {
        ArrayList<String> arrayList;
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        arrayList = new Gson().fromJson(s1, type);
        if(bitmap.size()> 0){
            bitmap.clear();
        }
        for(String sf : arrayList){
            bitmap.add(getBitmapFromString(sf));
        }

        ArrayList<String> arrayList1;
        arrayList1 = new Gson().fromJson(s2, type);
        if(undoBitmap.size()> 0){
            undoBitmap.clear();
        }
        for(String sf : arrayList1){
            undoBitmap.add(getBitmapFromString(sf));
        }
        Bitmap bml = getBitmapFromString(s3);
        mBitmap = bml.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
    }


    public void setBlur (boolean b)
    {
        isBlur = b;
        if(isBlur && isEmbrossed){
            isEmbrossed = false;
        }
    }

    public void removeMusks()
    {
        isBlur = false;
        isEmbrossed = false;
    }


    public void undoChanges()
    {
        if(newAdded) {
            bitmap.add(mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable()));
            newAdded=false;
        }
        if (bitmap.size()>1)
        {
            undoBitmap.add(bitmap.remove(bitmap.size()-1));
            mBitmap= bitmap.get(bitmap.size()-1).copy(mBitmap.getConfig(), mBitmap.isMutable());
            mCanvas = new Canvas(mBitmap);
            invalidate();
            if(bitmap.size()==1)
                allClear = true;
        }

    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public Bitmap saveImage()
    {
        return mBitmap;
    }


    public void redoChanges()
    {
        if (undoBitmap.size()>0)
        {
            bitmap.add(undoBitmap.remove(undoBitmap.size()-1));
            mBitmap= bitmap.get(bitmap.size()-1).copy(mBitmap.getConfig(),mBitmap.isMutable());
            mCanvas = new Canvas(mBitmap);
            invalidate();
        }
    }

    public int getCurrentColor()
    {
        return currentColor;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(penSelected || eraserSelected) {
            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    newAdded = true;
                    if(!allClear)
                        bitmap.add(mBitmap.copy(mBitmap.getConfig(),mBitmap.isMutable()));
                    else allClear = false;
                    drawPaint.setColor(currentColor);
                    drawPaint.setStrokeWidth(strokeWidth);
                    if(isEmbrossed){
                        if(drawPaint.getMaskFilter() != mEmboss)
                        {
                            drawPaint.setMaskFilter(mEmboss);
                        }
                    }
                    else if(isBlur){
                        if(drawPaint.getMaskFilter() != mBlur)
                        {
                            drawPaint.setMaskFilter(mBlur);
                        }
                    }
                    else
                    {
                        drawPaint.setMaskFilter(null);
                    }
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (eraserOn) {
                        drawPath.lineTo(touchX, touchY);
                        mCanvas.drawPath(drawPath, drawPaint);
                        drawPath.reset();
                        drawPath.moveTo(touchX, touchY);
                    } else {
                        drawPaint.setColor(currentColor);
                        drawPaint.setStrokeWidth(strokeWidth);
                        if(isEmbrossed){
                            if(drawPaint.getMaskFilter() != mEmboss)
                            {
                                drawPaint.setMaskFilter(mEmboss);
                            }
                        }
                        else if(isBlur){
                            if(drawPaint.getMaskFilter() != mBlur)
                            {
                                drawPaint.setMaskFilter(mBlur);
                            }
                        }
                        else
                        {
                            drawPaint.setMaskFilter(null);
                        }
                        drawPath.lineTo(touchX, touchY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    drawPaint.setColor(currentColor);
                    drawPaint.setStrokeWidth(strokeWidth);
                    if(isEmbrossed){
                        if(drawPaint.getMaskFilter() != mEmboss)
                        {
                            drawPaint.setMaskFilter(mEmboss);
                        }
                    }
                    else if(isBlur){
                        if(drawPaint.getMaskFilter() != mBlur)
                        {
                            drawPaint.setMaskFilter(mBlur);
                        }
                    }
                    else
                    {
                        drawPaint.setMaskFilter(null);
                    }
                    mCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;

                default:
                    return false;
            }

            invalidate();
            return true;
        }

        return false;
    }


}
