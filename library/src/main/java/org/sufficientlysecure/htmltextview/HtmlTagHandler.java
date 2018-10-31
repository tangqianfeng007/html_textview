/*
 * Copyright (C) 2013-2015 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2013-2015 Juha Kuitunen
 * Copyright (C) 2013 Mohammed Lakkadshaw
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sufficientlysecure.htmltextview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.BulletSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.widget.TextView;

import org.xml.sax.XMLReader;

import java.util.Stack;

/**
 * Some parts of this code are based on android.text.Html
 */
public class HtmlTagHandler implements Html.TagHandler {

    public static final String UNORDERED_LIST = "HTML_TEXTVIEW_ESCAPED_UL_TAG";
    public static final String ORDERED_LIST = "HTML_TEXTVIEW_ESCAPED_OL_TAG";
    public static final String LIST_ITEM = "HTML_TEXTVIEW_ESCAPED_LI_TAG";
    private Context mContext;
    private Html.ImageGetter mImageGetter;
    private final TextView mTextView;

    public HtmlTagHandler(Context context, Html.ImageGetter imageGetter, HtmlTextView tv) {
        mContext = context;
        mImageGetter = imageGetter;
        mTextView = tv;
    }

    /**
     * Newer versions of the Android SDK's {@link Html.TagHandler} handles &lt;ul&gt; and &lt;li&gt;
     * tags itself which means they never get delegated to this class. We want to handle the tags
     * ourselves so before passing the string html into Html.fromHtml(), we can use this method to
     * replace the &lt;ul&gt; and &lt;li&gt; tags with tags of our own.
     *
     * @param html String containing HTML, for example: "<b>Hello world!</b>"
     * @return html with replaced <ul> and <li> tags
     * @see <a href="https://github.com/android/platform_frameworks_base/commit/8b36c0bbd1503c61c111feac939193c47f812190">Specific Android SDK Commit</a>
     */
    String overrideTags(@Nullable String html) {

        if (html == null) {
            return null;
        }

//        html = html.replace("<ul", "<" + UNORDERED_LIST);
//        html = html.replace("</ul>", "</" + UNORDERED_LIST + ">");
//        html = html.replace("<ol", "<" + ORDERED_LIST);
//        html = html.replace("</ol>", "</" + ORDERED_LIST + ">");
//        html = html.replace("<li", "<" + LIST_ITEM);
//        html = html.replace("</li>", "</" + LIST_ITEM + ">");

        return html;
    }

    /**
     * Keeps track of lists (ol, ul). On bottom of Stack is the outermost list
     * and on top of Stack is the most nested list
     */
    Stack<String> lists = new Stack<>();
    /**
     * Tracks indexes of ordered lists so that after a nested list ends
     * we can continue with correct index of outer list
     */
    Stack<Integer> olNextIndex = new Stack<>();
    /**
     * List indentation in pixels. Nested lists use multiple of this.
     */
    /**
     * Running HTML table string based off of the root table tag. Root table tag being the tag which
     * isn't embedded within any other table tag. Example:
     * <!-- This is the root level opening table tag. This is where we keep track of tables. -->
     * <table>
     * ...
     * <table> <!-- Non-root table tags -->
     * ...
     * </table>
     * ...
     * </table>
     */
    /**
     * Tells us which level of table tag we're on; ultimately used to find the root table tag.
     */
    int tableTagLevel = 0;

    private static int userGivenIndent = -1;
    private static final int defaultIndent = 10;
    private static final int defaultListItemIndent = defaultIndent * 2;
    private static final BulletSpan defaultBullet = new BulletSpan(defaultIndent);

    private static class Ul {
    }

    private static class Ol {
    }

    private static class Code {
    }

    private static class Center {
    }

    private static class Strike {
    }

    private static class Table {
    }

    private static class Tr {
    }

    private static class Th {
    }

    private static class Td {

    }

    private static class Formula {

    }

