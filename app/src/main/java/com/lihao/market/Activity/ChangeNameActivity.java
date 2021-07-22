package com.lihao.market.Activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class ChangeNameActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private EditText nameEt;

    private TextView sureTv;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_change_name;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        nameEt = findViewById(R.id.nick_name);
        sureTv = findViewById(R.id.confirm);
    }

    @Override
    public void initData()
    {
        titleTv.setText("设置昵称");
        Intent intent = getIntent();
        if (intent != null)
        {
            String name = intent.getStringExtra("name");
            nameEt.setText(name);
        }
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        sureTv.setOnClickListener(this);
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
                changeName();
                break;
            default:
                break;
        }
    }

    private void changeName()
    {
        if (TextUtils.isEmpty(nameEt.getText().toString()))
        {
            ToastUtils.s(getApplicationContext(), "请输入昵称");
            return;
        }
        AjaxParams params = new AjaxParams();
        try {
            params.put("nick_name", nameEt.getText().toString());
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=profile&a=editprofile", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String name = object.getString("nick_name");
                        if (nameEt.getText().toString().equals(name))
                        {
                            Intent result = new Intent();
                            result.putExtra("name", name);
                            setResult(1, result);
                            finish();
                        }
                        else
                        {
                            ToastUtils.s(getApplicationContext(), "修改昵称失败");
                        }

                    } catch (Exception e)
                    {
                        Log.e("changeName", e.toString());
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
