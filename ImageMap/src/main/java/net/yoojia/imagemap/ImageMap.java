package net.yoojia.imagemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import net.yoojia.imagemap.support.Bubble;
import net.yoojia.imagemap.support.Shape;
import net.yoojia.imagemap.support.ShapeExtension;

import java.util.List;

/**
 * author :  chenyoca@gmail.com
 * date   :  2013-5-19
 * An HTML map like widget in an Android view
 */
public class ImageMap extends FrameLayout implements ShapeExtension,ShapeExtension.OnShapeClickListener{

    private OnShapeClickListener onShapeClickListener;

    private HighlightImageView highlightImageView;
    private Bubble bubble;

    public ImageMap(Context context) {
        this(context,null);
    }

    public ImageMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialImageView(context);
    }

    public ImageMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialImageView(context);
    }

    private void initialImageView(Context context){
        highlightImageView = new HighlightImageView(context);
        highlightImageView.setOnShapeClickListener(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(highlightImageView,params);
    }

    public void setOnShapeClickListener(OnShapeClickListener listener){
        this.onShapeClickListener = listener;
    }

    /**
     * Set a bubble view.
     * @param bubbleView a view object for display on image map.
     */
    public void setBubbleView(View bubbleView) {
        setBubbleView(bubbleView,null);
    }

    /**
     * Aet a bubble view and it's displayer interface.
     * @param bubbleView a view object for display on image map.
     * @param displayer the display interface for bubble view render.
     */
    public void setBubbleView(View bubbleView,Bubble.BubbleDisplayer displayer){
        if(bubbleView == null){
            throw new IllegalArgumentException("View for bubble cannot be null !");
        }
        bubble = new Bubble(bubbleView);
        bubble.setDisplayer(displayer);
        addView(bubble.view);
        bubble.view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void addShape(Shape shape) {
        highlightImageView.addShape(shape);
    }

    @Override
    public void addShapes(List<Shape> shapes) {
        highlightImageView.addShapes(shapes);
    }

    @Override
    public void removeShape(Object tag) {
        highlightImageView.removeShape(tag);
    }

    @Override
    public final void onShapeClick(Shape shape, float xOnImage, float yOnImage) {
        for(Shape item : highlightImageView.getShapes()){
            item.cleanBubbleRelation();
        }
        if(bubble != null){
            bubble.showAtShape(shape);
        }
        if(onShapeClickListener != null){
            onShapeClickListener.onShapeClick(shape,xOnImage,yOnImage);
        }
    }

    /**
     * set a bitmap for image map.
     * @param bitmap image
     */
    public void setMapBitmap(Bitmap bitmap){
        highlightImageView.setImageBitmap(bitmap);
    }
}
