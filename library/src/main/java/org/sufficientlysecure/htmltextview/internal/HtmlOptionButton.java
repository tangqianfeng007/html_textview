package org.sufficientlysecure.htmltextview.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTextView;
import org.sufficientlysecure.htmltextview.HtmlUtils;
import org.sufficientlysecure.htmltextview.R;

/**
 * Copyright (C) 2018, PING AN TECHNOLOGIES CO., LTD.
 * HtmlOptionButton
 * <p>
 * Description
 * 选择题选项按钮
 *
 * @author tangqianfeng567
 * @version 1.0
 * <p>
 * Ver 1.0, 2018/11/2, tangqianfeng567, Create file
 */
public class HtmlOptionButton extends FrameLayout {

    private State state;
    private String head;
    private String content;

    LinearLayout containerLl;
    private TextView headTv;
    private View line;
    private HtmlTextView contentHtv;

    public HtmlOptionButton(Context context) {
        this(context, null);
    }

    public HtmlOptionButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HtmlOptionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obainAttrs(context , attrs);
        initView();
    }

    private void obainAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.option_button);
            int type = mTypedArray.getInt(R.styleable.option_button_state , 0);
            if (type == State.NONE.value) {
                state = State.NONE;
            }
            else if (type == State.GREEN.value) {
                state = State.GREEN;
            }
            else if (type == State.YELLOW.value) {
                state = State.YELLOW;
            }
            else if (type == State.RED.value) {
                state = State.RED;
            }
            head = mTypedArray.getString(R.styleable.option_button_head);
            content = mTypedArray.getString(R.styleable.option_button_content);
            mTypedArray.recycle();
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.html_option_btn, this, true);
        containerLl = findViewById(R.id.option_container);
        headTv = findViewById(R.id.option_head);
        line = findViewById(R.id.option_line);
        contentHtv = findViewById(R.id.option_content);
        setState(state);
        setHead(head);
        setContent(content);
    }

    public HtmlOptionButton setState(State state) {
        this.state = state;
        switch (state) {
            case RED:
                containerLl.setBackgroundResource(R.drawable.html_option_red_bg);
                setTheme(ContextCompat.getColor(getContext() , R.color.html_option_red_color));
                break;
            case GREEN:
                containerLl.setBackgroundResource(R.drawable.html_option_green_bg);
                setTheme(ContextCompat.getColor(getContext() , R.color.html_option_green_color));
                break;
            case YELLOW:
                containerLl.setBackgroundResource(R.drawable.html_option_yellow_bg);
                setTheme(ContextCompat.getColor(getContext() , R.color.html_option_yellow_color));
                break;
            default:
            case NONE:
                containerLl.setBackgroundResource(R.drawable.html_option_default_bg);
                setTheme(ContextCompat.getColor(getContext() , R.color.html_option_default_color));
                break;

        }
        return this;
    }

    public void setTheme(int color) {
        headTv.setTextColor(color);
        line.setBackgroundColor(color);
        contentHtv.setTextColor(color);
    }

    public HtmlOptionButton setHead(String head) {
        this.head = head;
        headTv.setText(head);
        return this;
    }

    public HtmlOptionButton setContent(String html) {
        this.content = html;
        contentHtv.setHtml(HtmlUtils.parseHtmlData(html));
        return this;
    }

    public State getState() {
        return state;
    }

    public String getHead() {
        return head;
    }

    public String getContent() {
        return content;
    }

    public enum State {
        /**
         * 绿框
         */
        GREEN(1),
        /**
         * 黄框
         */
        YELLOW(2),
        /**
         * 红框
         */
        RED(3),
        /**
         * 默认灰
         */
        NONE(0);

        private int value;

        State(int i) {
            value = i;
        }
    }

}
