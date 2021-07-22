package com.lihao.market.Activity;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Adapter.BrandListAdapter;
import com.lihao.market.Adapter.ProductListAdapter;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Bean.BrandBean;
import com.lihao.market.Bean.ProductBean;
import com.lihao.market.Custom.PullToRefreshView;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bar.DoubleHeadedDragonBar;

/**
 * 产品列表
 */
public class ProductListActivity extends BaseActivity implements PullToRefreshView.OnFooterRefreshListener, View.OnClickListener, BrandListAdapter.BrandListener
{
    private TextView titleTv;

    private TextView backTv;

    private LinearLayout searchLayout;

    private TextView selectTv;

    private TextView allTextTv;

    private ImageView allImage;

    private RelativeLayout allLayout;

    private TextView newTextTv;

    private TextView saleTextTv;

    private TextView priceTextTv;

    private ImageView priceImage;

    private RelativeLayout priceLayout;

    private ImageView sortImage;

    private GridView gridView;

    private PullToRefreshView pullToRefreshView;

    private DrawerLayout mDrawer;

    private LinearLayout closeLayout;

    private Switch mSwitch;

    private TextView hasGood;

    private TextView promotionTv;

    private DoubleHeadedDragonBar dragonBar;

    private TextView roomTv;

    private TextView brandTv;

    private RecyclerView mRecyclewView;

    private TextView clearTv;

    private TextView confirmTv;

    private LinearLayout emptyLayout;

    private List<ProductBean> productBeanList = new ArrayList<>();

    private ProductListAdapter adapter;

    private BrandListAdapter brandListAdapter;

    private int index = 1;

    private String startPage = "1";

    private String id = "&id=0";

    private String sortType = KeySet.SORT_TYPE_1;

    private String orderType = KeySet.ORDER_TYPE_1;

    private boolean isALL = true;

    private boolean isPrice = true;

    //仅看有货
    private boolean isGoods = true;
    //促销
    private boolean isPromotion = true;
    //品牌全部
    private boolean isBrandAll = true;

    //是否有更多
    private boolean hasMore = true;

    //是否自营0：非自营1：自营
    private String isself = "0";

    private String price_min = "0";

    private String price_max = "0";

    //品牌，默认全部
    private String brand = "";

    //1：仅看有货
    private String hasgoods = "0";

    //1:促销
    private String promotion = "0";

