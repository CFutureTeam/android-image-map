/*
 Copyright (c) 2012 Robert Foss, Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.yoojia.imagemap;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {
	
    final Matrix imageMatrix = new Matrix();
    final Matrix imageSavedMatrix = new Matrix();
    
    final Matrix overlayerMatrix = new Matrix();

    private static final float FRICTION = 0.9f;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    float redundantXSpace, redundantYSpace;
    float right, bottom, origWidth, origHeight, bmWidth, bmHeight;
    float width, height;
    PointF last = new PointF();
    PointF mid = new PointF();
    PointF start = new PointF();
    float[] matrixValues;
    float matrixX, matrixY;

    float saveScale = 1f;
    float minScale = 1f;
    float maxScale = 5f;
    float oldDist = 1f;

    PointF lastDelta = new PointF(0, 0);
    float velocity = 0;
    long lastDragTime = 0;
    
    private Context mContext;
    private ScaleGestureDetector mScaleDetector;
    
    Paint overlayerPaint;
    
    public boolean onLeftSide = false, onTopSide = false, onRightSide = false, onBottomSide = false;

    public TouchImageView(Context context) {
        this(context,null);
    }
    
    public TouchImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        super.setClickable(true);
        this.mContext = context;
        init();
    }
    
    private OnTouchListener touchListener = new OnTouchListener() {

    	final static float MAX_VELOCITY = 1.5f;
    	
    	private long dragTime ;
    	private float dragVelocity;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
            mScaleDetector.onTouchEvent(event);

            fillMatrixXY();
            PointF curr = new PointF(event.getX(), event.getY());
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = DRAG;
                    float xOnView =  event.getX();
                    float yOnView = event.getY();
                    onClick(xOnView,yOnView);
                    imageSavedMatrix.set(imageMatrix);
                    last.set(event.getX(), event.getY());
                    start.set(last);

                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        imageSavedMatrix.set(imageMatrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                	if(mode == DRAG){
                		velocity = dragVelocity;
                	}
                    mode = NONE;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    velocity = 0;
                    imageSavedMatrix.set(imageMatrix);
                    oldDist = spacing(event);
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                    	dragTime = System.currentTimeMillis();
                    	dragVelocity = (float)distanceBetween(curr, last) / (dragTime - lastDragTime) * FRICTION;
                    	dragVelocity = Math.min(MAX_VELOCITY,dragVelocity);
                        lastDragTime = dragTime;
                        float deltaX = curr.x - last.x;
                        float deltaY = curr.y - last.y;
                        checkAndSetTranslate(deltaX, deltaY);
                        lastDelta.set(deltaX, deltaY);
                        last.set(curr.x, curr.y);
                    }
                    break;
                }

            setImageMatrix(imageMatrix);
            invalidate();
            return false;
		}
    };
    
	protected void init()
    {
        imageMatrix.setTranslate(1f, 1f);
        overlayerMatrix.setTranslate(1f, 1f);
        matrixValues = new float[9];
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(imageMatrix);
        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
        setOnTouchListener(touchListener);
        overlayerPaint = new Paint();
        overlayerPaint.setAntiAlias(true);
    }

    protected void onClick(float xOnView,float yOnView){}
	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float deltaX = lastDelta.x * velocity, deltaY = lastDelta.y * velocity;
        if (deltaX > width || deltaY > height) return;
        velocity *= FRICTION;
        if (Math.abs(deltaX) < 0.1 && Math.abs(deltaY) < 0.1) {
        	return;
        }
        checkAndSetTranslate(deltaX, deltaY);
        setImageMatrix(imageMatrix);
    }
    
    protected void postTranslate(float deltaX, float deltaY){
    	imageMatrix.postTranslate(deltaX,deltaY);
    	overlayerMatrix.postTranslate(deltaX,deltaY);
    }
    
    protected void postScale(float scaleFactor, float scaleCenterX, float scaleCenterY){
    	imageMatrix.postScale(scaleFactor, scaleFactor, scaleCenterX, scaleCenterY);
        overlayerMatrix.postScale(scaleFactor, scaleFactor, scaleCenterX, scaleCenterY);
    }
    
    private void checkAndSetTranslate(float deltaX, float deltaY)
    {
        float scaleWidth = Math.round(origWidth * saveScale);
        float scaleHeight = Math.round(origHeight * saveScale);
        fillMatrixXY();
        if (scaleWidth < width) {
            deltaX = 0;
            if (matrixY + deltaY > 0)
                deltaY = -matrixY;
            else if (matrixY + deltaY < -bottom)
                deltaY = -(matrixY + bottom);
        } else if (scaleHeight < height) {
            deltaY = 0;
            if (matrixX + deltaX > 0)
                deltaX = -matrixX;
            else if (matrixX + deltaX < -right)
                deltaX = -(matrixX + right);
        }
        else {
            if (matrixX + deltaX > 0)
                deltaX = -matrixX;
            else if (matrixX + deltaX < -right)
                deltaX = -(matrixX + right);

            if (matrixY + deltaY > 0)
                deltaY = -matrixY;
            else if (matrixY + deltaY < -bottom)
                deltaY = -(matrixY + bottom);
        }
        postTranslate(deltaX, deltaY);
        checkSiding();
    }
    
    private void checkSiding()
    {
        fillMatrixXY();
        
        float scaleWidth = Math.round(origWidth * saveScale);
        float scaleHeight = Math.round(origHeight * saveScale);
        onLeftSide = onRightSide = onTopSide = onBottomSide = false;
        if (-matrixX < 10.0f ) onLeftSide = true;
        if ((scaleWidth >= width && (matrixX + scaleWidth - width) < 10) ||
            (scaleWidth <= width && -matrixX + scaleWidth <= width)) onRightSide = true;
        if (-matrixY < 10.0f) onTopSide = true;
        if (Math.abs(-matrixY + height - scaleHeight) < 10.0f) onBottomSide = true;
    }
    
    private void calcPadding()
    {
        right = width * saveScale - width - (2 * redundantXSpace * saveScale);
        bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
    }
    
    private void fillMatrixXY()
    {
        imageMatrix.getValues(matrixValues);
        matrixX = matrixValues[Matrix.MTRANS_X];
        matrixY = matrixValues[Matrix.MTRANS_Y];
    }
    
    @Override
    public void setImageBitmap(Bitmap bm) {
        bmWidth = bm.getWidth();
        bmHeight = bm.getHeight();
        super.setImageBitmap(bm);

    }
    
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        float scale = saveScale;

        imageMatrix.setScale(scale, scale);
        overlayerMatrix.setScale(scale, scale);

        // Center the image
        redundantYSpace = height - (scale * bmHeight) ;
        redundantXSpace = width - (scale * bmWidth);
        redundantYSpace /= (float)2;
        redundantXSpace /= (float)2;

        // show the image to left and top of the view.
        postTranslate(0, 0);

        origWidth = width - 2 * redundantXSpace;
        origHeight = height - 2 * redundantYSpace;
        calcPadding();
        setImageMatrix(imageMatrix);
    }

    private double distanceBetween(PointF left, PointF right)
    {
        return Math.sqrt(Math.pow(left.x - right.x, 2) + Math.pow(left.y - right.y, 2));
    }
    
    private float spacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            //float scaleFactor = (float)Math.min(Math.max(.95f, detector.getScaleFactor()), 1.05);
            float origScale = saveScale;
            saveScale *= scaleFactor;
            if (saveScale > maxScale) {
                saveScale = maxScale;
                scaleFactor = maxScale / origScale;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                scaleFactor = minScale / origScale;
            }
            right = width * saveScale - width - (2 * redundantXSpace * saveScale);
            bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
            
            // 显示的图片比源图片要小时进行缩放
            if (origWidth * saveScale <= width || origHeight * saveScale <= height) {
            	final float scaleCenterX = width / 2;
            	final float scaleCenterY = height / 2;
                postScale(scaleFactor, scaleCenterX, scaleCenterY);

                //在边缘时缩小图片的平移修正
                if (scaleFactor < 1) {
                    imageMatrix.getValues(matrixValues);
                    float x = matrixValues[Matrix.MTRANS_X];
                    float y = matrixValues[Matrix.MTRANS_Y];
                    if (scaleFactor < 1) {
                        if (Math.round(origWidth * saveScale) < width) {
                        	float deltaX = 0,deltaY = 0;
                            if (y < -bottom){
                            	deltaY = -(y + bottom);
                                postTranslate(deltaX, deltaY);
                            }
                            else if (y > 0){
                            	deltaY = -y;
                            	postTranslate(deltaX, deltaY);
                            }
                        } else {
                        	float deltaX = 0,deltaY = 0;
                            if (x < -right){
                            	deltaX = -(x + right);
                            	postTranslate(deltaX, deltaY);
                            }
                            else if (x > 0){
                            	deltaX = -x;
                                postTranslate(deltaX, deltaY);
                            }
                        }
                    }
                }
            } else {
            	// 图片已经被放大，以触摸点为中心，缩放图片。
                postScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
                imageMatrix.getValues(matrixValues);
                float x = matrixValues[Matrix.MTRANS_X];
                float y = matrixValues[Matrix.MTRANS_Y];
                
                // 缩小图片时，如果在图片边缘缩小，为避免边缘出现空白，对图片进行平移处理
                if (scaleFactor < 1) {
                	float deltaX = 0,deltaY = 0;
                    if (x < -right){
                    	deltaX = -(x + right);
                    	deltaY = 0;
                    	postTranslate(deltaX, deltaY);
                    }
                    else if (x > 0){
                    	deltaX = -x;
                    	deltaY = 0;
                    	postTranslate(deltaX, deltaY);
                    }
                    if (y < -bottom){
                    	deltaX = 0;
                    	deltaY = -(y + bottom);
                    	postTranslate(deltaX, deltaY);
                    }
                    else if (y > 0){
                    	deltaX = 0;
                    	deltaY = -y;
                    	postTranslate(deltaX, deltaY);
                    }
                }
            }
            return true;
        }
    }
}