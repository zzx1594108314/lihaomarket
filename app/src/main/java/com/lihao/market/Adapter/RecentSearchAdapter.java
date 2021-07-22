package com.lihao.market.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.lihao.market.Activity.ProductListActivity;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;

import java.util.List;

public class RecentSearchAdapter extends BaseAdapter
{
    private Context mContext;

    private List<String> mData;

    private ViewHolder holder;

    public RecentSearchAdapter(Context context)
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recent_search_item_layout, null);
            holder.nameTv = convertView.findViewById(R.id.name);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameTv.setText(mData.get(position));

        holder.nameTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, ProductListActivity.class);
                intent.putExtra(KeySet.TITLE, "搜索");
                intent.putExtra(KeySet.FROM_SEARCH, true);
                intent.putExtra(KeySet.KEYWORD, mData.get(position));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public class ViewHolder
    {
        private TextView nameTv;
    }

    public void setData(List<String> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }
}
