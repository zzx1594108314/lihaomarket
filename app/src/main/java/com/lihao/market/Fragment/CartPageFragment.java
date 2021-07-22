package com.lihao.market.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Activity.SubmitOrderActivity;
import com.lihao.market.Adapter.CartItemAdapter;
import com.lihao.market.Adapter.ProductListAdapter;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Bean.CartBean;
import com.lihao.market.Bean.ProductBean;
import com.lihao.market.Custom.MyGridView;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 购物车
 */
public class CartPageFragment extends BaseFragment implements CartItemAdapter.DeleteListener, CartItemAdapter.SelectNumListener, View.OnClickListener
{
    private static final String ARG_SHOW_TEXT = "text";

    private TextView editTv;

    private CheckBox titleBox;

    private RecyclerView cartView;

    private CheckBox allBox;

    private TextView totalPrice;

    private RelativeLayout buyLayout;

    private TextView buyText;

    private RelativeLayout editLayout;

    private TextView deleteTv;

    private TextView saveText;

    private MyGridView myGridView;

    private LinearLayout noDataLayout;

    private RelativeLayout hasDataLayout;

    private List<CartBean> cartBeans = new ArrayList<>();

    /**
     * 猜你喜欢
     */
    private List<ProductBean> productBeans = new ArrayList<>();

    private CartItemAdapter cartItemAdapter;

    private ProductListAdapter listAdapter;

    private List<String> IDS = new ArrayList<>();

    /**
     * 是否已刷新过购物车
     */
    private boolean hide = true;

    private int buyNum = 0;

    public CartPageFragment()
    {}

    public static CartPageFragment newInstance(String param1)
    {
        CartPageFragment cartPageFragment = new CartPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        cartPageFragment.setArguments(args);
        return cartPageFragment;
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
        View cart = inflater.inflate(R.layout.fragment_cart_page, container, false);

        initView(cart);
        initListener();

        if (LoginUtil.getLogin())
        {
            noDataLayout.setVisibility(View.GONE);
            hasDataLayout.setVisibility(View.VISIBLE);
            initData();
        }
        else
        {
            noDataLayout.setVisibility(View.VISIBLE);
            hasDataLayout.setVisibility(View.GONE);
        }

        return cart;
    }

    private void initView(View view)
    {
        editTv = view.findViewById(R.id.edit_text);
        titleBox = view.findViewById(R.id.title_checkbox);
        cartView = view.findViewById(R.id.shop_recycler);
        allBox = view.findViewById(R.id.all_checkbox);
        totalPrice = view.findViewById(R.id.total_price);
        buyLayout = view.findViewById(R.id.buy_layout);
        buyText = view.findViewById(R.id.buy_text);
        editLayout = view.findViewById(R.id.edit_layout);
        deleteTv = view.findViewById(R.id.delete_text);
        saveText = view.findViewById(R.id.save_text);
        myGridView = view.findViewById(R.id.mygridview);
        noDataLayout = view.findViewById(R.id.no_data_layout);
        hasDataLayout = view.findViewById(R.id.has_data);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cartView.setLayoutManager(layoutManager);
        cartItemAdapter = new CartItemAdapter(getContext(), this, this);
        cartView.setAdapter(cartItemAdapter);

        listAdapter = new ProductListAdapter(getContext());
        myGridView.setAdapter(listAdapter);

    }

