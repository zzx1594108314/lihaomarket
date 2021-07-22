package com.lihao.market.Activity;

import android.content.Intent;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Custom.CircleImageView;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;
import com.lihao.market.wxapi.WXEntryActivity;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

public class BindActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private CircleImageView imageView;

    private TextView nameTv;

    private EditText phoneEt;

    private EditText passwordEt;

    private CheckBox checkBox;

    private TextView bindTv;

    private TextView registerTv;

    private String ident;

    private String name;

    private String headUrl;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_bind;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        imageView = findViewById(R.id.image);
        nameTv = findViewById(R.id.name);
        phoneEt = findViewById(R.id.phone);
        passwordEt = findViewById(R.id.password);
        checkBox = findViewById(R.id.checkbox);
        bindTv = findViewById(R.id.bind);
        registerTv = findViewById(R.id.quick_register);
    }

    @Override
    public void initData()
    {
        titleTv.setText("绑定账号");

        Intent intent = getIntent();
        if (intent != null)
        {
            ident = intent.getStringExtra("bind");
            name = intent.getStringExtra("name");
            headUrl = intent.getStringExtra("headUrl");
            nameTv.setText(name);
            if (TextUtils.isEmpty(headUrl))
            {
                headUrl = KeySet.URL + "/mobile/public/img/user_default.png";
            }
            Glide.with(this).load(headUrl)
                    .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_middle_common).error(R.mipmap.ic_middle_common)).into(imageView);
        }

        String str_1 = "没有账号?您可以";
        String str_2 = "立即注册";
        SpannableString ss = new SpannableString(str_2);
        MyClickableSpan clickSpan = new MyClickableSpan();
        ss.setSpan(clickSpan, 0, str_2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerTv.setText(str_1);
        registerTv.append(ss);
        //必须加这一句，否则就无法被点击
        registerTv.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        bindTv.setOnClickListener(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int passwordLength = passwordEt.getText().length();
                passwordEt.setInputType(isChecked ?
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
                passwordEt.setSelection(passwordLength);
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
            case R.id.bind:
                bindWx();
                break;
            default:
                break;
        }
    }

    private class MyClickableSpan extends ClickableSpan
    {

        @Override
        public void updateDrawState(@NonNull TextPaint ds)
        {
            super.updateDrawState(ds);
            ds.setColor(getResources().getColor(R.color.sort_select_title_text));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget)
        {
            Intent intent = new Intent(BindActivity.this, BindRegisterActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("headUrl", headUrl);
            intent.putExtra("ident", ident);
            startActivity(intent);
        }
    }

    private void bindWx()
    {
        if (TextUtils.isEmpty(phoneEt.getText().toString()))
        {
            ToastUtils.s(getApplicationContext(), "请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(passwordEt.getText().toString()))
        {
            ToastUtils.s(getApplicationContext(), "请输入密码");
            return;
        }

        AjaxParams params = new AjaxParams();
        params.put("username", phoneEt.getText().toString());
        params.put("password", passwordEt.getText().toString());
        params.put("bind_ident", ident);
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login&a=Getbind", params, new AjaxCallBack<Object>() {
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
                            SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.ACCOUNT, phoneEt.getText().toString());
                            SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.PASSWORD, passwordEt.getText().toString());
                            Intent intent = new Intent("com.broadcast.bindwx");
                            intent.putExtra("msg", 2);
                            BroadCastManager.getInstance().sendBroadCast(BindActivity.this, intent);
                            finish();
                        }
                        else
                        {
                            ToastUtils.s(getApplicationContext(), info);
                        }
                    } catch (Exception e)
                    {
                        Log.e("bindWx", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
