package com.lihao.market.Base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;

/**
 * 基类
 */
public abstract class BaseActivity extends AppCompatActivity
{
    private MyReceiver receiver;

    public ProgressDialog progressDialg = null;

    public ProgressDialog progressDialogwating = null;

    public ProgressDialog Dialg = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (ImmersionBar.hasNavigationBar(this))
        {
            getWindow().getDecorView().setPadding(0, 0, 0, ImmersionBar.getNavigationBarHeight(this));
        }
        ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).transparentBar().fitsSystemWindows(true).init();
        setContentView(getLayoutId());
        initView();
        initListener();
        initData();

        registerBroadcast();
    }

    /**
     * 给当前activity给予布局文件
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化
     */
    public abstract void initView();

    /**
     * 获取数据
     */
    public abstract void initData();

    /**
     * 设置监听
     */
    public abstract void initListener();

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void registerBroadcast()
    {
        // 注册广播接收者
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("exit_app");
        registerReceiver(receiver,filter);
    }

    private class MyReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(("exit_app").equals(intent.getAction()))
            {
                finish();
            }
        }
    }

    /**
     * 显示加载Dialog
     */
    protected void showLoadingDialog() {
        if (progressDialg == null) {
            progressDialg = new ProgressDialog(BaseActivity.this);
            progressDialg.setCancelable(false);
            progressDialg.setMessage("加载中...");
            //progressDialg.set
        }
        progressDialg.show();

    }

    /**
     * 隐藏加载Dialog
     */
    protected void hideLoadingDialog()
    {
        if (progressDialg != null) {
            progressDialg.dismiss();
        }
    }

    //显示等待进度条
    protected void showWaitingCmdDialog()
    {
        if (progressDialogwating == null) {
            progressDialogwating = new ProgressDialog(BaseActivity.this);
            progressDialogwating.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialogwating.setCancelable(false);
            progressDialogwating.setIndeterminate(false);
            progressDialogwating.setMessage("等待设备响应...");

        }
        progressDialogwating.show();

    }

    //隐藏等待进度条
    protected void hideWaitingDialog()
    {
        if (progressDialogwating != null) {
            progressDialogwating.dismiss();
        }
    }

    /**
     * 显示加载Dialog
     */
    protected void showStringLoadingDialog(String s)
    {
        if (Dialg == null) {
            Dialg = new ProgressDialog(BaseActivity.this);
            Dialg.setCancelable(false);
            Dialg.setMessage(s);
        }
        Dialg.show();

    }

    /**
     * 隐藏加载Dialog
     */
    protected void hideStringLoadingDialog()
    {
        if (Dialg != null) {
            Dialg.dismiss();
        }
    }
}
