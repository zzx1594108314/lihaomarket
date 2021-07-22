package com.lihao.market.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.ToastUtils;
import com.wby.utility.ToastUtil;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;


public class RegisterActivity extends BaseActivity implements View.OnClickListener
{
    private TextView titleTv;

    private TextView backTv;

    private EditText inputNumber;

    private EditText inputPwd;

    private EditText inputCode;

    private ImageView numberDelete;

    private ImageView pwdDelete;

    private TextView codeTv;

    private TextView confirmTv;

    private CheckBox showBox;

    private CheckBox agreeBox;

    private TextView serviceTv;

    private CountDownTimer timer;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_register;
    }

    @Override
    public void initView()
    {
        titleTv = findViewById(R.id.tv_title);
        backTv = findViewById(R.id.tv_back);
        inputNumber = findViewById(R.id.input);
        inputPwd = findViewById(R.id.edt_pwd);
        inputCode = findViewById(R.id.input_code);
        numberDelete = findViewById(R.id.account_delete);
        pwdDelete = findViewById(R.id.pwd_delete);
        codeTv = findViewById(R.id.code);
        confirmTv = findViewById(R.id.confirm);
        showBox = findViewById(R.id.cb);
        agreeBox = findViewById(R.id.item_checkbox);
        serviceTv = findViewById(R.id.service);

        timer = new TimeCount(60000, 1000);
    }

    @Override
    public void initData()
    {
        titleTv.setText("新用户注册");
    }

    @Override
    public void initListener()
    {
        codeTv.setOnClickListener(this);
        confirmTv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        serviceTv.setOnClickListener(this);

        inputNumber.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                {
                    numberDelete.setVisibility(View.VISIBLE);
                }
                else
                {
                    numberDelete.setVisibility(View.GONE);
                }
            }
        });
        inputPwd.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                {
                    pwdDelete.setVisibility(View.VISIBLE);
                }
                else
                {
                    pwdDelete.setVisibility(View.GONE);
                }
            }
        });
        showBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int passwordLength = inputPwd.getText().length();
                inputPwd.setInputType(isChecked ?
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
                inputPwd.setSelection(passwordLength);
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getCode()
    {
        if (TextUtils.isEmpty(inputNumber.getText()))
        {
            ToastUtils.s(RegisterActivity.this, "请输入手机号");
            return;
        }

        AjaxParams params = new AjaxParams();
        try {
            params.put("mobile", inputNumber.getText().toString());
            params.put("flag", "register");
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
            ToastUtils.s(RegisterActivity.this, "请输入手机号");
            return;
        }

        if (inputPwd.getText().toString().length() < 6)
        {
            ToastUtils.s(RegisterActivity.this, "密码少于6位");
            return;
        }

        if (TextUtils.isEmpty(inputPwd.getText()))
        {
            ToastUtils.s(RegisterActivity.this, "请输入密码");
            return;
        }

        if (TextUtils.isEmpty(inputCode.getText()))
        {
            ToastUtils.s(RegisterActivity.this, "请输入验证码");
            return;
        }

        try {
            AjaxParams params = new AjaxParams();
            params.put("mobile", inputNumber.getText().toString());
            params.put("password", inputPwd.getText().toString());
            params.put("mobile_code", inputCode.getText().toString());
            params.put("enabled_sms", "1");
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login&a=getregister", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String message = object.getString("info");
                        String result = object.getString("status");
                        ToastUtils.s(getApplicationContext(), message);
                        if ("y".equals(result))
                        {
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.code:
                timer.start();
                getCode();
                break;
            case R.id.confirm:
                if (agreeBox.isChecked())
                {
                    login();
                }
                else
                {
                    ToastUtils.s(RegisterActivity.this, "请同意用户注册协议");
                }
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.service:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.yzbttech.cn/download/LHSC.html");//此处填链接
                intent.setData(content_url);
                startActivity(intent);
                break;
            default:
                break;
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
