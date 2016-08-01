package ru.yandex.yamblz.ui.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawerView extends View implements Drawer {

    private static final int BACKGROUND_COLOR = Color.WHITE;

    private Bitmap mBitmap;
    private Paint mPaint;
    private Canvas mCanvas;
    private Path mPath;
    private float mSize = 10;
    private int mColor;
    private Tool mTool = Tool.Pencil;

    private float mPrevTouchX, mPrevTouchY;

    public DrawerView(Context context) {
        super(context);
        init();
    }

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPath = new Path();

        pencil();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int curW = mBitmap != null ? mBitmap.getWidth() : 0;
        int curH = mBitmap != null ? mBitmap.getHeight() : 0;

        if(curW >= w && curH >= h) {
            return;
        }

        if(curW < w) {
            curW = w;
        }

        if(curH < h) {
            curH = h;
        }

        Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if(mBitmap != null) {
            newCanvas.drawBitmap(mBitmap, 0, 0, null);
        }
        mBitmap = newBitmap;
        mCanvas = newCanvas;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                writeTouchCoordinates(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                handleTouch(event);
                writeTouchCoordinates(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void writeTouchCoordinates(MotionEvent event) {
        mPrevTouchX = event.getX();
        mPrevTouchY = event.getY();
    }

    private void handleTouch(MotionEvent event) {
        switch (mTool) {
            case Pencil:
                drawPencil(event);
                break;
            case Brush:
                break;
            case Eraser:
                drawEraser(event);
                break;

        }
    }

    private void drawPencil(MotionEvent event) {
        drawLine(mPrevTouchX, mPrevTouchY, event.getX(), event.getY());
    }

    private void drawEraser(MotionEvent event) {
        drawLine(mPrevTouchX, mPrevTouchY, event.getX(), event.getY());
    }

    private void drawCircle(float x, float y, float radius) {
        mCanvas.drawCircle(x, y, radius, mPaint);
        invalidate();
    }

    private void drawRect(float left, float top, float right, float bottom) {
        mCanvas.drawRect(left, top, right, bottom, mPaint);
        invalidate();
    }

    private void drawLine(float x1, float y1, float x2, float y2) {
        mPath.reset();
        mPath.moveTo(x1, y1);
        mPath.lineTo(x2, y2);
        mCanvas.drawPath(mPath, mPaint);
        invalidate();
    }



    @Override
    public void setSize(float size) {
        mSize = size;

        mPaint.setStrokeWidth(size / 2);
    }

    @Override
    public float getSize() {
        return mSize;
    }

    @Override
    public void brush() {
        mTool = Tool.Brush;

        mPaint.reset();

        mPaint.setColor(mColor);
    }

    @Override
    public void pencil() {
        mTool = Tool.Pencil;

        mPaint.reset();

        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mSize / 2);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void eraser() {
        mTool = Tool.Eraser;

        mPaint.reset();

        mPaint.setAntiAlias(true);
        mPaint.setColor(BACKGROUND_COLOR);
        mPaint.setStrokeWidth(mSize / 2);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void disable() {
        mPaint.reset();
        mTool = Tool.NO;
    }

    @Override
    public void setColor(int color) {
        mColor = color;

        if(mTool != Tool.Eraser) {
            mPaint.setColor(mColor);
        }
    }

    @Override
    public int getColor() {
        return mColor;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Tool getTool() {
        return mTool;
    }

    @Override
    public void selectTool(Tool tool) {
        switch (tool) {
            case Pencil:
                pencil();
                break;
            case Brush:
                brush();
                break;
            case Eraser:
                eraser();
                break;
            case NO:
                disable();
                break;
        }
    }
}
