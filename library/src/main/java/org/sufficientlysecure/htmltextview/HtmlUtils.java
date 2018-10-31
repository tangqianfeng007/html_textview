package org.sufficientlysecure.htmltextview;

/**
 * Copyright (C) 2018, PING AN TECHNOLOGIES CO., LTD.
 * HtmlUtils
 * <p>
 * Description
 *
 * @author tangqianfeng567
 * @version 1.0
 * <p>
 * Ver 1.0, 2018/10/29, tangqianfeng567, Create file
 */
public class HtmlUtils {

    private static final String fumale_s = "<formula>";
    private static final String fumale_e = "</formula>";

    /**
     * 把html数据里的公式符号转换成标签
     * $value$ --->  <fumale>value</fumale>
     *
     * @param data
     * @return
     */
    public static String parseHtmlData(String data) {
        StringBuilder sb = new StringBuilder(data);
        int strIndex = 0;
        boolean isStart$ = true;
        boolean isStart$$ = true;
        do {
            int tmp$ = sb.indexOf("$", strIndex);
            int tmp$$ = sb.indexOf("$$", strIndex);
            strIndex = tmp$;
            if (strIndex > -1) {
                if (tmp$ == tmp$$) {
                    if (!isStart$) {
                        sb.insert(tmp$ + 1, fumale_e);
                        isStart$ = !isStart$;
                        strIndex += (fumale_e.length() + 1);
                    } else {
                        if (isStart$$) {
                            sb.insert(tmp$$, fumale_s);
                            strIndex += (fumale_s.length() + 2);
                        } else {
                            sb.insert(tmp$$ + 2, fumale_e);
                            strIndex += (fumale_e.length() + 2);
                        }
                        isStart$$ = !isStart$$;
                    }
                } else {
                    if (isStart$) {
                        sb.insert(tmp$, fumale_s);
                        strIndex += (fumale_s.length() + 1);
                    } else {
                        sb.insert(tmp$ + 1, fumale_e);
                        strIndex += (fumale_e.length() + 1);
                    }
                    isStart$ = !isStart$;
                }
            }
        } while (strIndex > -1);

        return "<p>".concat(sb.toString()).concat("</p>");
    }
}
