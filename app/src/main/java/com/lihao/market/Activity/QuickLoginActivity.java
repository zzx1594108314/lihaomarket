package com.lihao.market.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;
import com.wby.utility.ToastUtil;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

/**
 * 手机号快捷登录
 */

public class QuickLoginActivity extends BaseActivity implements View.OnClickListener
{
    private TextView titleTv;

    private TextView backTv;

    private EditText inputNumber;

    private EditText inputCode;

    private TextView codeTv;

    private TextView confirmTv;

    private CountDownTimer timer;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_quick_login;
    }

    @Override
    public void initView()
    {
        titleTv = findViewById(R.id.tv_title);
        backTv = findViewById(R.id.tv_back);
        inputNumber = findViewById(R.id.input);
        inputCode = findViewById(R.id.edt_pwd);
        codeTv = findViewById(R.id.code);
        confirmTv = findViewById(R.id.login);

        timer = new TimeCount(60000, 1000);
    }

    @Override
    public void initData()
    {
        titleTv.setText("手机号快捷登录");
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        confirmTv.setOnClickListener(this);
        codeTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.login:
                login();
                break;
            case R.id.code:
                timer.start();
                getCode();
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCode()
    {
        if (TextUtils.isEmpty(inputNumber.getText()))
        {
            ToastUtils.s(QuickLoginActivity.this, "请输入手机号");
            return;
        }

        AjaxParams params = new AjaxParams();
        try {
            params.put("mobile", inputNumber.getText().toString());
            params.put("flag", "");
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=sms&a=getsend", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String message = object.getString("msg");
                        ToastUtils.s(getApplicationContext(), message);

                    } catch (Exception e)
                    {
                        Log.e("getCode", e.toString());
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

    private void login()
    {
        if (TextUtils.isEmpty(inputNumber.getText()))
        {
            ToastUtils.s(QuickLoginActivity.this, "请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(inputCode.getText()))
        {
            ToastUtils.s(QuickLoginActivity.this, "请输入验证码");
            return;
        }

        try {
            AjaxParams params = new AjaxParams();
            params.put("mobile", inputNumber.getText().toString());
            params.put("mobile_code", inputCode.getText().toString());
            params.put("enabled_sms", "1");
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login&a=getmobile_quick", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String message = object.getString("info");
                        String errorCode = object.getString("status");
                        ToastUtils.s(getApplicationContext(), message);
                        if ("y".equals(errorCode))
                        {
                            LoginUtil.setLogin(true);
                            SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.ACCOUNT, inputNumber.getText().toString());
                            SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.PASSWORD, inputCode.getText().toString());
                            Intent result = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString(KeySet.QUICK_LOGIN, errorCode);
                            result.putExtras(bundle);
                            setResult(1, result);
                            finish();
                        }
                    } catch (Exception e)
                    {
                        Log.e("login", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg)
                {
                    super.onFailure(t, strMsg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class TimeCount extends CountDownTimer
    {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            codeTv.setText("重新获取");
            codeTv.setClickable(true);
            codeTv.setPressed(false);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            codeTv.setClickable(false);
            codeTv.setPressed(true);
            codeTv.setText(millisUntilFinished / 1000 + "秒后重新获取");
        }
    }
}
