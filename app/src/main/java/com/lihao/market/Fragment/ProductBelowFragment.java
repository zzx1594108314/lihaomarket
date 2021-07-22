package com.lihao.market.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lihao.market.Adapter.MyFragmentPagerAdapter;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.Custom.ContinueSlideScrollView;
import com.lihao.market.R;

import java.util.ArrayList;
import java.util.List;

public class ProductBelowFragment extends BaseFragment
{
    private TabLayout mTablayout;

    private ViewPager mViewPager;

    private String[] mTitles = {"商品详情", "规格参数"};

    private MyFragmentPagerAdapter mAdapter;

    private ContinueSlideScrollView.onContinueSlide continueSlide;

    private ContinueSlideScrollView slideScrollView;

    private ProductBelowAFragment aFragment;

    private ProductBelowBFragment bFragment;

    public ProductBelowFragment()
    {
        aFragment = new ProductBelowAFragment();
        bFragment = new ProductBelowBFragment();
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
        View view = inflater.inflate(R.layout.fragment_product_below, container, false);

        mTablayout = view.findViewById(R.id.tab_collect_layout);
        mViewPager = view.findViewById(R.id.vp_collect);
        slideScrollView = view.findViewById(R.id.slide_scrollview_below);
        slideScrollView.setonContinueSlideListener(continueSlide);

        addTabToTabLayout();
        setupWIthViewPager();

        return view;
    }

    private void setupWIthViewPager() {
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(aFragment);
        mFragments.add(bFragment);

        mAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), mTitles, mFragments);

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

    public void setContinueSlideScrollView(ContinueSlideScrollView.onContinueSlide slide)
    {
        this.continueSlide = slide;
    }

    public void setGoodData(GoodBean bean)
    {
        if (aFragment != null)
        {
            aFragment.setGoodData(bean);
        }
    }
}
