package com.lihao.market.Activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Adapter.AddressListAdapter;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Bean.AddressBean;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends BaseActivity implements View.OnClickListener, AddressListAdapter.RefreshListener
{
    private TextView backTv;

    private TextView titleTv;

    private RecyclerView recyclerView;

    private TextView addTv;

    private AddressListAdapter listAdapter;

    private List<AddressBean> addressBeans = new ArrayList<>();

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_address_list;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        recyclerView = findViewById(R.id.recyclerview);
        addTv = findViewById(R.id.add_address);

        listAdapter = new AddressListAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(listAdapter);
    }

    @Override
    public void initData()
    {
        titleTv.setText("收货地址");
        showLoadingDialog();
        getAddressList();
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        addTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                sendBroadCast();
                finish();
                break;
            case R.id.add_address:
                Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
                intent.putExtra(KeySet.ADDRESS_TYPE, "0");
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        sendBroadCast();
        super.onBackPressed();
    }

    private void sendBroadCast()
    {
        Intent intent = new Intent("com.broadcast.addressupdate");
        BroadCastManager.getInstance().sendBroadCast(this, intent);
    }

    private void getAddressList()
    {
        addressBeans.clear();
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&a=getaddresslist", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray array = object.getJSONArray("consignee_list");
                        for (int i = 0; i < array.length(); i++)
                        {
                            JSONObject json = array.getJSONObject(i);
                            AddressBean address = new AddressBean();
                            address.setId(json.getString("address_id"));
                            address.setConsignee(json.getString("consignee"));
                            address.setAddress(json.getString("address"));
                            address.setMobile(json.getString("mobile"));
                            addressBeans.add(address);
                        }
                        listAdapter.setData(addressBeans);
                    } catch (Exception e)
                    {
                        Log.e("getAddressList", e.toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1)
        {
            getAddressList();
        }
    }

    @Override
    public void refresh()
    {
        getAddressList();
    }

    @Override
    public void editAddress(String name, String phone, String id)
    {
        Intent edit = new Intent(AddressListActivity.this, AddAddressActivity.class);
        edit.putExtra(KeySet.ADDRESS_TYPE, "1");
        edit.putExtra("name", name);
        edit.putExtra("phone", phone);
        edit.putExtra("addressId", id);
        startActivityForResult(edit, 1);
    }
}
