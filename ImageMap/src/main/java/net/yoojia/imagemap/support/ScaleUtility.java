package net.yoojia.imagemap.support;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * author : chenyoca@gmail.com
 * date   : 13-5-19
 * A utility for scale options.
 */
public class ScaleUtility {

    /**
     * Get the new position object when scale with a given point.
     * @param targetPointX the x position before scale
     * @param targetPointY the y position before scale
     * @param scaleCenterX the scale point ,x position
     * @param scaleCenterY the scale point ,y position
     * @param scale scale
     * @return the new position after scale
     */
    public static PointF scaleByPoint(float targetPointX,float targetPointY,float scaleCenterX,float scaleCenterY,float scale){
        Matrix matrix = new Matrix();
        // move the matrix to target position
        // then, scale with the given point and scale.
        matrix.preTranslate(targetPointX,targetPointY);
        matrix.postScale(scale,scale,scaleCenterX,scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return new PointF(values[Matrix.MTRANS_X],values[Matrix.MTRANS_Y]);
    }
}
