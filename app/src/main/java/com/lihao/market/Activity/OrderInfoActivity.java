package com.lihao.market.Activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lihao.market.Adapter.MyFragmentPagerAdapter;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Fragment.AllOrderFragment;
import com.lihao.market.Fragment.ReadyBuyFragment;
import com.lihao.market.Fragment.ReadyConfirmFragment;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单信息
 */
public class OrderInfoActivity extends BaseActivity implements View.OnClickListener
{
    private TextView tv_title;

    private TextView back;

    private TabLayout mTablayout;

    private ViewPager mViewPager;

    private String[] mTitles;

    private MyFragmentPagerAdapter mAdapter;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_order_info;
    }

    @Override
    public void initView()
    {
        mTablayout = findViewById(R.id.tab_collect_layout);
        mViewPager = findViewById(R.id.vp_collect);
        back = findViewById(R.id.tv_back);
        tv_title = findViewById(R.id.tv_title);
    }

    @Override
    public void initData()
    {
        tv_title.setText("订单信息");
        Intent intent = getIntent();
        if (intent != null)
        {
            String index = intent.getStringExtra(KeySet.ORDER_INDEX);
            queryTitleNum(index);
        }
    }

    @Override
    public void initListener()
    {
        back.setOnClickListener(this);
    }

    /*
     * 初始化FragmentPagerAdapter适配器并给ViewPager设置上该适配器，最后关联TabLayout和ViewPager
     */
    private void setupWIthViewPager()
    {
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new AllOrderFragment(this));
        mFragments.add(new ReadyBuyFragment(this));
        mFragments.add(new ReadyConfirmFragment(this));

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);

        mViewPager.setAdapter(mAdapter);
        mTablayout.setupWithViewPager(mViewPager);
    }

    /*添加tab */
    private void addTabToTabLayout()
    {

        for (int i = 0; i < mTitles.length; i++)
        {
            mTablayout.addTab(mTablayout.newTab().setText(mTitles[i]));
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
            default:
                break;
        }
    }

    private void queryTitleNum(final String index)
    {
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=order&a=title", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        JSONObject jsonObject = object.getJSONObject("order_num");
                        List<String> title = new ArrayList<>();
                        title.add("全部订单(" + jsonObject.getString("all_order") + ")");
                        title.add("待付款(" + jsonObject.getString("pay_count") + ")");
                        title.add("待收货(" + jsonObject.getString("confirmed_count") + ")");
                        mTitles = title.toArray(new String[title.size()]);
                        addTabToTabLayout();
                        setupWIthViewPager();
                        int position = Integer.parseInt(index);
                        mTablayout.getTabAt(position).select();
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

}
