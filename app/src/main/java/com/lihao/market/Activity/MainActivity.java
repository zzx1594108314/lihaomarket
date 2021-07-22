package com.lihao.market.Activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Dialog.CustomDialog;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Service.Downloadservice;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * page页
 */
public class MainActivity extends BaseActivity
{
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_main;
    }

    @Override
    public void initView()
    {

    }

    @Override
    public void initData()
    {
        timer = new CountDownTimer(2000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {

            }

            @Override
            public void onFinish()
            {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            }
        };
        timer.start();
    }

    @Override
    public void initListener()
    {

    }

}
