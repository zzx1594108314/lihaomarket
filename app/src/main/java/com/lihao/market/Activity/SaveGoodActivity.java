package com.lihao.market.Activity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Adapter.SaveGoodAdapter;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Bean.ProductBean;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SaveGoodActivity extends BaseActivity implements SaveGoodAdapter.DeleteListener
{
    private TextView backTv;

    private TextView titleTv;

    private RecyclerView recyclerView;

    private List<ProductBean> productBeans = new ArrayList<>();

    private SaveGoodAdapter adapter;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_save_good;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new SaveGoodAdapter(this, this);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData()
    {
        titleTv.setText("我的收藏");
        querySaveGood();
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void querySaveGood()
    {
        showLoadingDialog();
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&a=getcollectionlist", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        JSONObject guestList = object.getJSONObject("goods_list");
                        Iterator<String> it = guestList.keys();
                        while (it.hasNext())
                        {
                            JSONObject guest = guestList.getJSONObject(it.next());
                            ProductBean productBean = new ProductBean();
                            productBean.setGoods_id(guest.getString("goods_id"));
                            productBean.setGoods_name(guest.getString("goods_name"));
                            productBean.setImg(guest.getString("goods_thumb"));
                            productBean.setShop_price(guest.getString("shop_price"));
                            productBean.setRec_id(guest.getString("rec_id"));
                            productBeans.add(productBean);
                        }
                        adapter.setData(productBeans);

                    } catch (Exception e)
                    {
                        Log.e("querySaveGood", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg)
                {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete()
    {
        productBeans.clear();
        querySaveGood();
    }
}
