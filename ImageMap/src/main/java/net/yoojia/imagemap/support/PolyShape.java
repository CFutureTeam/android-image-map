/**
 * author : 桥下一粒砂 (chenyoca@gmail.com)
 * date   : 13-5-21
 * TODO
 */
package net.yoojia.imagemap.support;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;

public class PolyShape extends Shape {

    ArrayList<Float> xPoints = new ArrayList<Float>();
    ArrayList<Float> yPoints = new ArrayList<Float>();

    // centroid point for this poly
    float _x;
    float _y;

    int pointCount;

    // bounding box
    float top = -1;
    float bottom = -1;
    float left = -1;
    float right = -1;

    public PolyShape(Object tag, int coverColor) {
        super(tag, coverColor);
    }

    @Override
    public void setValues(float... coords) {
        int i = 0;
        while ((i+1)<coords.length) {
            float x = coords[i];
            float y = coords[i+1];
            xPoints.add(x);
            yPoints.add(y);
            top = (top==-1) ? y : Math.min(top,y);
            bottom = (bottom==-1) ? y : Math.max(bottom,y);
            left = (left==-1) ? x : Math.min(left,x);
            right = (right==-1) ? x : Math.max(right,x);
            i+=2;
        }
        pointCount = xPoints.size();

        // add point zero to the end to make
        // computing area and centroid easier
        xPoints.add(xPoints.get(0));
        yPoints.add(yPoints.get(0));

        computeCentroid();
    }

    public void computeCentroid() {
        double cx = 0.0, cy = 0.0;
        for (int i = 0; i < pointCount; i++) {
            cx = cx + (xPoints.get(i) + xPoints.get(i+1)) * (yPoints.get(i) * xPoints.get(i+1) - xPoints.get(i) * yPoints.get(i+1));
            cy = cy + (yPoints.get(i) + yPoints.get(i+1)) * (yPoints.get(i) * xPoints.get(i+1) - xPoints.get(i) * yPoints.get(i+1));
        }
        cx /= (6 * area());
        cy /= (6 * area());
        _x = Math.abs((int)cx);
        _y = Math.abs((int)cy);
    }

    public double area() {
        double sum = 0.0;
        for (int i = 0; i < pointCount; i++) {
            sum = sum + (xPoints.get(i) * yPoints.get(i+1)) - (yPoints.get(i) * xPoints.get(i+1));
        }
        sum = 0.5 * sum;
        return Math.abs(sum);
    }

    @Override
    public void draw(Canvas canvas) {
        drawPaint.setAlpha(alaph);
        Path path = new Path();
        float startX = xPoints.get(0);
        float startY = yPoints.get(0);
        path.moveTo(startX, startY);
        for (int i = 0; i < pointCount; i++) {
            float pointX = xPoints.get(i);
            float pointY = yPoints.get(i);
            path.lineTo(pointX , pointY);
        }
        path.close();
        canvas.drawPath(path, drawPaint);
    }

    @Override
    public void scale(float scale, float centerX, float centerY) {
        for(int i=0;i<pointCount;i++){
            PointF newPoint = ScaleUtility.scaleByPoint(xPoints.get(i),yPoints.get(i),centerX,centerY,scale);
            xPoints.set(i, newPoint.x);
            yPoints.set(i, newPoint.y);
        }
    }

    @Override
    public void translate(float deltaX, float deltaY) {
        for(int i=0;i<pointCount;i++){
            float x = xPoints.get(i) + deltaX;
            float y = yPoints.get(i) + deltaY;
            xPoints.set(i, x);
            yPoints.set(i, y);
        }
    }

    @Override
    public boolean inArea(float x, float y) {
        int i, j;
        boolean c = false;
        for (i = 0, j = pointCount - 1; i < pointCount; j = i++) {
            if ( ((yPoints.get(i)>y) != (yPoints.get(j)>y)) &&
                    (x < (xPoints.get(j)- xPoints.get(i)) * (y- yPoints.get(i)) / (yPoints.get(j)- yPoints.get(i)) + xPoints.get(i)) )
                c = !c;
        }
        return c;
    }

    @Override
    public PointF getCenter() {
        return null;
    }
}
