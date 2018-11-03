package org.sufficientlysecure.htmltextview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;

import java.lang.reflect.Field;

/**
 * Copyright (C) 2018, PING AN TECHNOLOGIES CO., LTD.
 * MatchTableRow
 * <p>
 * 用于修复tablerow控件设置divider属性时，线框不对齐问题。
 * PS：代码混淆时，请不要混淆TableRow组件
 * <p/>
 *
 * @author zhouwenhao173
 * @version 1.0
 * <p>
 * Ver 1.0, 2018/11/1, zhouwenhao173, Create file
 */
public class MatchTableRow extends TableRow {

    /**
     * TODO:
     * 以下三个属性名为android-sdk的官方属性名
     * 被改动的可能性很小
     * PS：三个属性名会被混淆，请一定记得不要混淆TableRow组件
     */
    private final String LOCATION = "LOCATION";
    private final String LOCATION_NEXT = "LOCATION_NEXT";
    private final String OFFSET = "mOffset";

    public MatchTableRow(Context context) {
        super(context);
    }

    public MatchTableRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getVirtualChildCount();
        for (int i = 0; i < count; ++i) {
            View child = getVirtualChildAt(i);
            TableRow.LayoutParams lp = (TableRow.LayoutParams) child.getLayoutParams();
            if (lp.gravity == Gravity.LEFT){
                int offset = getOffsetNext(lp);
                child.setPadding(child.getPaddingLeft(), child.getPaddingTop(),
                        child.getPaddingRight() + offset, child.getPaddingBottom());
            }else if (lp.gravity == Gravity.RIGHT){
                int offset = getOffsetLocation(lp);
                child.setPadding(child.getPaddingLeft() + offset, child.getPaddingTop(),
                        child.getPaddingRight(), child.getPaddingBottom());
                resetOffsetLocation(lp);
            }else if (lp.gravity == Gravity.CENTER){
                int offset = getOffsetLocation(lp);
                child.setPadding(child.getPaddingLeft() + offset, child.getPaddingTop(),
                        child.getPaddingRight() + offset, child.getPaddingBottom());
                resetOffsetLocation(lp);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getOffsetLocation(TableRow.LayoutParams lp){
        int set = 0;
        try {
            Field fs = lp.getClass().getDeclaredField(OFFSET);
            Field lfs = lp.getClass().getDeclaredField(LOCATION);
            fs.setAccessible(true);
            lfs.setAccessible(true);
            int location = lfs.getInt(null);
            int[] tmp = (int[]) fs.get(lp);
            set = tmp[location];
        }catch (Exception e){

        }
        return set;
    }

    private void resetOffsetLocation(TableRow.LayoutParams lp){
        try {
            Field fs = lp.getClass().getDeclaredField(OFFSET);
            Field lfs = lp.getClass().getDeclaredField(LOCATION);
            int location = lfs.getInt(lp);
            int[] tmp = (int[]) fs.get(lp);
            tmp[location] = 0;
            fs.set(lp, tmp);
        }catch (Exception e){

        }
    }


    private int getOffsetNext(TableRow.LayoutParams lp){
        int set = 0;
        try {
            Field fs = lp.getClass().getDeclaredField(OFFSET);
            Field lfs = lp.getClass().getDeclaredField(LOCATION_NEXT);
            fs.setAccessible(true);
            lfs.setAccessible(true);
            int location = lfs.getInt(null);
            int[] tmp = (int[]) fs.get(lp);
            set = tmp[location];
        }catch (Exception e){

        }
        return set;
    }

}
