package com.lihao.market.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.NavUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Activity.ProductListActivity;
import com.lihao.market.Bean.ItemBean;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.ToolUtils;

import java.util.List;

public class ProductRecAdapter extends BaseAdapter
{
    private Context mContext;

    private ViewHolder holder;

    private List<ItemBean> mDatas;

    public ProductRecAdapter(Context context)
    {
        this.mContext = context;
    }

    @Override
    public int getCount()
    {
        return mDatas == null ? 0 : mDatas.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recommend_item_layout, null);
            holder.nameTv = convertView.findViewById(R.id.name);
            holder.product = convertView.findViewById(R.id.product);
            holder.linearLayout = convertView.findViewById(R.id.layout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (ToolUtils.getScreenWidth(mContext) / 4.5f),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            holder.linearLayout.setLayoutParams(params);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(mDatas.get(position).getImg())
                .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_small_common).error(R.mipmap.ic_small_common)).into(holder.product);
        holder.nameTv.setText(mDatas.get(position).getDesc());
        holder.linearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, ProductListActivity.class);
                intent.putExtra(KeySet.GOOD_URL, mDatas.get(position).getUrl());
                intent.putExtra(KeySet.TITLE, mDatas.get(position).getDesc());
                intent.putExtra(KeySet.FROM_SEARCH, false);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public void setData(List<ItemBean> data)
    {
        mDatas = data;
        notifyDataSetChanged();
    }


    public class ViewHolder
    {
        private ImageView product;

        private TextView nameTv;

        private LinearLayout linearLayout;

    }

}
