package com.lihao.market.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Activity.ProductDetailActivity;
import com.lihao.market.Bean.ItemBean;
import com.lihao.market.Bean.ProductBean;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.ToolUtils;

import java.util.List;

/**
 * 商品
 */
public class ProductHotAdapter extends BaseAdapter
{
    private Context mContext;

    private List<ProductBean> mData;

    private ViewHolder holder;

    public ProductHotAdapter(Context context)
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.commodity_item_layout, null);
            holder.imageView = convertView.findViewById(R.id.picture);
            holder.layout = convertView.findViewById(R.id.layout);
            holder.title = convertView.findViewById(R.id.title);
            holder.total = convertView.findViewById(R.id.total);
            holder.sale = convertView.findViewById(R.id.real);
            holder.realPrice = convertView.findViewById(R.id.realprice);
            holder.oldPrice = convertView.findViewById(R.id.oldprice);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (ToolUtils.getScreenWidth(mContext) / 2f),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            holder.layout.setLayoutParams(params);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(mData.get(position).getImg())
                .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_big_common).error(R.mipmap.ic_big_common)).into(holder.imageView);
        holder.title.setText(mData.get(position).getGoods_name());
        holder.total.setText("库存：" + mData.get(position).getStock());
        holder.sale.setText("销量：" + mData.get(position).getSale());
        holder.realPrice.setText(mData.get(position).getShop_price());
        holder.oldPrice.setText(mData.get(position).getMarketPrice());
        holder.oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(KeySet.GOOD_URL, mData.get(position).getGoods_id());
                intent.putExtra(KeySet.TYPE_ID, mData.get(position).getAttr_id());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public class ViewHolder
    {
        private LinearLayout layout;

        private ImageView imageView;

        private TextView title;

        private TextView total;

        private TextView sale;

        private TextView realPrice;

        private TextView oldPrice;
    }

    public void setData(List<ProductBean> data)
    {
        if (mData != null)
        {
            mData.clear();
        }
        this.mData = data;
        notifyDataSetChanged();
    }
}
