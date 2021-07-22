package com.lihao.market.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Activity.PayModeActivity;
import com.lihao.market.Adapter.OrderInfoAdapter;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.Bean.OrderBean;
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

public class AllOrderFragment extends BaseFragment implements OrderInfoAdapter.QueryOrderListener
{
    private RecyclerView mRecyclerView;

    private LinearLayout emptyLayout;

    private OrderInfoAdapter infoAdapter;

    private List<OrderBean> orderBeans = new ArrayList<>();

    private Context mContext;

    public AllOrderFragment(Context context)
    {
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View order = inflater.inflate(R.layout.fragment_all_order, container, false);

        initView(order);
//        initListener();
//        initData();

        return order;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            initData();
        }
    }

    private void initView(View view)
    {
        mRecyclerView = view.findViewById(R.id.recyclerview);
        emptyLayout = view.findViewById(R.id.empty_layout);

        infoAdapter = new OrderInfoAdapter(getContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(infoAdapter);
    }

    private void initData()
    {
        showLoadingDialog(mContext);
        try {
            AjaxParams params = new AjaxParams();
            params.put("page", "1");
            params.put("size", "20");
            params.put("status", "0");
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=order&a=getindex", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        orderBeans.clear();
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray jsonArray = object.getJSONArray("order_list");
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject op = jsonArray.getJSONObject(i);
                            OrderBean orderBean = new OrderBean();
                            orderBean.setOrder_id(op.getString("order_id"));
                            orderBean.setOrder_sn(op.getString("order_sn"));
                            orderBean.setOrder_time(op.getString("order_time"));
                            orderBean.setOrder_status(op.getString("order_status"));
                            orderBean.setConsignee(op.getString("consignee"));
                            orderBean.setOrder_goods_num(op.getString("order_goods_num"));
                            orderBean.setAddress(op.getString("address"));
                            orderBean.setAddress_detail(op.getString("address_detail"));
                            orderBean.setTotal_fee(op.getString("total_fee"));
                            orderBean.setOrder_url(op.getString("order_url"));
                            orderBean.setPay_status(op.getString("pay_status"));
                            orderBean.setOrder_del(op.getString("order_del"));
                            JSONArray array = op.getJSONArray("order_goods");
                            List<GoodBean> goodBeans = new ArrayList<>();
                            for (int j = 0; j < array.length(); j++)
                            {
                                JSONObject json = array.getJSONObject(j);
                                GoodBean goodBean = new GoodBean();
                                goodBean.setGoods_id(json.getString("goods_id"));
                                goodBean.setGoods_name(json.getString("goods_name"));
                                goodBean.setGoods_number(json.getString("goods_number"));
                                goodBean.setGoods_thumb(json.getString("goods_thumb"));
                                goodBean.setShop_price(json.getString("goods_price"));
                                goodBeans.add(goodBean);
                            }
                            orderBean.setOrder_goods(goodBeans);
                            orderBeans.add(orderBean);
                        }

                        if (orderBeans.size() > 0)
                        {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            emptyLayout.setVisibility(View.GONE);
                        }
                        else
                        {
                            mRecyclerView.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                        infoAdapter.setData(orderBeans);
                    } catch (Exception e)
                    {
                        Log.e("order_all", e.toString());
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
    public void queryOrder()
    {
        initData();
    }

    @Override
    public void payOrder(String order_sn, String orderId, String price)
    {
        Intent intent = new Intent(getContext(), PayModeActivity.class);
        intent.putExtra("order_sn", order_sn);
        intent.putExtra("price", price);
        intent.putExtra("order_id", orderId);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2)
        {
            initData();
        }
    }
}
