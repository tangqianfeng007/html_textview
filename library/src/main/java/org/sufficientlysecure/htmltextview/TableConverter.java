package org.sufficientlysecure.htmltextview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
        Bitmap b = View2BitmapUtils.view2Bitmap(view);
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
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setDividerDrawable(context.getResources().getDrawable(R.drawable.shape_horizontal));
        tableLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_END | LinearLayout.SHOW_DIVIDER_MIDDLE);

        for (int i = 0; i < whole.size(); i++) {
            String[] row = whole.get(i);
            TableRow tableRow = new MatchTableRow(context);
            final TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(params);
            tableRow.setDividerDrawable(context.getResources().getDrawable(R.drawable.shape_vertical));
            tableRow.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_END | LinearLayout.SHOW_DIVIDER_MIDDLE);

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
        return tableLayout;
    }

    private static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }
}
