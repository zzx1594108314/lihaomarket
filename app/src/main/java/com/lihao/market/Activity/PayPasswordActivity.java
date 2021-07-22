package com.lihao.market.Activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

public class PayPasswordActivity extends BaseActivity implements View.OnClickListener
{
    private TextView titleTv;

    private TextView backTv;

    private EditText inputNumber;

    private EditText inputCode;

    private TextView codeTv;

    private TextView confirmTv;

    private CountDownTimer timer;

    private TextView phoneTv;

    private RelativeLayout oldLayout;

    private EditText oldEt;

    private String phone;

    private String isEdit = "0";

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_pay_password;
    }

    @Override
    public void initView()
    {
        titleTv = findViewById(R.id.tv_title);
        backTv = findViewById(R.id.tv_back);
        inputNumber = findViewById(R.id.input);
        inputCode = findViewById(R.id.edt_pwd);
        codeTv = findViewById(R.id.code);
        confirmTv = findViewById(R.id.next);
        phoneTv = findViewById(R.id.phone);
        oldLayout = findViewById(R.id.old_layout);
        oldEt = findViewById(R.id.old_pwd);

        timer = new TimeCount(60000, 1000);
    }

    @Override
    public void initData()
    {
        titleTv.setText("设置支付密码");
        Intent intent = getIntent();
        if (intent != null)
        {
            phone = intent.getStringExtra("account");
            phoneTv.setText(phone);
            isEdit = intent.getStringExtra("isEdit");
            if (!"0".equals(isEdit))
            {
                oldLayout.setVisibility(View.VISIBLE);
                titleTv.setText("修改支付密码");
            }
            else
            {
                titleTv.setText("设置支付密码");
            }
        }
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
            case R.id.next:
                next();
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

        AjaxParams params = new AjaxParams();
        try {
            params.put("mobile", phone);
            params.put("flag", "");
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=sms&a=getsend", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String message = object.getString("content");
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

    private void next()
    {
        if (TextUtils.isEmpty(inputNumber.getText()))
        {
            ToastUtils.s(PayPasswordActivity.this, "请输入验证码");
            return;
        }

        if (TextUtils.isEmpty(inputCode.getText()))
        {
            ToastUtils.s(PayPasswordActivity.this, "请输入支付密码");
            return;
        }

        if (!"0".equals(isEdit))
        {
            if (TextUtils.isEmpty(oldEt.getText()))
            {
                ToastUtils.s(PayPasswordActivity.this, "请输入旧密码");
                return;
            }
        }

        try {
            AjaxParams params = new AjaxParams();
            params.put("sms_code", inputNumber.getText().toString());
            params.put("pay_paypwd", inputCode.getText().toString());
            params.put("type", "phone");
            params.put("sms_signin", "1");
            params.put("is_edit", isEdit);
            params.put("old_pay_paypwd", oldEt.getText().toString());
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=profile&a=getuser_edit_paypwd", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        String result = object.getString("content");
                        ToastUtils.s(getApplicationContext(), result);
                        if ("0".equals(error))
                        {
                            setResult(1);
                            finish();
                        }
                    } catch (Exception e)
                    {
                        Log.e("next", e.toString());
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
