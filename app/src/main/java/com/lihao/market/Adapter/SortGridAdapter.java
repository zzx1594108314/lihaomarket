package com.lihao.market.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Activity.ProductListActivity;
import com.lihao.market.Bean.SortBean;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;

import java.util.List;

public class SortGridAdapter extends BaseAdapter
{
    private Context mContext;

    private List<SortBean> mData;

    private ViewHolder holder;

    public SortGridAdapter(Context context)
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sort_product_item, null);
            holder.imageView = convertView.findViewById(R.id.image);
            holder.name = convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(mData.get(position).getCat_img())
                .apply(new RequestOptions().centerCrop()).into(holder.imageView);
        holder.name.setText(mData.get(position).getName());

        holder.imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, ProductListActivity.class);
                intent.putExtra(KeySet.GOOD_URL, mData.get(position).getUrl());
                intent.putExtra(KeySet.TITLE, mData.get(position).getName());
                intent.putExtra(KeySet.FROM_SEARCH, false);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public class ViewHolder
    {
        private ImageView imageView;

        private TextView name;
    }

    public void setData(List<SortBean> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }
}
