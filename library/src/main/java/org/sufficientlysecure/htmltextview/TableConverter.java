package org.sufficientlysecure.htmltextview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Copyright (C) 2018, PING AN TECHNOLOGIES CO., LTD.
 * TableConverter
 * <p>
 * Description
 * 把html代码段转换成tablayout,然后把tablelayout绘制成bitmap
 *
 * @author tangqianfeng567
 * @version 1.0
 * <p>
 * Ver 1.0, 2018/10/29, tangqianfeng567, Create file
 */
public class TableConverter {

    public static Drawable convert(Context context, String html, TextView textView) {
        List<String[]> ls = null;
        try {
            ls = dom2Html(new ByteArrayInputStream(html.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        View view = renderTable(context, ls, textView);
        Bitmap b = view2Bitmap(view);
        Drawable drawable = bitmap2Drawable(b);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    public static List<String[]> dom2Html(InputStream is) throws Exception {
        int max = 0;
        List<String[]> list = new ArrayList<>();
        List<List<String>> temps = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        NodeList trList = document.getElementsByTagName("tr");
        for (int i = 0; i < trList.getLength(); i++) {
            List<String> l = new ArrayList<>();
            Node tr = trList.item(i);
            if ("tr".equalsIgnoreCase(tr.getNodeName())) {
                NodeList tdList = tr.getChildNodes();
                for (int n = 0; n < tdList.getLength(); n++) {
                    Node td = tdList.item(n);
                    if ("td".equalsIgnoreCase(td.getNodeName()) || "th".equalsIgnoreCase(td.getNodeName())) {
                        l.add(HtmlUtils.parseHtmlData(td.getTextContent()));
                    }
                }
                max = Math.max(max, l.size());
                temps.add(l);
            }
        }
        //表格每行数据格长度 补齐
        for (List<String> l : temps) {
            list.add(l.toArray(new String[max]));
        }
        return list;
    }

    /**
     * 生成原生tablelayout
     *
     * @param context
     * @param whole
     * @param textView
     * @return
     */
    private static View renderTable(Context context, List<String[]> whole, TextView textView) {
        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        TableLayout tableLayout = new TableLayout(context);

        for (int i = 0; i < whole.size(); i++) {
            String[] row = whole.get(i);
            TableRow tableRow = new TableRow(context);
            final TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(params);

            for (int j = 0; j < row.length; j++) {
                String cell = row[j];
                if (cell == null) {
                    cell = "";
                }
                HtmlTextView hv = new HtmlTextView(context);
                hv.setHtml(cell);
                hv.setTextSize(textView.getTextSize());
                hv.setTextColor(textView.getTextColors());
                TableRow.LayoutParams pcvParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                pcvParams.gravity = Gravity.CENTER;
                hv.setPadding(10, 10, 10, 10);
                hv.setLayoutParams(pcvParams);
                tableRow.addView(hv);
            }
            tableLayout.addView(tableRow);
        }
        scrollView.addView(tableLayout);
        return tableLayout;
    }

    private static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    public static class DrawItem {
        public int[] w;
        public int[] h;
        public int line;
        public int colum;

    }

    //=======================================将布局绘制成表格bitmap======================

    /**
     * 把view转换成bitmap
     *
     * @param view
     * @return
     */
    private static Bitmap view2Bitmap(View view) {
        DrawItem drawItem = new DrawItem();
        la2Me(view);
        View tmp = view;
        while (!(tmp instanceof TableLayout)) {
            tmp = ((ViewGroup) view).getChildAt(0);
        }
        getViewWH(tmp, drawItem);

        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        if (width == 0 || height == 0) {
            return null;
        }
        Bitmap bmp = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        view.draw(canvas);

        /**
         * 绘制表格的边框
         */
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#AA000000"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);

        //竖边线绘制
        int x = 0;
        for (int i = 0; i < drawItem.colum + 1; ++i) {
            if (i != 0) {
                x += drawItem.w[i - 1];
            }
            if (i == 0) {
                canvas.drawLine(x + 1, 0, x + 1, height, paint);
            } else if (i == drawItem.colum) {
                canvas.drawLine(x - 1, 0, x - 1, height, paint);
            } else {
                canvas.drawLine(x, 0, x, height, paint);
            }
        }
        //横边线绘制
        int y = 0;
        for (int i = 0; i < drawItem.line + 1; ++i) {
            if (i != 0) {
                y += drawItem.h[i - 1];
            }
            if (i == 0) {
                canvas.drawLine(0, y + 1, width, y + 1, paint);
            } else if (i == drawItem.line) {
                canvas.drawLine(0, y - 1, width, y - 1, paint);
            } else {
                canvas.drawLine(0, y, width, y, paint);
            }
        }

        return bmp;
    }

    /**
     * 计算出每一行每一列的最大行高和最大列宽
     * PS：该表格必须填充满，否则将会崩溃
     * PS：入参必须为TableLayout，并且其中子项只能为TableRow，否则将会崩溃
     * @param v
     * @param drawItem
     */
    private static void getViewWH(View v, DrawItem drawItem) {
        if (v instanceof TableLayout) {
            int line = ((TableLayout) v).getChildCount();
            drawItem.line = line;
            drawItem.h = new int[line];
            TableRow tr = (TableRow) ((TableLayout) v).getChildAt(0);
            int colum = tr.getChildCount();
            drawItem.colum = colum;
            drawItem.w = new int[colum];
            for (int i = 0; i < line; ++i) {
                TableRow trtmp = (TableRow) ((TableLayout) v).getChildAt(i);
                drawItem.h[i] = trtmp.getMeasuredHeight();
            }
            for (int j = 0; j < colum; ++j) {
                int width = 0;
                for (int i = 0; i < line; ++i) {
                    TableRow trtmp = (TableRow) ((TableLayout) v).getChildAt(i);
                    if (width < trtmp.getChildAt(j).getMeasuredWidth()) {
                        width = trtmp.getChildAt(j).getMeasuredWidth();
                    }
                }
                drawItem.w[j] = width;
            }
        }
    }

    /**
     * 手动对整个布局进行layout和measure
     * @param view
     */
    private static void la2Me(View view) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                view.layout(0, 0, lp.width, lp.height);
                view.measure(lp.width, lp.height);
                int childCount = ((ViewGroup) view).getChildCount();
                for (int i = 0; i < childCount; ++i) {
                    la2Me(((ViewGroup) view).getChildAt(i));
                }
                view.layout(0, 0, lp.width, lp.height);
                view.measure(lp.width, lp.height);
            } else {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                view.layout(0, 0, lp.width, lp.height);
                view.measure(lp.width, lp.height);
            }
        }
    }
}
