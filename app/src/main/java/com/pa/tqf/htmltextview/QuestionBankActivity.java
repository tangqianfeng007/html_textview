package com.pa.tqf.htmltextview;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.readystatesoftware.chuck.Chuck;
import com.readystatesoftware.chuck.ChuckInterceptor;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.sufficientlysecure.htmltextview.HtmlTextView;
import org.sufficientlysecure.htmltextview.HtmlUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuestionBankActivity extends AppCompatActivity {

    /**
     * http://k12-portal-stg1.zhi-niao.com/h5portal/onePortal/index.html#/login获取cookie 然后替换
     */
    public static final String cookie = "JSESSIONID=c7d06e4a-a401-4c5b-81e9-2373cce2d7a0";
    public static final String url = "http://k12-portal-stg1.zhi-niao.com/homework/web/common/list/autonomous?period=2&chapterIdList=8424763&pageSize=10&source=A&subjectId=2&knowledgeIdList=33685507,33685508,33685509,33685510,33685511,33685512,33685513,33685514,33685515,33685516,33685517,33685518,33685519,33685520,33685521,33685522,33685525,33685526,33685527,33685528,33685529,33685530,33685531,33685547,33685548,33685549,33685550,33685551,33685553,33685554,33685555,33685556,33685558,33685574,33685618,33685619,33685620,33685621,33685622,33685623,33685624,33685625,33685626,33685628,33685629,33685630,33685631,33685632,33685633,33685634,33685635,33685636,33685637,33685647,33685778,33685779,33685780,33685783,33685784,33685785,33685786,33685787,33685788,33685789,33685790,33685791,33685792,33685793,33685794,33685796,33685799,33685800,33685854,33685855,33685883,33685980,33685981,33685985,33685986,33685989,33685990,33685991,33685992,33685993,33685994,33685995,33685996,33685997,33685999,33686000,33686001,33686002,33686003&questionType=0&pageNo=";
    int page = 1;
    OkHttpClient okHttpClient;

    SmartRefreshLayout srf;
    RecyclerView rcl;
    EditText pageEt;
    Button jump;
    Button log;
    HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_bank);
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(getApplicationContext()))
                .build();
        srf = findViewById(R.id.refresh);
        rcl = findViewById(R.id.rcl);
        pageEt = findViewById(R.id.page);
        jump = findViewById(R.id.jump);
        log = findViewById(R.id.log);
        initView();
        refresh(0);
    }

    private void initView() {
        rcl.setLayoutManager(new LinearLayoutManager(this));
        srf.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                loadMore();
            }
        });

        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refresh(page);
            }
        });
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pageStr = pageEt.getText().toString().trim();
                if (TextUtils.isEmpty(pageStr)) {
                    page = 0;
                }
                else {
                    page = Integer.parseInt(pageStr);
                }
                refresh(page);
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Chuck.getLaunchIntent(QuestionBankActivity.this));
            }
        });
    }

    private void refresh(int page) {
        String u = url+page;
        Request request = new Request.Builder()
                .url(u)
                .addHeader("Cookie" , cookie)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                srf.finishRefresh();
                Toast.makeText(QuestionBankActivity.this , e.getMessage() , Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                srf.finishRefresh();
                if (response.isSuccessful()) {
                    QuestionBean questionBean = JSON.parseObject(response.body().string() , QuestionBean.class);
                    if ("200".equalsIgnoreCase(questionBean.getCode())) {
                        refreshUI(questionBean.getBody().getList());
                    }
                }
            }
        });
    }

    private void refreshUI(List<QuestionBean.BodyBean.ListBean> list) {
        adapter = new HomeAdapter(R.layout.question_item  , list);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                rcl.setAdapter(adapter);
            }
        });
    }

    private void loadMore() {
        page ++;
        String u = url+page;
        Request request = new Request.Builder()
                .url(u)
                .addHeader("Cookie" , cookie)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                srf.finishLoadMore();
                Toast.makeText(QuestionBankActivity.this , e.getMessage() , Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                srf.finishLoadMore();
                if (response.isSuccessful()) {
                    QuestionBean questionBean = JSON.parseObject(response.body().string() , QuestionBean.class);
                    if ("200".equalsIgnoreCase(questionBean.getCode())) {
                        loadMoreUI(questionBean.getBody().getList());
                    }
                }
            }
        });
    }

    private void loadMoreUI(List<QuestionBean.BodyBean.ListBean> list) {
        if (adapter != null) {
            adapter.getData().addAll(list);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public class HomeAdapter extends BaseQuickAdapter<QuestionBean.BodyBean.ListBean, BaseViewHolder> {

        public HomeAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, QuestionBean.BodyBean.ListBean item) {
            HtmlTextView h = helper.getView(R.id.hlv);
            h.setHtml(HtmlUtils.parseHtmlData(item.getContent()));
        }
    }

}
