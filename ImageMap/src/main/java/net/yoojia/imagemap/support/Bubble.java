package net.yoojia.imagemap.support;

import android.graphics.PointF;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * author : chenyoca@gmail.com
 * date   : 13-5-19
 * The bubble wrapper.
 */
public class Bubble {

    public interface BubbleDisplayer {
        void onDisplay(Shape shape, View bubbleView);
    }

    private BubbleDisplayer displayer;

    public final View view;

    public Bubble(View view){
        this.view = view;
        final int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wrapContent,wrapContent);
        this.view.setLayoutParams(params);
        this.view.setClickable(true);
    }

    /**
     * Set the interface for bubble view render.
     * @param displayer displayer implments
     */
    public void setDisplayer(BubbleDisplayer displayer) {
        this.displayer = displayer;
    }

    /**
     * Show the bubble view on the shape.
     * @param shape the shape to show on
     */
    public void showAtShape(Shape shape){
        if(view == null) return;
        shape.createBubbleRelation(this);
        resetPosition(shape);
        if (displayer != null){
            displayer.onDisplay(shape, view);
        }
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Reset the bubble view to the other shape.
     * @param shape
     */
    public void resetPosition(Shape shape){
        if(view != null){
            PointF center = shape.getCenter();
            float posX = center.x - view.getWidth()/2;
            float posY = center.y - view.getHeight();
            if(isSDK_11_Later()){
                view.setX(posX);
                view.setY(posY);
            }else{
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                if(params != null){
                    params.leftMargin = (int)posX;
                    params.topMargin = (int)posY;
                }
            }

        }
    }

    /**
     * The image translated, sync translate the bubble view.
     * @param deltaX
     * @param deltaY
     */
    public void onTranslate(float deltaX,float deltaY){
        if(view != null && view.isShown()){
            if(isSDK_11_Later()){
                float x = view.getX();
                float y = view.getY();
                view.setX(x+deltaX);
                view.setY(y+deltaY);
            }else{
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                if(params != null){
                    params.leftMargin += (int)deltaX;
                    params.topMargin += (int)deltaY;
                }
            }
        }
    }

    boolean isSDK_11_Later(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
