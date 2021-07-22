package com.lihao.market.Adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * ViewPager适配器
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter
{
    private String[] titles;

    private List<Fragment> fragments;

    /**
     * 构造函数
     */
    public MyFragmentPagerAdapter(FragmentManager fm, String[] titles, List<Fragment> fragments)
    {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i)
    {
        return fragments.get(i);
    }

    @Override
    public int getCount()
    {
        return titles.length;
    }

    public CharSequence getPageTitle(int position)
    {
        return titles[position];
    }

    //自定义一个添加title和fragment的方法，供Activity使用
    public void addTitlesAndFragments(String[] titles, List<Fragment> fragments)
    {
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public long getItemId(int position)
    {
        return fragments.get(position).hashCode();
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
    }
}
