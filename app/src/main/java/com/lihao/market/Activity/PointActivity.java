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

public class PointActivity extends BaseActivity
{
    private TextView backTv;

    private TextView titleTv;

    private RecyclerView mRecyclerView;

    private PointRecordAdapter adapter;

    private List<PointBean> pointBeans = new ArrayList<>();

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_point;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        mRecyclerView = findViewById(R.id.recyclerview);

        adapter = new PointRecordAdapter(this, "0");
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void initData()
    {
        titleTv.setText("我的积分");
        queryPointList();
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

    private void queryPointList()
    {
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=account&a=GetPayPoints", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray array = object.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++)
                        {
                            JSONObject jsonObject = array.getJSONObject(i);
                            PointBean bean = new PointBean();
                            bean.setLog_id(jsonObject.getString("log_id"));
                            bean.setChange_desc(jsonObject.getString("change_desc"));
                            bean.setChange_time(jsonObject.getString("change_time"));
                            bean.setType(jsonObject.getString("type"));
                            bean.setPay_points(jsonObject.getString("pay_points"));
                            pointBeans.add(bean);
                        }
                        adapter.setData(pointBeans);

                    } catch (Exception e)
                    {
                        Log.e("queryPointList", e.toString());
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
