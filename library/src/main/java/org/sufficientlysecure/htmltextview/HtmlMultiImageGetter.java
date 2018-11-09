package org.sufficientlysecure.htmltextview;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

/**
 * Copyright (C) 2018, PING AN TECHNOLOGIES CO., LTD.
 * HtmlMultiImageGetter
 * <p>
 * Description
 *
 * @author tangqianfeng567
 * @version 1.0
 * <p>
 * Ver 1.0, 2018/10/25, tangqianfeng567, Create file
 */
public class HtmlMultiImageGetter implements Html.ImageGetter {

    public static final String PREFIX_HTTP = "http";
    public static final String PREFIX_FORMULA = "$";

    TextView container;
    HtmlHttpImageGetter htmlHttpImageGetter;
    HtmlFormulaImageGetter htmlFormulaImageGetter;
    HtmlResImageGetter htmlResImageGetter;

    public HtmlMultiImageGetter(TextView textView) {
        this.container = textView;
    }

    @Override
    public Drawable getDrawable(String source) {
        source = source.replaceAll("\\\\*\"" , "");
        if (source.startsWith(PREFIX_HTTP)) {
            if (htmlHttpImageGetter == null) {
                htmlHttpImageGetter = new HtmlHttpImageGetter(container);
            }
            return htmlHttpImageGetter.getDrawable(source);
        }
        else if (source.startsWith(PREFIX_FORMULA)) {
            if (htmlFormulaImageGetter == null) {
                htmlFormulaImageGetter = new HtmlFormulaImageGetter(container);
            }
            return htmlFormulaImageGetter.getDrawable(source);
        }
        else {
            if (htmlResImageGetter == null) {
                htmlResImageGetter = new HtmlResImageGetter(container);
            }
            return htmlResImageGetter.getDrawable(source);
        }
    }
}
