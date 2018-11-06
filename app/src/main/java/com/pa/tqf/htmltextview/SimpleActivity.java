package com.pa.tqf.htmltextview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.sufficientlysecure.htmltextview.HtmlTextView;
import org.sufficientlysecure.htmltextview.HtmlUtils;

import java.io.InputStream;
import java.util.Scanner;

public class SimpleActivity extends AppCompatActivity {

    HtmlTextView tv;
    HtmlTextView tv1;
    HtmlTextView tv2;
    HtmlTextView tv3;
    HtmlTextView tv4;
    HtmlTextView tv5;
    HtmlTextView tv6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        tv = findViewById(R.id.tv);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);

        tv1.setHtml(HtmlUtils.parseHtmlData(ExampleFormula.mExample1));
        tv2.setHtml(HtmlUtils.parseHtmlData(ExampleFormula.mExample2));
//        tv3.setHtml(HtmlUtils.parseHtmlData(ExampleFormula.mExample3));
        tv4.setHtml(HtmlUtils.parseHtmlData(ExampleFormula.mExample4));
        tv5.setHtml(HtmlUtils.parseHtmlData(ExampleFormula.mExample5));
        tv6.setHtml(HtmlUtils.parseHtmlData(ExampleFormula.mExample6));

        String a = convertStreamToString(getResources().openRawResource(R.raw.example));
        tv.setHtml(HtmlUtils.parseHtmlData(a));
    }

    private String convertStreamToString(@NonNull InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
