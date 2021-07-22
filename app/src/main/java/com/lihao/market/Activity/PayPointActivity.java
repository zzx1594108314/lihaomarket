package com.lihao.market.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.R;

public class PayPointActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private TextView orderSn;

    private TextView confirmTv;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_pay_point;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        orderSn = findViewById(R.id.order_sn);
        confirmTv = findViewById(R.id.confirm);
    }

    @Override
    public void initData()
    {
        titleTv.setText("下单成功");
        Intent intent = getIntent();
        if (intent != null)
        {
            String order = intent.getStringExtra("order_sn");
            orderSn.setText(order);
        }
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        confirmTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
            case R.id.confirm:
                setResult(2);
                finish();
                break;
            default:
                break;
        }
    }
}