    @Override
    public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {
        if (opening) {
            // opening tag
            if (HtmlTextView.DEBUG) {
                Log.d(HtmlTextView.TAG, "opening, output: " + output.toString());
            }

            if (tag.equalsIgnoreCase(UNORDERED_LIST)) {
                lists.push(tag);
            } else if (tag.equalsIgnoreCase(ORDERED_LIST)) {
                lists.push(tag);
                olNextIndex.push(1);
            } else if (tag.equalsIgnoreCase(LIST_ITEM)) {
                if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                    output.append("\n");
                }
                if (!lists.isEmpty()) {
                    String parentList = lists.peek();
                    if (parentList.equalsIgnoreCase(ORDERED_LIST)) {
                        start(output, new Ol());
                        olNextIndex.push(olNextIndex.pop() + 1);
                    } else if (parentList.equalsIgnoreCase(UNORDERED_LIST)) {
                        start(output, new Ul());
                    }
                }
            } else if (tag.equalsIgnoreCase("code")) {
                start(output, new Code());
            } else if (tag.equalsIgnoreCase("center")) {
                start(output, new Center());
            } else if (tag.equalsIgnoreCase("s") || tag.equalsIgnoreCase("strike")) {
                start(output, new Strike());
            } else if (tag.equalsIgnoreCase("table")) {
                start(output, new Table());
                tableTagLevel++;
            } else if (tag.equalsIgnoreCase("tr")) {
                output.append("<tr>");
            } else if (tag.equalsIgnoreCase("th")) {
                output.append("<th>");
            } else if (tag.equalsIgnoreCase("td")) {
                output.append("<td>");
            } else if (tag.equalsIgnoreCase("formula")) {
                if (tableTagLevel <= 0) {
                    start(output, new Formula());
                }
                else {
                    output.append("<formula>");
                }
            }
        } else {
            // closing tag
            if (HtmlTextView.DEBUG) {
                Log.d(HtmlTextView.TAG, "closing, output: " + output.toString());
            }

            if (tag.equalsIgnoreCase(UNORDERED_LIST)) {
                lists.pop();
            } else if (tag.equalsIgnoreCase(ORDERED_LIST)) {
                lists.pop();
                olNextIndex.pop();
            } else if (tag.equalsIgnoreCase(LIST_ITEM)) {
                if (!lists.isEmpty()) {
                    int listItemIndent = (userGivenIndent > -1) ? (userGivenIndent * 2) : defaultListItemIndent;
                    if (lists.peek().equalsIgnoreCase(UNORDERED_LIST)) {
                        if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }
                        // Nested BulletSpans increases distance between bullet and text, so we must prevent it.
                        int indent = (userGivenIndent > -1) ? userGivenIndent : defaultIndent;
                        BulletSpan bullet = (userGivenIndent > -1) ? new BulletSpan(userGivenIndent) : defaultBullet;
                        if (lists.size() > 1) {
                            indent = indent - bullet.getLeadingMargin(true);
                            if (lists.size() > 2) {
                                // This get's more complicated when we add a LeadingMarginSpan into the same line:
                                // we have also counter it's effect to BulletSpan
                                indent -= (lists.size() - 2) * listItemIndent;
                            }
                        }
                        BulletSpan newBullet = new BulletSpan(indent);
                        end(output, Ul.class, false,
                                new LeadingMarginSpan.Standard(listItemIndent * (lists.size() - 1)),
                                newBullet);
                    } else if (lists.peek().equalsIgnoreCase(ORDERED_LIST)) {
                        if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }

                        // Nested NumberSpans increases distance between number and text, so we must prevent it.
                        int indent = (userGivenIndent > -1) ? userGivenIndent : defaultIndent;
                        NumberSpan span = new NumberSpan(indent, olNextIndex.lastElement() - 1);
                        if (lists.size() > 1) {
                            indent = indent - span.getLeadingMargin(true);
                            if (lists.size() > 2) {
                                // As with BulletSpan, we need to compensate for the spacing after the number.
                                indent -= (lists.size() - 2) * listItemIndent;
                            }
                        }
                        NumberSpan numberSpan = new NumberSpan(indent, olNextIndex.lastElement() - 1);
                        end(output, Ol.class, false,
                                new LeadingMarginSpan.Standard(listItemIndent * (lists.size() - 1)),
                                numberSpan);
                    }
                }
            } else if (tag.equalsIgnoreCase("code")) {
                end(output, Code.class, false, new TypefaceSpan("monospace"));
            } else if (tag.equalsIgnoreCase("center")) {
                end(output, Center.class, true, new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER));
            } else if (tag.equalsIgnoreCase("s") || tag.equalsIgnoreCase("strike")) {
                end(output, Strike.class, false, new StrikethroughSpan());
            } else if (tag.equalsIgnoreCase("table")) {
                tableTagLevel--;
                endTable(output, Table.class);
            } else if (tag.equalsIgnoreCase("tr")) {
                output.append("</tr>");
            } else if (tag.equalsIgnoreCase("th")) {
                output.append("</th>");
            } else if (tag.equalsIgnoreCase("td")) {
                output.append("</td>");
            } else if (tag.equalsIgnoreCase("formula")) {
                if (tableTagLevel <= 0) {
                    endFormula(output, Formula.class);
                }
                else {
                    output.append("</formula>");
                }
            }
        }

