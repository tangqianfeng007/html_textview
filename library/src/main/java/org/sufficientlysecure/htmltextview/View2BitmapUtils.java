package org.sufficientlysecure.htmltextview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Copyright (C) 2018, PING AN TECHNOLOGIES CO., LTD.
 * View2BitmapUtils
 * <p>
 * 将布局绘制成bitmap
 * <p/>
 *
 * @author zhouwenhao173
 * @version 1.0
 * <p>
 * Ver 1.0, 2018/11/1, zhouwenhao173, Create file
 */
public class View2BitmapUtils {
    /**
     * 把view转换成bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap view2Bitmap(View view){
        layout2Measure(view);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        if (width == 0 || height == 0){
            return null;
        }
        Bitmap bmp  = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        view.draw(canvas);
        return bmp;
    }

    /**
     * 对view进行layout和measure
     * @param view
     */
    private static void layout2Measure(View view){
        if (view != null){
            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(spec, spec);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }
}
