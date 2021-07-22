package com.lihao.market.Activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Adapter.OrderDetailItemAdapter;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Bean.CartBean;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.Bean.OrderBean;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderDetailActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private TextView nameTv;

    private TextView phoneTv;

    private TextView addressTv;

    private TextView orderSnTv;

    private TextView timeTv;

    private RecyclerView recyclerView;

    private TextView realPrice;

    private TextView submitTv;

    private TextView productPriceTv;

    private RelativeLayout deductionLayout;

    private TextView deductionTv;

    private ImageView serviceIv;

    private String orderId;

    private List<GoodBean> goodBeans = new ArrayList<>();

    private OrderDetailItemAdapter itemAdapter;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_order_detail;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        nameTv = findViewById(R.id.name);
        phoneTv = findViewById(R.id.phone);
        addressTv = findViewById(R.id.address);
        orderSnTv = findViewById(R.id.order_sn);
        timeTv = findViewById(R.id.time);
        recyclerView = findViewById(R.id.recyclerview);
        realPrice = findViewById(R.id.real_price);
        submitTv = findViewById(R.id.submit_order);
        productPriceTv = findViewById(R.id.product_price);
        deductionLayout = findViewById(R.id.deduction_layout);
        deductionTv = findViewById(R.id.deduction);
        serviceIv = findViewById(R.id.service);

        itemAdapter = new OrderDetailItemAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void initData()
    {
        titleTv.setText("订单详情");
        Intent intent = getIntent();
        if (intent != null)
        {
            orderId = intent.getStringExtra("orderId");
        }
        queryOrderInfo();
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        serviceIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.service:
                String account = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ACCOUNT);
                HashMap<String, String> info = new HashMap<>();
                info.put("name", account);
                info.put("tel", account);
                Intent intent = new MQIntentBuilder(this).setCustomizedId(account).setClientInfo(info).build();
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void queryOrderInfo()
    {
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        params.put("order_id", orderId);
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=order&a=getdetail", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        goodBeans.clear();
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        if ("0".equals(error))
                        {
                            JSONObject order = object.getJSONObject("order");
                            nameTv.setText(order.getString("consignee"));
                            phoneTv.setText(order.getString("mobile"));
                            addressTv.setText(order.getString("detail_address"));
                            orderSnTv.setText(order.getString("order_sn"));
                            timeTv.setText(order.getString("add_time"));
                            productPriceTv.setText(order.getString("formated_goods_amount"));
                            realPrice.setText(order.getString("formated_order_amount"));
                            submitTv.setText(order.getString("order_status"));
                            int integral = Integer.parseInt(order.getString("integral"));
                            if (integral > 0)
                            {
                                deductionLayout.setVisibility(View.VISIBLE);
                                deductionTv.setText("-" + order.getString("formated_integral_money"));
                            }
                            else
                            {
                                deductionLayout.setVisibility(View.GONE);
                            }



//                            orderBean.setOrder_id(order.getString("order_id"));
//                            orderBean.setOrder_sn(order.getString("order_sn"));
//                            orderBean.setOrder_status(order.getString("order_status"));
//                            orderBean.setConsignee(order.getString("consignee"));
//                            orderBean.setMobile(order.getString("mobile"));
//                            orderBean.setAddress_detail(order.getString("detail_address"));
//                            orderBean.setOrder_time(order.getString("add_time"));
//                            orderBean.setIntegral(order.getString("integral"));
//                            orderBean.setFormated_integral_money(order.getString("formated_integral_money"));
//                            orderBean.setFormated_order_amount(order.getString("formated_order_amount"));

                            JSONArray array = object.getJSONArray("goods_list");
                            for (int i = 0; i < array.length(); i++)
                            {
                                JSONObject json = array.getJSONObject(i);
                                GoodBean goodBean = new GoodBean();
                                goodBean.setGoods_thumb(json.getString("goods_thumb"));
                                goodBean.setGoods_name(json.getString("goods_name"));
                                goodBean.setShop_price(json.getString("goods_price"));
                                goodBean.setGoods_desc(json.getString("goods_attr"));
                                goodBean.setGoods_number(json.getString("goods_number"));
                                goodBeans.add(goodBean);
                            }
                            itemAdapter.setData(goodBeans);
//                            orderBean.setOrder_goods(goodBeans);
                        }
                    } catch (Exception e)
                    {
                        Log.e("queryOrderInfo", e.toString());
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
