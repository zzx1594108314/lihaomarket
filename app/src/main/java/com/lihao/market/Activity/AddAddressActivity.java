package com.lihao.market.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

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

public class AddAddressActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private EditText nameTv;

    private EditText phoneTv;

    private TextView addressTv;

    private EditText detailTv;

    private TextView saveTv;

    private String provinceId;

    private String cityId;

    private String countryId;

    private String addressId;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_add_address;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        nameTv = findViewById(R.id.name);
        phoneTv = findViewById(R.id.phone);
        detailTv = findViewById(R.id.detail_edit);
        addressTv = findViewById(R.id.address);
        saveTv = findViewById(R.id.save);
    }

    @Override
    public void initData()
    {
        Intent intent = getIntent();
        if (intent != null)
        {
            String type = intent.getStringExtra(KeySet.ADDRESS_TYPE);
            if ("0".equals(type))
            {
                titleTv.setText("新增地址");
            }
            else
            {
                titleTv.setText("编辑地址");
                String name = intent.getStringExtra("name");
                String phone = intent.getStringExtra("phone");
                addressId = intent.getStringExtra("addressId");
                nameTv.setText(name);
                phoneTv.setText(phone);
            }
        }
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        addressTv.setOnClickListener(this);
        saveTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.address:
                Intent intent = new Intent(AddAddressActivity.this, AddressChooseActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.save:
                saveAddress();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null)
        {
            Bundle bundle = data.getExtras();
            if (resultCode == 1 && bundle != null)
            {
                String address = bundle.getString(KeySet.ADDRESS);
                provinceId = bundle.getString(KeySet.PROVINCE_ID);
                cityId = bundle.getString(KeySet.CITY_ID);
                countryId = bundle.getString(KeySet.COUNTRY_ID);
                addressTv.setText(address);
            }
        }
    }

    private void saveAddress()
    {
        if (TextUtils.isEmpty(nameTv.getText()))
        {
            ToastUtils.s(AddAddressActivity.this, "请输入收货人姓名");
            return;
        }
        if (TextUtils.isEmpty(phoneTv.getText()))
        {
            ToastUtils.s(AddAddressActivity.this, "请输入联系电话");
            return;
        }
        if (TextUtils.isEmpty(addressTv.getText()))
        {
            ToastUtils.s(AddAddressActivity.this, "请选择所在地区");
            return;
        }
        if (TextUtils.isEmpty(detailTv.getText()))
        {
            ToastUtils.s(AddAddressActivity.this, "请输入详细地址");
            return;
        }
        try {
            AjaxParams params = new AjaxParams();
            params.put("address_id", addressId);
            params.put("consignee", nameTv.getText().toString());
            params.put("mobile", phoneTv.getText().toString());
            params.put("province_region_id", provinceId);
            params.put("city_region_id", cityId);
            params.put("district_region_id", countryId);
            params.put("address", detailTv.getText().toString());
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&a=add_address", params, new AjaxCallBack<Object>()
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
                            setResult(1);
                            finish();
                        }
                    } catch (Exception e)
                    {
                        Log.e("saveAddress", e.toString());
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
