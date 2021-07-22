package com.lihao.market.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.R;

public class BalanceActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private TextView balanceTv;

    private TextView pointTv;

    private RelativeLayout balanceLayout;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_balance;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        balanceTv = findViewById(R.id.balance);
        pointTv = findViewById(R.id.point);
        balanceLayout = findViewById(R.id.account_layout);
    }

    @Override
    public void initData()
    {
        titleTv.setText("我的余额");
        Intent intent = getIntent();
        if (intent != null)
        {
            String balance = intent.getStringExtra("balance");
            String point = intent.getStringExtra("point");
            balanceTv.setText(balance);
            pointTv.setText(point);
        }
    }

    @Override
    public void initListener()
    {
        balanceLayout.setOnClickListener(this);
        backTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.account_layout:
                Intent intent = new Intent(BalanceActivity.this, AccountDetailActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
