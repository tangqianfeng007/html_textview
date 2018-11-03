package org.sufficientlysecure.htmltextview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextPaint;
import android.widget.TextView;

import org.scilab.forge.jlatexmath.core.AjLatexMath;
import org.scilab.forge.jlatexmath.core.Insets;
import org.scilab.forge.jlatexmath.core.TeXConstants;
import org.scilab.forge.jlatexmath.core.TeXFormula;
import org.scilab.forge.jlatexmath.core.TeXIcon;

import static org.scilab.forge.jlatexmath.core.TeXFormula.getPartialTeXFormula;

/**
 * Copyright (C) 2018, PING AN TECHNOLOGIES CO., LTD.
 * HtmlFormulaImageGetter
 * <p>
 * Description
 *
 * @author tangqianfeng567
 * @version 1.0
 * <p>
 * Ver 1.0, 2018/10/25, tangqianfeng567, Create file
 */
public class HtmlFormulaImageGetter implements Html.ImageGetter {

    TextPaint textPaint;

    public HtmlFormulaImageGetter(TextView textView) {
        textPaint = textView.getPaint();
    }

    public HtmlFormulaImageGetter(TextPaint textPaint) {
        this.textPaint = textPaint;
    }

    @Override
    public Drawable getDrawable(String source) {
        source = clearSymbol(source);
        TeXFormula teXFormula = getPartialTeXFormula(source);
        Bitmap bitmap = loadBitmap(teXFormula);
        if (bitmap == null) {
            return null;
        }
        Drawable drawable = bitmap2Drawable(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    private String clearSymbol(String source) {
        return source.replaceAll("\\\\+" , "\\\\");
    }


    protected Bitmap loadBitmap(TeXFormula teXFormula) {
        TeXIcon icon = teXFormula.new TeXIconBuilder()
                .setStyle(TeXConstants.STYLE_DISPLAY)
                .setSize((float) (textPaint.getTextSize() / textPaint.density * 1.1))
                .setWidth(TeXConstants.UNIT_SP, textPaint.getTextSize() / textPaint.density , TeXConstants.ALIGN_LEFT)
                .setIsMaxWidth(true)
                .setInterLineSpacing(TeXConstants.UNIT_SP,
                        AjLatexMath.getLeading(textPaint.getTextSize() / textPaint.density))
                .build();

        if (icon == null || icon.getIconHeight() > 1080 || icon.getIconWidth() > 1980) {
            return null;
        }

        icon.setInsets(new Insets(5, 2, 5, 2));

        Bitmap image = Bitmap.createBitmap(icon.getIconWidth(), icon.getIconHeight(),
                Bitmap.Config.ARGB_8888);

        if (image == null) {
            return null;
        }

        Canvas g2 = new Canvas(image);
        g2.drawColor(Color.TRANSPARENT);
        icon.paintIcon(g2, 0, 0);
        return image;
    }

    Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }
}
