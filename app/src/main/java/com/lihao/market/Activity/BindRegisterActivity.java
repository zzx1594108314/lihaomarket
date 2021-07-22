package com.lihao.market.Activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Custom.CircleImageView;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;
import com.lihao.market.wxapi.WXEntryActivity;
import com.wby.utility.ToastUtil;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

public class BindRegisterActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private CircleImageView imageView;

    private TextView nameTv;

    private EditText phoneEt;

    private EditText codeEt;

    private TextView codeTv;

    private TextView sureTv;

    private String ident;

    private TimeCount timer;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_bind_register;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        imageView = findViewById(R.id.image);
        nameTv = findViewById(R.id.name);
        phoneEt = findViewById(R.id.phone);
        codeEt = findViewById(R.id.code_ed);
        sureTv = findViewById(R.id.sure);
        codeTv = findViewById(R.id.code);

        timer = new TimeCount(60000, 1000);
    }

    @Override
    public void initData()
    {
        titleTv.setText("绑定注册");

        Intent intent = getIntent();
        if (intent != null)
        {
            String name = intent.getStringExtra("name");
            String headUrl = intent.getStringExtra("headUrl");
            ident = intent.getStringExtra("ident");
            nameTv.setText(name);
            Glide.with(this).load(headUrl)
                    .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_middle_common).error(R.mipmap.ic_middle_common)).into(imageView);
        }
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        sureTv.setOnClickListener(this);
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
            case R.id.sure:
                bindRegister();
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
        if (TextUtils.isEmpty(phoneEt.getText()))
        {
            ToastUtils.s(BindRegisterActivity.this, "请输入手机号");
            return;
        }

        AjaxParams params = new AjaxParams();
        try {
            params.put("mobile", phoneEt.getText().toString());
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

    private void bindRegister()
    {
        if (TextUtils.isEmpty(phoneEt.getText()))
        {
            ToastUtils.s(BindRegisterActivity.this, "请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(codeEt.getText()))
        {
            ToastUtils.s(BindRegisterActivity.this, "请输入验证码");
            return;
        }

        AjaxParams params = new AjaxParams();
        try {
            params.put("mobile", phoneEt.getText().toString());
            params.put("mobile_code", codeEt.getText().toString());
            params.put("ident", ident);
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login&a=getbindregister", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String status = object.getString("status");
                        String info = object.getString("info");
                        if ("y".equals(status))
                        {
//                            Intent intent = new Intent("com.broadcast.bindwx");
//                            intent.putExtra("msg", 2);
//                            BroadCastManager.getInstance().sendBroadCast(BindRegisterActivity.this, intent);

                            SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.ACCOUNT, phoneEt.getText().toString());
                            SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.PASSWORD, codeEt.getText().toString());

                            Intent person = new Intent(BindRegisterActivity.this, HomeActivity.class);
                            person.putExtra("fragment_flag", 2);
                            LoginUtil.setLogin(true);
                            startActivity(person);

                        }
                        else
                        {
                            ToastUtils.s(BindRegisterActivity.this, info);
                        }

                    } catch (Exception e)
                    {
                        Log.e("bindRegister", e.toString());
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

    class TimeCount extends CountDownTimer
    {
        private TimeCount(long millisInFuture, long countDownInterval) {
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
