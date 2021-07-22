package com.lihao.market.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.Bean.JoinCartBean;
import com.lihao.market.Bean.KindBean;
import com.lihao.market.Bean.PicList;
import com.lihao.market.Custom.ContinueSlideScrollView;
import com.lihao.market.Dialog.KindDialog;
import com.lihao.market.Fragment.ProductAboveFragment;
import com.lihao.market.Fragment.ProductBelowFragment;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProductDetailActivity extends BaseActivity implements View.OnClickListener, ProductAboveFragment.CartNumListener
{
    private TextView backTv;

    private TextView titleTv;

    private TextView shareTv;

    private FrameLayout frameLayout;

    private RelativeLayout productLayout;

    private TextView productTv;

    private View productView;

    private RelativeLayout detailLayout;

    private TextView detailTv;

    private View detailView;

    private RelativeLayout commentLayout;

    private TextView commentTv;

    private View commentView;

    private TextView joinCart;

    private TextView buyTv;

    private TextView cartNum;

    private LinearLayout cartLayout;

    private LinearLayout saveLayout;

    private ImageView saveImage;

    private LinearLayout kefuLayout;

    private ProductAboveFragment aboveFragment;

    private ProductBelowFragment belowFragment;

    private String id;

    private String attr_id;

    private boolean isSave = false;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_product_detail;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        shareTv = findViewById(R.id.right_tv);
        frameLayout = findViewById(R.id.framelayout);
        productLayout = findViewById(R.id.product_layout);
        detailLayout = findViewById(R.id.detail_layout);
        commentLayout = findViewById(R.id.comment_layout);
        productTv = findViewById(R.id.product_text);
        productView = findViewById(R.id.product_view);
        detailTv = findViewById(R.id.detail_text);
        detailView = findViewById(R.id.detail_view);
        commentTv = findViewById(R.id.comment_text);
        commentView = findViewById(R.id.comment_view);
        joinCart = findViewById(R.id.join_shop);
        buyTv = findViewById(R.id.buy);
        cartNum = findViewById(R.id.cart_num);
        cartLayout = findViewById(R.id.cart_layout);
        saveLayout = findViewById(R.id.save_layout);
        saveImage = findViewById(R.id.save_image);
        kefuLayout = findViewById(R.id.kefu_layout);

        aboveFragment = new ProductAboveFragment(this);
        belowFragment = new ProductBelowFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.framelayout, aboveFragment).commit();
    }

    @Override
    public void initData()
    {
        Intent intent = getIntent();
        if (intent != null)
        {
            id = intent.getStringExtra(KeySet.GOOD_URL);
            attr_id = intent.getStringExtra(KeySet.TYPE_ID);
            getProductDetail();
            queryGoodNum();
        }
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        shareTv.setOnClickListener(this);
        productLayout.setOnClickListener(this);
        detailLayout.setOnClickListener(this);
        commentLayout.setOnClickListener(this);
        joinCart.setOnClickListener(this);
        buyTv.setOnClickListener(this);
        cartLayout.setOnClickListener(this);
        saveLayout.setOnClickListener(this);
        kefuLayout.setOnClickListener(this);

        aboveFragment.setContinueSlideScrollView(new ContinueSlideScrollView.onContinueSlide()
        {
            @Override
            public void onContinueSlideTop()
            {

            }

            @Override
            public void onContinueSlideBottom()
            {
                changeToB();
            }
        });

        belowFragment.setContinueSlideScrollView(new ContinueSlideScrollView.onContinueSlide()
        {
            @Override
            public void onContinueSlideTop()
            {
                changeToA();
            }

            @Override
            public void onContinueSlideBottom()
            {

            }
        });

    }

    private void changeToB()
    {
        if (belowFragment.isAdded())
        {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_below_in,R.anim.slide_below_out).show(belowFragment).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_below_in,R.anim.slide_below_out)
                    .add(R.id.framelayout,belowFragment).commit();
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_above_in,R.anim.slide_above_out).show(belowFragment)
                .hide(aboveFragment).commit();

        detailTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
        detailView.setBackgroundColor(getResources().getColor(R.color.sort_select_title_text));
        productTv.setTextColor(getResources().getColor(R.color.text_un_select_color));
        productView.setBackgroundColor(getResources().getColor(R.color.sort_select_title));
    }

    private void changeToA()
    {
        if (aboveFragment.isAdded())
        {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_above_in,R.anim.slide_above_out)
                    .show(aboveFragment).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_above_in,R.anim.slide_above_out)
                    .add(R.id.framelayout,aboveFragment).commit();
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_below_in,R.anim.slide_below_out).show(aboveFragment)
                .hide(belowFragment).commit();
        productTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
        productView.setBackgroundColor(getResources().getColor(R.color.sort_select_title_text));
        detailTv.setTextColor(getResources().getColor(R.color.text_un_select_color));
        detailView.setBackgroundColor(getResources().getColor(R.color.sort_select_title));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.right_tv:
                break;
            case R.id.product_layout:
                changeToA();
                break;
            case R.id.detail_layout:
                changeToB();
                break;
            case R.id.comment_layout:
                break;
            case R.id.join_shop:
                if (LoginUtil.getLogin())
                {
                    aboveFragment.showBuyOrCart("2");
                }
                else
                {
                    ToastUtils.s(this, "您未登录，请先登录");
                }
                break;
            case R.id.buy:
                if (LoginUtil.getLogin())
                {
                    aboveFragment.showBuyOrCart("1");
                }
                else
                {
                    ToastUtils.s(this, "您未登录，请先登录");
                }
                break;
            case R.id.cart_layout:
                Intent cart = new Intent(ProductDetailActivity.this, HomeActivity.class);
                cart.putExtra("fragment_flag", 1);
                startActivity(cart);
                break;
            case R.id.save_layout:
                if (LoginUtil.getLogin())
                {
                    saveGood();
                }
                else
                {
                    ToastUtils.s(ProductDetailActivity.this, "请登录后收藏该商品");
                }
                break;
            case R.id.kefu_layout:
                if (LoginUtil.getLogin())
                {
                    String account = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ACCOUNT);
                    HashMap<String, String> info = new HashMap<>();
                    info.put("name", account);
                    info.put("tel", account);
                    Intent intent = new MQIntentBuilder(this).setCustomizedId(account).setClientInfo(info).build();
                    startActivity(intent);
                }
                else
                {
                    ToastUtils.s(ProductDetailActivity.this, "您未登录，请先登录");
                }
                break;
            default:
                break;
        }
    }

    private void getProductDetail()
    {
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=goods&a=getnewgoods&id=" + id + "&number=1", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        if (object.getString("error").equals("0"))
                        {
                            JSONObject jsonData = object.getJSONObject("goods");
                            GoodBean goodBean = new GoodBean();
                            goodBean.setGoods_id(jsonData.getString("goods_id"));
                            goodBean.setCat_id(jsonData.getString("cat_id"));
                            goodBean.setGoods_desc(jsonData.getString("goods_desc"));
                            goodBean.setGoods_img(jsonData.getString("goods_img"));
                            goodBean.setGoods_thumb(jsonData.getString("goods_thumb"));
                            goodBean.setGoods_name(jsonData.getString("goods_name"));
                            goodBean.setIntegral(jsonData.getString("integral"));
                            goodBean.setSales_volume(jsonData.getString("sales_volume"));
                            goodBean.setShop_price(jsonData.getString("shop_price"));
                            goodBean.setCollect_count(jsonData.getString("collect_count"));

                            if ("0".equals(jsonData.getString("collect_count")))
                            {
                                saveImage.setBackgroundResource(R.mipmap.ic_save);
                                isSave = false;
                            }
                            else
                            {
                                isSave = true;
                                saveImage.setBackgroundResource(R.mipmap.ic_saveed);
                            }

                            JSONArray array = jsonData.getJSONArray("pic");

                            List<PicList> picLists = new ArrayList<>();
                            PicList firstPic = new PicList();
                            firstPic.setImg_url(jsonData.getString("goods_img"));
                            picLists.add(firstPic);
                            if (array.length() > 0)
                            {
                                for (int i = 0; i < array.length(); i++)
                                {
                                    JSONObject op = array.getJSONObject(i);
                                    PicList picList = new PicList();
                                    picList.setImg_url(op.getString("img_url"));
                                    picLists.add(picList);
                                }
                            }
                            goodBean.setPicLists(picLists);

                            goodBean.setGoods_number(object.getString("attr_number"));
                            JSONObject jsonObject = object.getJSONObject("properties");
                            Iterator<String> it = jsonObject.keys();
                            String key = it.next();
                            goodBean.setProductKey(key);
                            JSONObject json = jsonObject.getJSONObject(key);
                            JSONArray jsonArray = json.getJSONArray("values");
                            List<KindBean> kindBeans = new ArrayList<>();
                            for (int j = 0; j < jsonArray.length(); j++)
                            {
                                JSONObject json2 = jsonArray.getJSONObject(j);
                                KindBean kindBean = new KindBean();
                                kindBean.setLabel(json2.getString("label"));
                                kindBean.setAttr_sort(json2.getString("attr_sort"));
                                kindBean.setId(json2.getString("id"));
                                kindBean.setGood_id(id);
                                kindBeans.add(kindBean);
                            }
                            goodBean.setKindBeans(kindBeans);

                            if (aboveFragment != null)
                            {
                                aboveFragment.setGoodData(goodBean);
                            }
                            if (belowFragment != null)
                            {
                                belowFragment.setGoodData(goodBean);
                            }
                        }

                    } catch (Exception e)
                    {
                        Log.e("getProductList", e.toString());
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

    private void queryGoodNum()
    {
        if (!LoginUtil.getLogin())
        {
            return;
        }
        AjaxParams params = new AjaxParams();
        try {

            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=cart&a=getgoodnumber", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        String goods_number = object.getString("goods_number");
                        if ("0".equals(error))
                        {
                            cartNum.setText(goods_number);
                        }

                    } catch (Exception e)
                    {
                        Log.e("queryGoodNum", e.toString());
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

    private void saveGood()
    {
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=goods&a=add_collection&id=" + id, params, new AjaxCallBack<Object>()
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
                            if (isSave)
                            {
                                saveImage.setBackgroundResource(R.mipmap.ic_save);
                                isSave = false;
                                ToastUtils.s(ProductDetailActivity.this, "已取消收藏");
                            }
                            else
                            {
                                saveImage.setBackgroundResource(R.mipmap.ic_saveed);
                                isSave = true;
                                ToastUtils.s(ProductDetailActivity.this, "已添加收藏");
                            }
                        }

                    } catch (Exception e)
                    {
                        Log.e("saveGood", e.toString());
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
    public void getCartNum()
    {
        queryGoodNum();
    }
}
