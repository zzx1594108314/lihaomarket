package com.lihao.market.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.R;

import java.util.List;

public class OrderListAdapter extends BaseAdapter
{
    private Context mContext;

    private List<GoodBean> mData;

    private ViewHolder holder;

    public OrderListAdapter(Context context)
    {
        this.mContext = context;
    }

    @Override
    public int getCount()
    {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_good_list_item, null);
            holder.imageView = convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(mData.get(position).getGoods_thumb())
                .apply(new RequestOptions().centerCrop()).into(holder.imageView);
        return convertView;
    }

    private class ViewHolder
    {
        private ImageView imageView;
    }

    public void setData(List<GoodBean> data)
    {
        if (mData != null)
        {
            mData.clear();
        }
        this.mData = data;
        notifyDataSetChanged();
    }
}
