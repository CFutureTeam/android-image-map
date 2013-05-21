package net.yoojia.imagemap.support;

import android.graphics.*;

public abstract class Shape {

	public final int color;
	public final Object tag;

    protected Bubble displayBubble;
	protected int alaph = 255;
	protected final Paint drawPaint;

    protected final static Paint cleanPaint;

    static {
        cleanPaint = new Paint();
        cleanPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
	
	public Shape(Object tag, int coverColor){
		this.tag = tag;
		this.color = coverColor;

		drawPaint = new Paint();
		drawPaint.setColor(coverColor);
		drawPaint.setStyle(Paint.Style.FILL);
		drawPaint.setAntiAlias(true);
        drawPaint.setFilterBitmap(true);

    }

	public void setAlaph(int alaph){
		this.alaph = alaph;
	}

    public void createBubbleRelation(Bubble displayBubble) {
        this.displayBubble = displayBubble;
    }

    public void cleanBubbleRelation(){
        this.displayBubble = null;
    }

    public abstract void setValues(float...coords);

    /**
     * Set coords. Split by char ',' .
     * @param coords coords
     */
    public void setValues(String coords){
        String[] parametrs = coords.split(",");
        final int size = parametrs.length;
        final float[] args = new float[size];
        for(int i=0;i<size;i++){
            args[i] = Float.valueOf(parametrs[i].trim());
        }
        setValues(args);
    }

    public abstract void draw(Canvas canvas);

	public abstract void scale(float scale,float centerX,float centerY);

    public abstract void translate(float deltaX,float deltaY);

    public abstract boolean inArea(float x,float y);
	
    public abstract PointF getCenter();
	
}
