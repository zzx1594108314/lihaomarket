package com.lihao.market.Activity;

import android.view.View;
import android.widget.TextView;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.R;

public class AboutActivity extends BaseActivity
{
    private TextView backTv;

    private TextView titleTv;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_about;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
    }

    @Override
    public void initData()
    {
        titleTv.setText("关于");
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
