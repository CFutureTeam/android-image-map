# Android Image Map 

一个类似HTML map标签功能的Android组件。可以实现HTML里图片热点映射。

An android view like html map tag.

## 项目结构

	此项目是在Android Stdio IDE中创建的，其目录结构与Eclipse很不同。如果使用Eclipse打开，需要把路径设置到 ...../ImageMap/src/main 里。

## 截图

	![ScreenShot](https://github.com/chenyoca/ImageMap/ScreenShot.png)

## 特点

	 * 支持图像缩放和拖动。图中色块覆盖的区域为图片热点。图像缩放和拖动时，这些热点区域也会跟着缩放和移动。

	 * 支持 Circle,Rect,Poly 三种形状。对应的类为 CircleShape, RectShape, PolyShape。向ImageMap对象中添加即可。

	 * 支持纯生HTML map数据，可以直接把HTML Map生成的coords数据直接设置到Shape中。

	 * 支持Bitmap对象，res目录下的drawable文件等数据来源。

	 * 在代码中创建或者XML中布局。

 ## 使用

 详细代码见MainActivity.java文件。

 ```java
   
	//取得在XML中布局的ImageMap对象，并设置图片

    ImageMap map = (ImageMap) findViewById(R.id.imagemap);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.pink_girl,new BitmapFactory.Options());
        map.setMapBitmap(bitmap);

    // 设置Shape被点击时的监听
    // 注意：同一时刻只有一个Shape被监听点击
	map.setOnShapeClickListener(new ShapeExtension.OnShapeClickListener() {
	    @Override
	    public void onShapeClick(Shape shape, float xOnImage, float yOnImage) {
	        String msg = "Shape "+shape.tag+" clicked !";
	        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
	    }
	});

	// 圆形
	// "black"是唯一标识这个Shape的字符串，不可重复。
    Shape black = new CircleShape("black", Color.BLACK);
    black.setValues(633,122,15);
    map.addShape(black);

    // 矩形
    Shape black = new CircleShape("black", Color.BLACK);
    black.setValues(633,122,15);
    map.addShape(black);

    // 多边形
    Shape black = new CircleShape("black", Color.BLACK);
    black.setValues(633,122,15);
    map.addShape(black);

 ```
