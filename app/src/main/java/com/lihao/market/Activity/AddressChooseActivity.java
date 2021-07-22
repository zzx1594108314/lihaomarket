package com.lihao.market.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Adapter.AddressChooseAdapter;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Bean.AddressBean;
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

public class AddressChooseActivity extends BaseActivity implements View.OnClickListener, AddressChooseAdapter.chooseDataListener
{
    private TextView backTv;

    private TextView titleTv;

    private RelativeLayout provinceLayout;

    private TextView provinceTv;

    private View provinceView;

    private RelativeLayout cityLayout;

    private TextView cityTv;

    private View cityView;

    private RelativeLayout countryLayout;

    private TextView countryTv;

    private View countryView;

    private RelativeLayout townLayout;

    private TextView townTv;

    private View townView;

    private RecyclerView recyclerView;

    private AddressChooseAdapter chooseAdapter;

    private List<AddressBean> addressBeans = new ArrayList<>();

    /**
     * 标志当前显示位置
     */
    private int index = 0;

    private String title;

    private String provinceId;

    private String cityId;

    private String countryId;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_address_choose;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        provinceLayout = findViewById(R.id.province_layout);
        provinceTv = findViewById(R.id.province);
        provinceView = findViewById(R.id.province_view);
        cityLayout = findViewById(R.id.city_layout);
        cityTv = findViewById(R.id.city);
        cityView = findViewById(R.id.city_view);
        countryLayout = findViewById(R.id.county_layout);
        countryTv = findViewById(R.id.county);
        countryView = findViewById(R.id.county_view);
        townLayout = findViewById(R.id.town_layout);
        townTv = findViewById(R.id.town);
        townView = findViewById(R.id.town_view);
        recyclerView = findViewById(R.id.recyclerview);

        chooseAdapter = new AddressChooseAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chooseAdapter);
    }

    @Override
    public void initData()
    {
        titleTv.setText("地址选择");
        title = "请选择";
        queryAddress("1");
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        provinceLayout.setOnClickListener(this);
        cityLayout.setOnClickListener(this);
        countryLayout.setOnClickListener(this);
        townLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.province_layout:
                queryAddress("1");
                index = 0;
                provinceTv.setText("请选择");
                provinceTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                cityLayout.setVisibility(View.GONE);
                countryLayout.setVisibility(View.GONE);
                townLayout.setVisibility(View.GONE);
                break;
            case R.id.city_layout:
                queryAddress(provinceId);
                index = 1;
                cityTv.setText("请选择");
                cityTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                countryLayout.setVisibility(View.GONE);
                townLayout.setVisibility(View.GONE);
                break;
            case R.id.county_layout:
                queryAddress(cityId);
                index = 2;
                townLayout.setVisibility(View.GONE);
                break;
            case R.id.town_layout:
                queryAddress(countryId);
                break;
            default:
                break;
        }
    }

    private void queryAddress(String id)
    {
        showTitle(title, id);
        addressBeans.clear();
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=region&a=address&parent_id=" + id, params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray array = object.getJSONArray("addressList");
                        if (array.length() < 1)
                        {
                            String address = provinceTv.getText().toString() + cityTv.getText().toString() + countryTv.getText().toString();
                            Intent result = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString(KeySet.ADDRESS, address);
                            bundle.putString(KeySet.PROVINCE_ID, provinceId);
                            bundle.putString(KeySet.CITY_ID, cityId);
                            bundle.putString(KeySet.COUNTRY_ID, countryId);
                            result.putExtras(bundle);
                            setResult(1, result);
                            finish();
                        }
                        for (int i = 0; i < array.length(); i++)
                        {
                            JSONObject jsonObject = array.getJSONObject(i);
                            AddressBean addressBean = new AddressBean();
                            addressBean.setId(jsonObject.getString("id"));
                            addressBean.setName(jsonObject.getString("name"));
                            addressBeans.add(addressBean);
                        }
                        chooseAdapter.setData(addressBeans, index);

                    } catch (Exception e)
                    {
                        Log.e("queryAddress", e.toString());
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

    private void showTitle(String name, String id)
    {
        switch (index)
        {
            case 0:
                provinceTv.setText(name);
                provinceTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                provinceView.setVisibility(View.VISIBLE);
                break;
            case 1:
                provinceTv.setText(name);
                provinceTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                provinceView.setVisibility(View.GONE);
                cityLayout.setVisibility(View.VISIBLE);
                cityTv.setText("请选择");
                cityTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                cityView.setVisibility(View.VISIBLE);
                provinceId = id;
                break;
            case 2:
                cityTv.setText(name);
                cityTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                cityView.setVisibility(View.GONE);
                countryLayout.setVisibility(View.VISIBLE);
                countryTv.setText("请选择");
                countryTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                countryView.setVisibility(View.VISIBLE);
                cityId = id;
                break;
            case 3:
                countryTv.setText(name);
                countryTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                countryView.setVisibility(View.GONE);
                townLayout.setVisibility(View.VISIBLE);
                townTv.setText("请选择");
                townTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                townView.setVisibility(View.VISIBLE);
                countryId = id;
                break;
            default:
                break;
        }
    }

    @Override
    public void getChoose(String id, String name, int position)
    {
        index = position;
        title = name;
        showTitle(title, id);
        if (index == 3)
        {
            hideLoadingDialog();
            countryId = id;
            String address = provinceTv.getText().toString() + cityTv.getText().toString() + countryTv.getText().toString();
            Intent result = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(KeySet.ADDRESS, address);
            bundle.putString(KeySet.PROVINCE_ID, provinceId);
            bundle.putString(KeySet.CITY_ID, cityId);
            bundle.putString(KeySet.COUNTRY_ID, countryId);
            result.putExtras(bundle);
            setResult(1, result);
            finish();
        }
        queryAddress(id);
//        showTitle(name);
    }
}
