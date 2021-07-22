package com.lihao.market.Activity;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.lihao.market.Adapter.OrderListAdapter;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.Custom.MyGridView;
import com.lihao.market.Dialog.CustomDialog;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车提交订单
 */
public class SubmitOrderActivity extends BaseActivity implements View.OnClickListener
{
    private TextView backTv;

    private TextView titleTv;

    private TextView nameTv;

    private TextView phoneTv;

    private TextView addressTv;

    private ImageView moreTv;

    private MyGridView myGridView;

    private EditText inputEt;

    private TextView goodNum;

    private TextView shopNum;

    private TextView goodTotal;

    private TextView realPrice;

    private TextView submitTv;

    private RelativeLayout tipLayout;

    private TextView pointTipTv;

    private Switch pointSwitch;

    private TextView productPriceTv;

    private RelativeLayout deductionLayout;

    private TextView deductionTv;

    private RelativeLayout balanceLayout;

    private Switch balanceSwitch;

    private LinearLayout payPwdLayout;

    private EditText inputPay;

    private OrderListAdapter listAdapter;

    private List<GoodBean> goodBeans = new ArrayList<>();

    private String totalPrice = "0";

    private String integral = "0";

    private String surPlus = "0";

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_submit_order;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        nameTv = findViewById(R.id.name);
        phoneTv = findViewById(R.id.phone);
        addressTv = findViewById(R.id.address);
        moreTv = findViewById(R.id.more);
        myGridView = findViewById(R.id.gridview);
        inputEt = findViewById(R.id.message);
        goodNum = findViewById(R.id.good_num);
        shopNum = findViewById(R.id.num);
        goodTotal = findViewById(R.id.price);
        realPrice = findViewById(R.id.real_price);
        submitTv = findViewById(R.id.submit_order);
        tipLayout = findViewById(R.id.point_tip_layout);
        pointTipTv = findViewById(R.id.point_tip);
        pointSwitch = findViewById(R.id.tip_switch_disturb);
        productPriceTv = findViewById(R.id.product_price);
        deductionLayout = findViewById(R.id.deduction_layout);
        deductionTv = findViewById(R.id.deduction);
        balanceLayout = findViewById(R.id.balance_tip_layout);
        balanceSwitch = findViewById(R.id.balance_switch_disturb);
        payPwdLayout = findViewById(R.id.pay_pwd_layout);
        inputPay = findViewById(R.id.input_pay);