    private void initListener()
    {
        buyText.setOnClickListener(this);
        editTv.setOnClickListener(this);
        deleteTv.setOnClickListener(this);

        allBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (cartBeans.size() > 0)
                {
                    boolean isCheck = ((CheckBox) v).isChecked();
                    cartItemAdapter.allSelect(isCheck, true);
                }
            }
        });
    }

    private void initData()
    {
        showLoadingDialog();
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=cart&a=getindex", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        int check = 0;
                        int isInviled = 0;
                        JSONObject object = new JSONObject(o.toString());
                        if (object.getString("error").equals("0"))
                        {
                            cartBeans.clear();
                            productBeans.clear();
                            JSONArray array = object.getJSONArray("goods_list");
                            if (array.length() > 0)
                            {
                                JSONObject jsonObject = array.getJSONObject(0);
                                JSONArray jsonArray = jsonObject.getJSONArray("goods_list");
                                if (jsonArray.length() > 0)
                                {
                                    for (int i = 0; i < jsonArray.length(); i++)
                                    {
                                        JSONObject json = jsonArray.getJSONObject(i);
                                        CartBean cartBean = new CartBean();
                                        cartBean.setRec_id(json.getString("rec_id"));
                                        cartBean.setUser_id(json.getString("user_id"));
                                        cartBean.setGoods_id(json.getString("goods_id"));
                                        cartBean.setGoods_sn(json.getString("goods_sn"));
                                        cartBean.setProduct_id(json.getString("product_id"));
                                        cartBean.setGoods_name(json.getString("goods_name"));
                                        cartBean.setMarket_price(json.getString("market_price"));
                                        cartBean.setGoods_price(json.getString("goods_price"));
                                        cartBean.setGoods_number(json.getString("goods_number"));
                                        cartBean.setGoods_attr(json.getString("goods_attr"));
                                        cartBean.setGoods_thumb(json.getString("goods_thumb"));
                                        cartBean.setAttr_number(json.getString("attr_number"));
                                        cartBean.setIs_invalid(json.getString("is_invalid"));
                                        cartBean.setIs_checked(json.getString("is_checked"));
                                        if ("1".equals(json.getString("is_invalid")))
                                        {
                                            isInviled++;
                                        }
                                        if ("1".equals(json.getString("is_checked")))
                                        {
                                            check++;
                                        }
                                        cartBeans.add(cartBean);
                                    }
                                    if (check == cartBeans.size() - isInviled)
                                    {
                                        allBox.setChecked(true);
                                    }
                                    cartItemAdapter.setData(cartBeans);
                                }
                            }

                            JSONObject guestList = object.getJSONObject("guess_list");
                            Iterator<String> it = guestList.keys();
                            while (it.hasNext())
                            {
                                JSONObject guest = guestList.getJSONObject(it.next());
                                ProductBean productBean = new ProductBean();
                                productBean.setGoods_id(guest.getString("goods_id"));
                                productBean.setGoods_name(guest.getString("goods_name"));
                                productBean.setImg(guest.getString("goods_thumb"));
                                productBean.setShop_price(guest.getString("shop_price_formated"));
                                productBean.setSale(guest.getString("sales_volume"));
                                productBean.setStock("9999");
                                productBeans.add(productBean);
                            }
                            listAdapter.setData(productBeans);

                            JSONObject total = object.getJSONObject("total");
                            totalPrice.setText(total.getString("goods_price"));
                            buyText.setText("去结算(" + total.getString("cart_goods_number") + ")");
                            try
                            {
                                buyNum = Integer.parseInt(total.getString("cart_goods_number"));
                            }
                            catch (Exception e)
                            {
                                Log.e("selectNumber", "e: " + e.toString());
                            }
                        }
                    } catch (Exception e)
                    {
                        Log.e("initData,cart", e.toString());
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
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        if (!hidden)
        {
            if (LoginUtil.getLogin())
            {
                noDataLayout.setVisibility(View.GONE);
                hasDataLayout.setVisibility(View.VISIBLE);
                initData();
            }
            else
            {
                noDataLayout.setVisibility(View.VISIBLE);
                hasDataLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void deleteCart()
    {
        if (LoginUtil.getLogin())
        {
            noDataLayout.setVisibility(View.GONE);
            hasDataLayout.setVisibility(View.VISIBLE);
            initData();
        }
        else
        {
            noDataLayout.setVisibility(View.VISIBLE);
            hasDataLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void selectCallBack(List<String> ids, String status)
    {
        cartLabelCount(status, ids);
    }

    @Override
    public void selectNumber(String num, String price)
    {
        try
        {
            buyNum = Integer.parseInt(num);
        }
        catch (Exception e)
        {
            Log.e("selectNumber", "e: " + e.toString());
        }
        buyText.setText("去结算(" + num + ")");
        totalPrice.setText(price);
    }

    @Override
    public void showAllSelect(boolean all)
    {
        allBox.setChecked(all);
    }

    @Override
    public void deleteIdCallBack(List<String> ids)
    {
        IDS.clear();
        IDS.addAll(ids);
    }

    private void cartLabelCount(String status, List<String> recId)
    {
        StringBuffer buffer = new StringBuffer();
        if (recId.size() > 0)
        {
            for (String id : recId)
            {
                buffer.append(id).append(",");
            }
        }
        else
        {
            buffer.append("");
        }
        try {
            AjaxParams params = new AjaxParams();
            params.put("type", "2");
            params.put("rec_id", buffer.toString());
            params.put("cart_id", buffer.toString());
            params.put("status", status);
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=cart&a=getcart_label_count", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String cartNum = object.getString("cart_number");
                        String price = object.getString("goods_amount");
                        buyText.setText("去结算(" + cartNum + ")");
                        totalPrice.setText(price);
                        try
                        {
                            buyNum = Integer.parseInt(cartNum);
                        }
                        catch (Exception e)
                        {
                            Log.e("cartLabelCountcart", "e: " + e.toString());
                        }

                    } catch (Exception e)
                    {
                        Log.e("cartLabelCount", e.toString());
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

    private void deleteCartGood(List<String> ids)
    {
        StringBuffer buffer = new StringBuffer();
        for (String id : ids)
        {
            buffer.append(id).append(",");
        }
        try {
            AjaxParams params = new AjaxParams();
            params.put("id", buffer.toString());
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=cart&a=getdrop_goods", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        if ("0".equals(error))
                        {
                            initData();
                        }

                    } catch (Exception e)
                    {
                        Log.e("deleteCartGood", e.toString());
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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buy_text:
                if (buyNum > 0)
                {
                    Intent order = new Intent(getContext(), SubmitOrderActivity.class);
                    startActivityForResult(order, 1);
                }
                else
                {
                    ToastUtils.s(getContext(), "您未勾选商品");
                }
                break;
            case R.id.edit_text:
                if (cartBeans.size() > 0)
                {
                    if ("编辑".equals(editTv.getText()))
                    {
                        editTv.setText("完成");
                        buyLayout.setVisibility(View.GONE);
                        editLayout.setVisibility(View.VISIBLE);
                    } else if ("完成".equals(editTv.getText()))
                    {
                        editTv.setText("编辑");
                        buyLayout.setVisibility(View.VISIBLE);
                        editLayout.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.delete_text:
                if (IDS.size() > 0)
                {
                    deleteCartGood(IDS);
                }
                else
                {
                    ToastUtils.s(getContext(), "至少选中一个商品");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1)
        {
            initData();
        }
    }
}