    //关键词
    private String keyword = "";

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_product_list;
    }

    @Override
    public void initView()
    {
        titleTv = findViewById(R.id.tv_title);
        backTv = findViewById(R.id.tv_back);
        searchLayout = findViewById(R.id.search);
        selectTv = findViewById(R.id.select);
        allTextTv = findViewById(R.id.all_text);
        allImage = findViewById(R.id.all_image);
        allLayout = findViewById(R.id.all_layout);
        newTextTv = findViewById(R.id.new_product);
        saleTextTv = findViewById(R.id.sale_text);
        priceTextTv = findViewById(R.id.price_text);
        priceImage = findViewById(R.id.price_image);
        priceLayout = findViewById(R.id.price_layout);
        sortImage = findViewById(R.id.sort_image);
        gridView = findViewById(R.id.product_view);
        pullToRefreshView = findViewById(R.id.system_main_pull_refresh_view);
        mDrawer = findViewById(R.id.drawer);
        mSwitch = findViewById(R.id.tip_switch_disturb);
        closeLayout = findViewById(R.id.close);
        hasGood = findViewById(R.id.has_good);
        promotionTv = findViewById(R.id.promotion);
        dragonBar = findViewById(R.id.seek_bar);
        brandTv = findViewById(R.id.brand);
        mRecyclewView = findViewById(R.id.recycler);
        clearTv = findViewById(R.id.clear);
        confirmTv = findViewById(R.id.confirm);
        roomTv = findViewById(R.id.room);
        emptyLayout = findViewById(R.id.empty_layout);

        adapter = new ProductListAdapter(this);
        gridView.setAdapter(adapter);

        brandListAdapter = new BrandListAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclewView.setLayoutManager(layoutManager);
        mRecyclewView.setAdapter(brandListAdapter);

        pullToRefreshView.setOnFooterRefreshListener(this);
        pullToRefreshView.setHeadEndber(false);
    }

    @Override
    public void initData()
    {
        Intent intent = getIntent();
        if (intent != null)
        {
            titleTv.setText(intent.getStringExtra(KeySet.TITLE));
            boolean fromSearch = intent.getBooleanExtra(KeySet.FROM_SEARCH, false);
            if (!fromSearch)
            {
                String url = intent.getStringExtra(KeySet.GOOD_URL);
                id = url.substring(url.indexOf("&id="));
                getBrands();
            }
            if (fromSearch)
            {
                selectTv.setVisibility(View.GONE);
                keyword = intent.getStringExtra(KeySet.KEYWORD);
            }
            else
            {
                selectTv.setVisibility(View.VISIBLE);
            }
            getProductList(startPage, id, sortType, orderType, true);

        }
        dragonBar.setUnit("0", "10000");//解析价格字段
        dragonBar.setCallBack(new DoubleHeadedDragonBar.DhdBarCallBack()
        {
            @Override
            public String getMinMaxString(int value, int value1)
            {
                return super.getMinMaxString(value, value1);
            }

            @Override
            public void onEndTouch(float minPercentage, float maxPercentage)
            {
                String start = String.valueOf(10000 / 100 * (int) minPercentage);
                String end = String.valueOf(10000 / 100 * (int) maxPercentage);
                price_min = start;
                price_max = end;
                roomTv.setText(start + "~" + end);
                super.onEndTouch(minPercentage, maxPercentage);
            }
        });

    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        allLayout.setOnClickListener(this);
        newTextTv.setOnClickListener(this);
        saleTextTv.setOnClickListener(this);
        priceLayout.setOnClickListener(this);
        selectTv.setOnClickListener(this);
        closeLayout.setOnClickListener(this);
        hasGood.setOnClickListener(this);
        promotionTv.setOnClickListener(this);
        brandTv.setOnClickListener(this);
        clearTv.setOnClickListener(this);
        confirmTv.setOnClickListener(this);
        searchLayout.setOnClickListener(this);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    isself = "1";
                }
                else
                {
                    isself = "0";
                }
            }
        });
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view)
    {
        if (hasMore)
        {
            index += 1;
            getProductList(String.valueOf(index), id, sortType, orderType, false);
        }
        else
        {
            pullToRefreshView.onFooterRefreshComplete();
            ToastUtils.s(this,"已到底啦");
        }
    }

    private void getProductList(String page, String id, String sortType, String orderType, boolean sort)
    {
        showLoadingDialog();
        if (!(this.sortType.equals(sortType)) || sort)
        {
            productBeanList.clear();
        }
        this.sortType = sortType;
        AjaxParams params = new AjaxParams();
        try {
            params.put("page", page);
            params.put("size", "10");
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=category&a=getproducts" + id + "&sort=" + sortType + "&order=" + orderType
                    + "&keyword=" + Uri.encode(keyword) + "&isself=" + isself + "&hasgoods=" + hasgoods + "&promotion=" + promotion + "&price_min=" + price_min + "&price_max=" + price_max
                    + "&brand=" + brand, params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray oba = object.getJSONArray("list");

                        if (oba.length() < 10)
                        {
                            hasMore = false;
                        }
                        for (int i = 0; i < oba.length(); i++)
                        {
                            JSONObject op = oba.getJSONObject(i);
                            ProductBean productBean = new ProductBean();
                            productBean.setGoods_id(op.getString("goods_id"));
                            productBean.setGoods_name(op.getString("goods_name"));
                            productBean.setSale(op.getString("sales_volume"));
                            productBean.setImg(op.getString("goods_img"));
                            productBean.setShop_price(op.getString("shop_price"));
                            productBean.setStock(op.getString("goods_number"));

                            JSONObject jsonObject = op.getJSONObject("spe");
                            Iterator<String> it = jsonObject.keys();
                            String key = it.next();
                            JSONObject json = jsonObject.getJSONObject(key);
                            JSONArray jsonArray = json.getJSONArray("values");
                            JSONObject json2 = jsonArray.getJSONObject(0);
                            productBean.setAttr_id(json2.getString("id"));

                            productBeanList.add(productBean);

                        }
                        adapter.setData(productBeanList);
                        if (productBeanList.size() > 0)
                        {
                            mDrawer.setVisibility(View.VISIBLE);
                            emptyLayout.setVisibility(View.GONE);
                        }
                        else
                        {
                            mDrawer.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }

                        pullToRefreshView.onFooterRefreshComplete();

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

    /**
     * 获取品牌
     */
    private void getBrands()
    {
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=category&a=getbrandinfo" + id, params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        List<BrandBean> brandBeans = new ArrayList<>();
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray oba = object.getJSONArray("brands");
                        for (int i = 0; i < oba.length(); i++)
                        {
                            JSONObject op = oba.getJSONObject(i);
                            BrandBean brandBean = new BrandBean();
                            brandBean.setId(op.getString("brand_id"));
                            brandBean.setBrandName(op.getString("brand_name"));
                            brandBeans.add(brandBean);
                        }
                        brandListAdapter.setData(brandBeans);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.all_layout:
                if (isALL)
                {
                    getProductList(startPage, id, KeySet.SORT_TYPE_4, KeySet.ORDER_TYPE_2, true);
                    allImage.setBackgroundResource(R.mipmap.ic_down_red);
                    isALL = false;
                    orderType = KeySet.ORDER_TYPE_2;
                }
                else
                {
                    getProductList(startPage, id, KeySet.SORT_TYPE_4, KeySet.ORDER_TYPE_1, true);
                    allImage.setBackgroundResource(R.mipmap.ic_up_red);
                    isALL = true;
                    orderType = KeySet.ORDER_TYPE_1;
                }
                allTextTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                newTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                saleTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                priceTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                priceImage.setBackgroundResource(R.mipmap.ic_down_black);
                isPrice = true;
                hasMore = true;
                break;
            case R.id.new_product:
                isPrice = true;
                isALL = true;
                getProductList(startPage, id, KeySet.SORT_TYPE_1, KeySet.ORDER_TYPE_1, true);
                allTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                newTextTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                saleTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                priceTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                priceImage.setBackgroundResource(R.mipmap.ic_down_black);
                allImage.setBackgroundResource(R.mipmap.ic_down_black);
                orderType = KeySet.ORDER_TYPE_1;
                hasMore = true;
                break;
            case R.id.sale_text:
                isALL = true;
                isPrice = true;
                getProductList(startPage, id, KeySet.SORT_TYPE_3, KeySet.ORDER_TYPE_2, true);
                allTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                newTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                saleTextTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                priceTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                priceImage.setBackgroundResource(R.mipmap.ic_down_black);
                allImage.setBackgroundResource(R.mipmap.ic_down_black);
                orderType = KeySet.ORDER_TYPE_2;
                hasMore = true;
                break;
            case R.id.price_layout:
                isALL = true;
                if (isPrice)
                {
                    getProductList(startPage, id, KeySet.SORT_TYPE_2, KeySet.ORDER_TYPE_2, true);
                    priceImage.setBackgroundResource(R.mipmap.ic_down_red);
                    isPrice = false;
                    orderType = KeySet.ORDER_TYPE_2;
                }
                else
                {
                    getProductList(startPage, id, KeySet.SORT_TYPE_2, KeySet.ORDER_TYPE_1, true);
                    priceImage.setBackgroundResource(R.mipmap.ic_up_red);
                    isPrice = true;
                    orderType = KeySet.ORDER_TYPE_1;
                }
                allTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                newTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                saleTextTv.setTextColor(getResources().getColor(R.color.sort_unselect_title_text));
                priceTextTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                allImage.setBackgroundResource(R.mipmap.ic_down_black);
                isALL = true;
                hasMore = true;
                break;
            case R.id.select:
                mDrawer.openDrawer(GravityCompat.END);
                break;
            case R.id.close:
                mDrawer.closeDrawer(GravityCompat.END);
                break;
            case R.id.has_good:
                if (isGoods)
                {
                    hasGood.setBackgroundResource(R.drawable.text_squire_red);
                    hasGood.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                    isGoods = false;
                    hasgoods = "1";
                }
                else
                {
                    hasGood.setBackgroundResource(R.drawable.text_squire_grey);
                    hasGood.setTextColor(getResources().getColor(R.color.current_time_text));
                    isGoods = true;
                    hasgoods = "0";
                }
                break;
            case R.id.promotion:
                if (isPromotion)
                {
                    promotionTv.setBackgroundResource(R.drawable.text_squire_red);
                    promotionTv.setTextColor(getResources().getColor(R.color.sort_select_title_text));
                    isPromotion = false;
                    promotion = "1";
                }
                else
                {
                    promotionTv.setBackgroundResource(R.drawable.text_squire_grey);
                    promotionTv.setTextColor(getResources().getColor(R.color.current_time_text));
                    isPromotion = true;
                    promotion = "0";
                }
                break;
            case R.id.brand:
                if (isBrandAll)
                {
                    mRecyclewView.setVisibility(View.VISIBLE);
                    isBrandAll = false;
                }
                else
                {
                    mRecyclewView.setVisibility(View.GONE);
                    isBrandAll = true;
                }
                break;
            case R.id.clear:
                mSwitch.setChecked(false);
                isself = "0";

                hasGood.setBackgroundResource(R.drawable.text_squire_grey);
                hasGood.setTextColor(getResources().getColor(R.color.current_time_text));
                isGoods = true;
                hasgoods = "0";

                promotionTv.setBackgroundResource(R.drawable.text_squire_grey);
                promotionTv.setTextColor(getResources().getColor(R.color.current_time_text));
                isPromotion = true;
                promotion = "0";

                dragonBar.setMinValue(0);
                dragonBar.setMaxValue(100);
                roomTv.setText("0~10000");
                price_min = "0";
                price_max = "0";

                brandListAdapter.restoreData();
                brand = "";

                break;
            case R.id.confirm:
                hasMore = true;
                getProductList(startPage, id, sortType, orderType, true);
                mDrawer.closeDrawer(GravityCompat.END);
                break;
            case R.id.search:
                Intent cart = new Intent(ProductListActivity.this, HomeActivity.class);
                cart.putExtra("fragment_flag", 3);
                startActivity(cart);
                break;
            default:
                break;
        }
    }

    @Override
    public void setBrandInfo(List<BrandBean> info)
    {
        if (info != null)
        {
            brand = "";
            if (info.size() == 1)
            {
                brand = info.get(0).getId();
                brandTv.setText(info.get(0).getBrandName());
            }
            else
            {
                StringBuffer buffer = new StringBuffer();
                StringBuffer buffer1 = new StringBuffer();
                for (int i = 0; i < info.size(); i++)
                {
                    if (i < info.size() - 1)
                    {
                        buffer.append(info.get(i).getId()).append(",");
                        buffer1.append(info.get(i).getBrandName()).append(",");
//                        brand += info.get(i).getId() + "%2c";
//                        brandName += info.get(i).getBrandName() + ",";
                    }
                    else
                    {
                        buffer.append(info.get(i).getId());
                        buffer1.append(info.get(i).getBrandName());
//                        brand += info.get(i).getId();
//                        brandName += info.get(i).getBrandName();
                    }
                }
                brand = buffer.toString();
                brandTv.setText(buffer1.toString());
            }
        }
    }
}
