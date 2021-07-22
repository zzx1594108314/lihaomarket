package com.lihao.market.Activity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Adapter.PointRecordAdapter;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Bean.PointBean;
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

public class AccountDetailActivity extends BaseActivity
{
    private TextView backTv;

    private TextView titleTv;

    private RecyclerView recyclerView;

    private PointRecordAdapter adapter;

    private List<PointBean> pointBeans = new ArrayList<>();

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_account_detail;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new PointRecordAdapter(this, "1");
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData()
    {
        titleTv.setText("账户明细");
        queryAccountDetail();
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

    private void queryAccountDetail()
    {
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=account&a=getdetail", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray array = object.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++)
                        {
                            PointBean bean = new PointBean();
                            JSONObject json = array.getJSONObject(i);
                            bean.setChange_time(json.getString("change_time"));
                            bean.setChange_desc(json.getString("change_desc"));
                            bean.setType(json.getString("type"));
                            bean.setPay_points(json.getString("amount"));
                            pointBeans.add(bean);
                        }
                        adapter.setData(pointBeans);

                    } catch (Exception e)
                    {
                        Log.e("queryAccountDetail", e.toString());
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
