package com.g.baseassignments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class canvas18 extends View {
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private float mX, mY;
    private static canvas18 cc;
    private static final float TOUCH_TOLERANCE = 4;
     static  int currentColor= Color.RED;
     static  int backgroundColor=Color.WHITE;
    public canvas18(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(currentColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);
        mCanvas = new Canvas();
        mPath = new Path();
    }

    public void clear() {
        backgroundColor = Color.WHITE;
        paths.clear();
        // normal();
        invalidate();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path p : paths){
            canvas.drawPath(p, mPaint);
        }
        canvas.drawPath(mPath, mPaint);
    }
    public void onClickUndo () {
        if (paths.size()>0) {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }else{

        }
    }

    public void onClickRedo (){
        if (undonePaths.size()>0){
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }else {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                undonePaths.clear();
                mPath.reset();
                mPaint.setColor(currentColor);
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                    mX = x;
                    mY = y;
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX, mY);
                mCanvas.drawPath(mPath, mPaint);
                paths.add(mPath);
                mPath = new Path();
                invalidate();
                break;
        }
        return true;
    }
}
