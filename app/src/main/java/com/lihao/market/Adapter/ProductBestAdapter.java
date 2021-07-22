package com.lihao.market.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Bean.ItemBean;
import com.lihao.market.R;
import com.lihao.market.Util.ToolUtils;

import java.util.List;

/**
 * 精品展示
 */
public class ProductBestAdapter extends BaseAdapter
{
    private Context mContext;

    private List<ItemBean> mData;

    private ViewHolder holder;

    public ProductBestAdapter(Context context)
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.best_item_layout, null);
            holder.imageView = convertView.findViewById(R.id.image);
            holder.layout = convertView.findViewById(R.id.layout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (ToolUtils.getScreenWidth(mContext) / 3f),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            holder.layout.setLayoutParams(params);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(mData.get(position).getImg())
                .apply(new RequestOptions().centerCrop()).into(holder.imageView);
        return convertView;
    }

    public class ViewHolder
    {
        private ImageView imageView;

        private LinearLayout layout;
    }

    public void setData(List<ItemBean> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }
}
