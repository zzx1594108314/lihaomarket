package com.lihao.market.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.lihao.market.Activity.AddressListActivity;
import com.lihao.market.Activity.ConfirmOrderActivity;
import com.lihao.market.Activity.SettingInfoActivity;
import com.lihao.market.Adapter.MerchantPagerAdapter;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.Bean.JoinCartBean;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Custom.ContinueSlideScrollView;
import com.lihao.market.Dialog.KindDialog;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductAboveFragment extends BaseFragment implements View.OnClickListener, KindDialog.NumTypeCallback
{
    private ViewPager viewPager;

    private TextView numTv;

    private TextView nameTv;

    private TextView priceTv;

    private TextView scoreTv;

    private TextView saleTv;

    private TextView stockTv;

    private TextView categoryTv;

    private TextView addressTv;

    private RelativeLayout addressLayout;

    private RelativeLayout kindLayout;

    private MerchantPagerAdapter pagerAdapter;

    private ContinueSlideScrollView slideScrollView;

    private ContinueSlideScrollView.onContinueSlide continueSlide;

    private GoodBean mGoodBean;

    private String typeId;

    private String num = "1";

    private CartNumListener cartNumListener;

    public ProductAboveFragment(CartNumListener listener)
    {
        cartNumListener = listener;
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
        View view = inflater.inflate(R.layout.fragment_product_above, container, false);

        initView(view);

        IntentFilter filter = new IntentFilter("com.broadcast.addressupdate");
        BroadCastManager.getInstance().registerReceiver(getActivity(), addressReceiver, filter);

        return view;
    }

    private void initView(View view)
    {
        slideScrollView = view.findViewById(R.id.slide_scrollview_above);
        slideScrollView.setonContinueSlideListener(continueSlide);
        viewPager = view.findViewById(R.id.view_pager);
        nameTv = view.findViewById(R.id.name);
        numTv = view.findViewById(R.id.num);
        priceTv = view.findViewById(R.id.price);
        scoreTv = view.findViewById(R.id.score);
        saleTv = view.findViewById(R.id.sale_num);
        stockTv = view.findViewById(R.id.stock_num);
        categoryTv = view.findViewById(R.id.category);
        kindLayout = view.findViewById(R.id.kind_layout);
        addressTv = view.findViewById(R.id.address);
        addressLayout = view.findViewById(R.id.address_layout);

        kindLayout.setOnClickListener(this);
        addressLayout.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                numTv.setText(position + 1 + "/" + mGoodBean.getPicLists().size());
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    public void setContinueSlideScrollView(ContinueSlideScrollView.onContinueSlide slide)
    {
        this.continueSlide = slide;
    }

    public void setGoodData(GoodBean bean)
    {
        this.mGoodBean = bean;
        pagerAdapter = new MerchantPagerAdapter(bean.getPicLists(), getContext());
        nameTv.setText(bean.getGoods_name());
        stockTv.setText(bean.getGoods_number());
        priceTv.setText("¥" + bean.getShop_price());
        String address = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ADDRESS);
        if (TextUtils.isEmpty(address))
        {
            addressTv.setText("未设置地址");
        }
        else
        {
            addressTv.setText(address);
        }


        if (bean.getIntegral() == null || TextUtils.isEmpty(bean.getIntegral()) || "0".equals(bean.getIntegral()))
        {
            scoreTv.setVisibility(View.GONE);
        }
        else
        {
//            scoreTv.setText("可用积分" + bean.getIntegral());
            scoreTv.setText("可用积分");
            scoreTv.setVisibility(View.VISIBLE);
        }
        numTv.setText(1 + "/" + bean.getPicLists().size());
        saleTv.setText(bean.getSales_volume());
        if (bean.getKindBeans().size() > 0)
        {
            categoryTv.setText(bean.getKindBeans().get(0).getLabel() + ",1");
            typeId = bean.getKindBeans().get(0).getId();
            num = "1";
        }
        viewPager.setAdapter(pagerAdapter);
    }

    private void showDialog(String flag)
    {
        final KindDialog dialog = new KindDialog(getContext(), mGoodBean, this, flag);
        dialog.show();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.kind_layout:
                showDialog("0");
                break;
            case R.id.address_layout:
                if (LoginUtil.getLogin())
                {
                    Intent address = new Intent(getContext(), AddressListActivity.class);
                    startActivity(address);
                }
                else
                {
                    ToastUtils.s(getContext(), "您未登录，请先登录");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void getInfo(String type, String num, String typeId, String flag)
    {
        categoryTv.setText(type + "，" + num);
        this.typeId = typeId;
        this.num = num;
        if ("1".equals(flag))
        {
            Intent order = new Intent(getContext(), ConfirmOrderActivity.class);
            order.putExtra("typeId", typeId);
            order.putExtra("num", num);
            order.putExtra("goodId", mGoodBean.getGoods_id());
            startActivity(order);
        }
        else if ("2".equals(flag))
        {
            joinCart();
        }
    }

    public String getTypeId()
    {
        return typeId;
    }

    public String getNum()
    {
        return num;
    }

    public void showBuyOrCart(String flag)
    {
        showDialog(flag);
    }

    private void joinCart()
    {
        if (!LoginUtil.getLogin())
        {
            ToastUtils.s(getContext(), "未登录，请先登录");
            return;
        }
        AjaxParams params = new AjaxParams();
        try {
            List<String> type = new ArrayList<>();
            type.add(typeId);

            JoinCartBean bean = new JoinCartBean();
            bean.setWarehouse_id("0");
            bean.setArea_id("0");
            bean.setQuick(1);
            bean.setSpec(type);
            bean.setGoods_id(Integer.parseInt(mGoodBean.getGoods_id()));
            bean.setStore_id(0);
            bean.setNumber(num);
            bean.setParent(0);
            params.put("goods", new Gson().toJson(bean));
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=cart&a=getadd_to_cart", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        String good_number = object.getString("goods_number");
                        if ("0".equals(error))
                        {
                            ToastUtils.s(getContext(), "商品已加入购物车");
                            cartNumListener.getCartNum();
                        }

                    } catch (Exception e)
                    {
                        Log.e("joinCart", e.toString());
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

    public interface CartNumListener
    {
        void getCartNum();
    }

    private BroadcastReceiver addressReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String address = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ADDRESS);
            if (TextUtils.isEmpty(address))
            {
                addressTv.setText("未设置地址");
            }
            else
            {
                addressTv.setText(address);
            }
        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        BroadCastManager.getInstance().unregisterReceiver(getActivity(), addressReceiver);
    }
}
