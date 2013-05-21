package net.yoojia.imagemap.support;

import android.graphics.Canvas;
import android.graphics.PointF;

public class CircleShape extends Shape {
	
	private PointF center;
	private float radius;
	private float originRadius;

	public CircleShape(Object tag, int coverColor) {
		super(tag, coverColor);
	}

    /**
     * Set Center,radius
     * @param coords centerX,CenterY,radius
     */
    @Override
	public void setValues(float...coords){
        if(coords == null || coords.length != 3){
            throw new IllegalArgumentException("Please set values with 3 paramters: centerX,centerY,radius");
        }
        final float centerX = coords[0];
        final float centerY = coords[1];
        final float radius = coords[2];
		set(new PointF(centerX, centerY), radius);
	}
	
	public void set(PointF center,float radius){
		this.center = center;
		this.radius = radius;
        this.originRadius = radius;
	}

    @Override
	public PointF getCenter(){
		return center;
	}

	@Override
	public void draw(Canvas canvas) {
		drawPaint.setAlpha(alaph);
		canvas.drawCircle(center.x, center.y, radius, drawPaint);
	}

	@Override
	public void scale(float scale,float centerX,float centerY) {
        PointF newCenter = ScaleUtility.scaleByPoint(center.x,center.y,centerX,centerY,scale);
        radius *= scale;
        center.set(newCenter.x,newCenter.y);
        //update the bubble position
        if( displayBubble != null ){
            displayBubble.resetPosition(this);
        }
	}

    @Override
    public void translate(float deltaX, float deltaY) {
        center.x += deltaX;
        center.y += deltaY;
    }

    @Override
    public boolean inArea(float x, float y) {
        boolean ret = false;
        float dx = center.x - Math.abs(x);
        float dy = center.y - Math.abs(y);
        float d = (float)Math.sqrt((dx*dx)+(dy*dy));
        if (d<radius) {
            ret = true;
        }
        return ret;
    }

}