//        storeTableTags(opening, tag);
    }


    /**
     * Mark the opening tag by using private classes
     */
    private void start(Editable output, Object mark) {
        int len = output.length();
        output.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);

        if (HtmlTextView.DEBUG) {
            Log.d(HtmlTextView.TAG, "len: " + len);
        }
    }

    /**
     * Modified from {@link android.text.Html}
     */
    private void end(Editable output, Class kind, boolean paragraphStyle, Object... replaces) {
        Object obj = getLast(output, kind);
        // start of the tag
        int where = output.getSpanStart(obj);
        // end of the tag
        int len = output.length();

        output.removeSpan(obj);

        if (where != len) {
            int thisLen = len;
            // paragraph styles like AlignmentSpan need to end with a new line!
            if (paragraphStyle) {
                output.append("\n");
                thisLen++;
            }
            for (Object replace : replaces) {
                output.setSpan(replace, where, thisLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (HtmlTextView.DEBUG) {
                Log.d(HtmlTextView.TAG, "where: " + where);
                Log.d(HtmlTextView.TAG, "thisLen: " + thisLen);
            }
        }
    }

    private void endFormula(Editable output, Class<Formula> kind) {
        Object obj = getLast(output, kind);
        // start of the tag
        int where = output.getSpanStart(obj);
        // end of the tag
        int len = output.length();

        String value = output.toString().substring(where);
        output.removeSpan(obj);

        if (where != len) {
            int thisLen = len;
            Drawable d = null;

            if (output != null) {
                d = mImageGetter.getDrawable(value);
            }

            if (d == null) {
                d = ContextCompat.getDrawable(mContext , R.drawable.empty);
                d.setBounds(0, 0, 40, 40);
            }

            output.setSpan(new ImageSpan(d, value), where, thisLen,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void endTable(Editable output, Class<Table> kind) {
        Object obj = getLast(output, kind);
        // start of the tag
        int where = output.getSpanStart(obj);
        // end of the tag
        int len = output.length();

        String value = "<table>"+ output.toString().substring(where) + "</table>";
        output.removeSpan(obj);

        if (where != len) {
            int thisLen = len;
            Drawable d = null;

            if (output != null) {
                d = TableConverter.convert(mContext , value , mTextView);
            }

            if (d == null) {
                d = ContextCompat.getDrawable(mContext , R.drawable.empty);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            }

            output.setSpan(new ImageSpan(d, value), where, thisLen,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }


    /**
     * Returns the text contained within a span and deletes it from the output string
     */
    private CharSequence extractSpanText(Editable output, Class kind) {
        final Object obj = getLast(output, kind);
        // start of the tag
        final int where = output.getSpanStart(obj);
        // end of the tag
        final int len = output.length();

        final CharSequence extractedSpanText = output.subSequence(where, len);
        output.delete(where, len);
        return extractedSpanText;
    }

    /**
     * Get last marked position of a specific tag kind (private class)
     */
    private static Object getLast(Editable text, Class kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            for (int i = objs.length; i > 0; i--) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1];
                }
            }
            return null;
        }
    }

    // Util method for setting pixels.
    public void setListIndentPx(float px) {
        userGivenIndent = Math.round(px);
    }
} 
