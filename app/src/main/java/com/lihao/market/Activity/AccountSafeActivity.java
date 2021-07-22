package com.lihao.market.Activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

public class AccountSafeActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private RelativeLayout changePw;

    private RelativeLayout mobileLayout;

    private RelativeLayout payPw;

    private TextView mobileTv;

    private ImageView mobileIv;

    private TextView payTv;

    private ImageView payIv;

    private String account;

    private String isEdit = "0";

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_account_safe;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        changePw = findViewById(R.id.change_pw);
        mobileLayout = findViewById(R.id.mobile_layout);
        payPw = findViewById(R.id.pay_pwd);
        mobileTv = findViewById(R.id.mobile_text);
        mobileIv = findViewById(R.id.safe_mobile);
        payTv = findViewById(R.id.pay_text);
        payIv = findViewById(R.id.pay_image);
    }

    @Override
    public void initData()
    {
        titleTv.setText("账户安全");
        init();
        account = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ACCOUNT);
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        changePw.setOnClickListener(this);
        mobileLayout.setOnClickListener(this);
        payPw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.change_pw:
                Intent intent1 = new Intent(AccountSafeActivity.this, ForgetPasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.mobile_layout:
                Intent intent2 = new Intent(AccountSafeActivity.this, SafeMobileActivity.class);
                intent2.putExtra("account", account);
                startActivityForResult(intent2, 1);
                break;
            case R.id.pay_pwd:
                if (TextUtils.isEmpty(account))
                {
                    ToastUtils.s(AccountSafeActivity.this, "请先验证手机");
                    return;
                }
                Intent intent3 = new Intent(AccountSafeActivity.this, PayPasswordActivity.class);
                intent3.putExtra("account", account);
                intent3.putExtra("isEdit", isEdit);
                startActivityForResult(intent3, 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1)
        {
            init();
        }
    }

    private void init()
    {
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=profile&a=getaccountsafe", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String users_paypwd = object.getString("users_paypwd");
                        if (!"0".equals(users_paypwd))
                        {
                            isEdit = "1";
                            payTv.setText("已启用支付密码");
                            payTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                            payIv.setBackgroundResource(R.mipmap.ic_safe);
                        }
                        if (!TextUtils.isEmpty(account))
                        {
                            mobileTv.setText("已验证手机" + account);
                            mobileTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                            mobileIv.setBackgroundResource(R.mipmap.ic_safe);
                        }
                    } catch (Exception e)
                    {
                        Log.e("init", e.toString());
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
}
