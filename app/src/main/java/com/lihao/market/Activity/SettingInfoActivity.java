package com.lihao.market.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Custom.CircleImageView;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Service.LogoSuspendService;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;


public class SettingInfoActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private RelativeLayout addressLayout;

    private RelativeLayout nickNameLayout;

    private TextView nickTv;

    private TextView headNameTv;

    private TextView userNameTv;

    private RelativeLayout headViewLayout;

    private TextView logoutTv;

    private CircleImageView circleIv;

    private RelativeLayout accountSafe;

    private RelativeLayout kefuLayout;

    private Intent service;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_setting_info;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        addressLayout = findViewById(R.id.address_layout);
        nickNameLayout = findViewById(R.id.nickname_layout);
        nickTv = findViewById(R.id.nick_name);
        userNameTv = findViewById(R.id.username);
        headNameTv = findViewById(R.id.nick_name_head);
        headViewLayout = findViewById(R.id.head_view_layout);
        circleIv = findViewById(R.id.head_image);
        logoutTv = findViewById(R.id.logout);
        accountSafe = findViewById(R.id.account_safe);
        kefuLayout = findViewById(R.id.kefu_layout);
    }

    @Override
    public void initData()
    {
        titleTv.setText("个人中心");
        String userName = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ACCOUNT);
        userNameTv.setText(userName);
        Intent intent = getIntent();
        if (intent != null)
        {
            String name = intent.getStringExtra("name");
            String sex = intent.getStringExtra("sex");
            String url = intent.getStringExtra("url");
            nickTv.setText(name);
            headNameTv.setText(name);
            Glide.with(this).load(url)
                    .apply(new RequestOptions().centerCrop().fallback(R.mipmap.user_default).error(R.mipmap.user_default)).into(circleIv);
        }

    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        addressLayout.setOnClickListener(this);
        nickNameLayout.setOnClickListener(this);
        headViewLayout.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        accountSafe.setOnClickListener(this);
        kefuLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                setResult(2);
                finish();
                break;
            case R.id.address_layout:
                Intent address = new Intent(SettingInfoActivity.this, AddressListActivity.class);
                startActivity(address);
                break;
            case R.id.nickname_layout:
                Intent nickName = new Intent(SettingInfoActivity.this, ChangeNameActivity.class);
                nickName.putExtra("name", nickTv.getText());
                startActivityForResult(nickName, 1);
                break;
            case R.id.head_view_layout:
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.account_safe:
                Intent account = new Intent(SettingInfoActivity.this, AccountSafeActivity.class);
                startActivity(account);
                break;
            case R.id.kefu_layout:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
                {
                    //没有权限，需要申请权限，因为是打开一个授权页面，所以拿不到返回状态的，所以建议是在onResume方法中从新执行一次校验
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                }
                else
                {
                    //已经有权限，可以直接显示悬浮窗
                    service = new Intent(this, LogoSuspendService.class);
                    startService(service);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 && data != null)
        {
            Bundle bundle = data.getExtras();
            if (bundle != null)
            {
                nickTv.setText(bundle.getString("name"));
                headNameTv.setText(bundle.getString("name"));
            }
        }
        if (requestCode == 100)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (Settings.canDrawOverlays(this))
                {
                    service = new Intent(this, LogoSuspendService.class);
                    startService(service);
                }
            }
        }
    }

    private void logout()
    {
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=user&c=login&a=logout", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        SharedPreferencesUtils.clearValue(SharedPreferencesUtils.ACCOUNT);
                        SharedPreferencesUtils.clearValue(SharedPreferencesUtils.PASSWORD);
                        SharedPreferencesUtils.clearValue(SharedPreferencesUtils.RECENT_SEARCH);
                        SharedPreferencesUtils.clearValue(SharedPreferencesUtils.WX_USER_NAME);
                        LoginUtil.setLogin(false);
                        Intent result = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("logout", "y");
                        result.putExtras(bundle);
                        setResult(1, result);
                        finish();
                    } catch (Exception e)
                    {
                        Log.e("logout", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
