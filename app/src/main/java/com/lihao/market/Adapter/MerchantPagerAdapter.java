package com.lihao.market.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Bean.PicList;

import java.util.List;

public class MerchantPagerAdapter extends PagerAdapter
{
    private List<PicList> datas;

    private Context mContext;

    public MerchantPagerAdapter(List<PicList> data, Context context)
    {
        datas = data;
        mContext = context;
    }
    @Override
    public int getCount()
    {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o)
    {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        ImageView imageView = new ImageView(mContext);
        if (datas != null)
        {
            String path = datas.get(position).getImg_url();
            Glide.with(mContext).load(path).apply(new RequestOptions().centerCrop()).into(imageView);
        }
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((View) object);
    }


}