        listAdapter = new OrderListAdapter(this);
        myGridView.setAdapter(listAdapter);
    }

    @Override
    public void initData()
    {
        titleTv.setText("订单确认");
        getOrderInfo();
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        moreTv.setOnClickListener(this);
        submitTv.setOnClickListener(this);

        pointSwitch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean isCheck = ((Switch)v).isChecked();
                if (isCheck)
                {
                    changeIntegral("1");
                    deductionLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    changeIntegral("0");
                    deductionLayout.setVisibility(View.GONE);
                }
            }
        });

        balanceSwitch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean isCheck = ((Switch)v).isChecked();
                if (isCheck)
                {
                    surPlus = "1";
                    changeBalance("1");
                    payPwdLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    surPlus = "0";
                    changeBalance("0");
                    payPwdLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                setResult(1);
                finish();
                break;
            case R.id.more:
                Intent more = new Intent(SubmitOrderActivity.this, FlowAddressListActivity.class);
                startActivityForResult(more, 1);
                break;
            case R.id.submit_order:
                submitOrder();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        setResult(1);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1)
        {
            getOrderInfo();
        }
        else if (resultCode == 2)
        {
            submitTv.setText("支付完成");
            submitTv.setEnabled(false);
            submitTv.setBackgroundColor(getResources().getColor(R.color.order_submit_or_over));
            realPrice.setText("0");
        }
        else if (resultCode == 3)
        {
            submitTv.setText("订单已提交");
            submitTv.setEnabled(false);
            submitTv.setBackgroundColor(getResources().getColor(R.color.order_submit_or_over));
            realPrice.setText("0");
        }
    }

    private void changeIntegral(String point)
    {
        AjaxParams params = new AjaxParams();
        try {

            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=flow&a=change_integral&points=" + point, params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        integral = object.getString("integral");
                        String amount = object.getString("amount");
                        realPrice.setText(amount);
                        deductionTv.setText("-" + integral);
                        totalPrice = amount.replace("¥", "");
                    } catch (Exception e)
                    {
                        Log.e("changeIntegral", e.toString());
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

    private void changeBalance(String surplus)
    {
        AjaxParams params = new AjaxParams();
        try {

            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=flow&a=change_surplus&surplus=" + surplus, params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                    } catch (Exception e)
                    {
                        Log.e("changeBalance", e.toString());
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

    private void getOrderInfo()
    {
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=flow&a=getindex", params, new AjaxCallBack<Object>() {
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
                            int sum = 0;
                            JSONObject consignee = object.getJSONObject("consignee");
                            nameTv.setText(consignee.getString("consignee"));
                            phoneTv.setText(consignee.getString("mobile"));
                            addressTv.setText(consignee.getString("province_name") + consignee.getString("city_name")
                                    + consignee.getString("district_name") + consignee.getString("address"));
                            JSONArray cartArray = object.getJSONArray("cart_goods_list");
                            if (cartArray.length() > 0)
                            {
                                JSONObject cartObject = cartArray.getJSONObject(0);
                                String shopNumber = cartObject.getString("shop_goods_num");
                                shopNum.setText("共" + shopNumber + "款");
                                JSONArray goodArray = cartObject.getJSONArray("goods_list");
                                for (int i = 0; i < goodArray.length(); i++)
                                {
                                    JSONObject goodObject = goodArray.getJSONObject(i);
                                    GoodBean bean = new GoodBean();
                                    bean.setGoods_thumb(goodObject.getString("goods_thumb"));
                                    try
                                    {
                                        sum = sum + Integer.parseInt(goodObject.getString("goods_number"));
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e("getOrderInfo","getOrderInfo: e" + e.toString());
                                    }
                                    goodBeans.add(bean);
                                }
                            }
                            listAdapter.setData(goodBeans);
                            goodNum.setText("共" + sum + "件商品");
                            String totalObject = object.getString("total_goods_price");
                            goodTotal.setText(totalObject);
                            realPrice.setText(totalObject);
                            productPriceTv.setText(totalObject);
                            totalPrice = object.getString("goods_price");

                            String allow_use_surplus = object.getString("allow_use_surplus");
                            String user_money = object.getString("user_money");
                            if ("1".equals(allow_use_surplus))
                            {
                                Number number = Float.parseFloat(user_money) * 100;
                                if (number.intValue() > 0)
                                {
                                    balanceLayout.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    balanceLayout.setVisibility(View.GONE);
                                }
                            }

                            String integral_money = object.getString("integral_money");
                            String your_integral = object.getString("your_integral");
                            int integral = Integer.parseInt(integral_money);
                            if (integral > 0)
                            {
                                tipLayout.setVisibility(View.VISIBLE);
                                String tip = "可使用<font color = '#F94747'>" + your_integral + "</font>，本单最多可用<font " +
                                        "color = '#F94747'>" + integral_money + "</font>积分抵扣";
                                pointTipTv.setText(Html.fromHtml(tip));
                            }
                            else
                            {
                                tipLayout.setVisibility(View.VISIBLE);
                            }

                        }
                        else if ("2".equals(error))
                        {
                            Intent more = new Intent(SubmitOrderActivity.this, FlowAddressListActivity.class);
                            startActivityForResult(more, 1);
                        }
                    } catch (Exception e)
                    {
                        Log.e("getOrderInfo", e.toString());
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

    private void submitOrder()
    {
        submitTv.setText("提交中...");
        submitTv.setEnabled(false);
        AjaxParams params = new AjaxParams();
        try {
            params.put("shipping_prompt", "0");
            params.put("ru_id", "0");
            params.put("shipping_type", "0");
            params.put("shipping_code", "fpd");
            params.put("shipping", "11");
            params.put("payment", "15");
            params.put("shipping_type", "0");
            params.put("integral", integral);
            params.put("is_surplus", surPlus);
            params.put("pay_paypwd", inputPay.getText().toString());
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=flow&a=getdone", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        submitTv.setText("提交订单");
                        submitTv.setEnabled(true);
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        if ("0".equals(error))
                        {
                            JSONObject orderInfo = object.getJSONObject("order_info");
                            String order_sn = orderInfo.getString("order_sn");
                            String order_id = orderInfo.getString("order_id");
                            Intent intent = new Intent(SubmitOrderActivity.this, PayModeActivity.class);
                            intent.putExtra("order_sn", order_sn);
                            intent.putExtra("price", totalPrice);
                            intent.putExtra("order_id", order_id);
                            startActivityForResult(intent, 1);
                        }
                        else if ("1".equals(error))
                        {
                            String order_sn = object.getString("order_sn");
                            Intent point = new Intent(SubmitOrderActivity.this, PayPointActivity.class);
                            point.putExtra("order_sn", order_sn);
                            startActivityForResult(point, 1);
                        }
                        else if ("2".equals(error))
                        {
                            final CustomDialog customDialog = new CustomDialog(SubmitOrderActivity.this);
                            customDialog.setTitle("提示");
                            customDialog.setContent("未启用支付密码，是否去启用？");
                            customDialog.setOnPositiveListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent payPwd = new Intent(SubmitOrderActivity.this, AccountSafeActivity.class);
                                    startActivity(payPwd);
                                    customDialog.dismiss();
                                    finish();
                                }
                            });
                            customDialog.setOnNegativeListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    customDialog.dismiss();
                                    finish();
                                }
                            });
                            customDialog.show();

                        }
                        else if ("3".equals(error))
                        {
                            String content = object.getString("content");
                            ToastUtils.s(SubmitOrderActivity.this, content);
                        }
                    } catch (Exception e)
                    {
                        Log.e("submitOrder", e.toString());
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
