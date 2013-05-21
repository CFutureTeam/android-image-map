package net.yoojia.imagemap.support;

import java.util.List;

public interface ShapeExtension{

    public interface OnShapeClickListener{
        void onShapeClick(Shape shape, float xOnImage, float yOnImage);
    }

//    void onMoving(float deltaX, float deltaY);

    void addShape(Shape shape);

    void addShapes(List<Shape> shapes);

    void removeShape(Object tag);
}