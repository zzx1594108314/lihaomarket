package com.lihao.market.Activity;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

/**
 * 设置密码
 */
public class SettingPasswordActivity extends BaseActivity implements View.OnClickListener
{
    private TextView titleTv;

    private TextView backTv;

    private EditText wordEt;

    private CheckBox wordBox;

    private EditText wordMoreEt;

    private CheckBox wordMoreBox;

    private TextView confirmTv;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_setting_password;
    }

    @Override
    public void initView()
    {
        titleTv = findViewById(R.id.tv_title);
        backTv = findViewById(R.id.tv_back);
        wordEt = findViewById(R.id.input);
        wordBox = findViewById(R.id.one_cb);
        wordMoreEt = findViewById(R.id.edt_pwd);
        wordMoreBox = findViewById(R.id.two_cb);
        confirmTv = findViewById(R.id.confirm);
    }

    @Override
    public void initData()
    {
        titleTv.setText("设置密码");
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        confirmTv.setOnClickListener(this);

        wordBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int passwordLength = wordEt.getText().length();
                wordEt.setInputType(isChecked ?
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
                wordEt.setSelection(passwordLength);
            }
        });

        wordMoreBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int passwordLength = wordMoreEt.getText().length();
                wordMoreEt.setInputType(isChecked ?
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
                wordMoreEt.setSelection(passwordLength);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.confirm:
                setPassword();
                break;
            default:
                break;
        }
    }

    /**
     * 修改密码
     */
    private void setPassword()
    {
        if (wordEt.getText().toString().length() < 6)
        {
            ToastUtils.s(SettingPasswordActivity.this, "密码少于6位");
            return;
        }
        if (!wordEt.getText().toString().equals(wordMoreEt.getText().toString()))
        {
            ToastUtils.s(SettingPasswordActivity.this, "两次密码不一致");
            return;
        }

        AjaxParams params = new AjaxParams();
        try {
            params.put("password", wordEt.getText().toString());
            params.put("repassword", wordMoreEt.getText().toString());
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login&a=geteditforgetpassword", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String content = object.getString("content");
                        ToastUtils.l(getApplicationContext(), content);

                    } catch (Exception e)
                    {
                        Log.e("setPassword", e.toString());
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
