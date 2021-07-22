package com.lihao.market.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.R;

import java.util.List;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder>
{
    private Context mContext;

    private List<GoodBean> mData;

    public OrderDetailItemAdapter(Context context)
    {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if (mData != null)
        {
            Glide.with(mContext).load(mData.get(position).getGoods_thumb()).into(holder.imageview);
            holder.titleTv.setText(mData.get(position).getGoods_name());
            holder.priceTv.setText(mData.get(position).getShop_price());
            holder.subTitleTv.setText(mData.get(position).getGoods_desc());
            holder.numTv.setText(mData.get(position).getGoods_number() + "件");
        }
    }

    @Override
    public int getItemCount()
    {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageview;

        private TextView titleTv;

        private TextView priceTv;

        private TextView subTitleTv;

        private TextView numTv;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageview);
            titleTv = itemView.findViewById(R.id.title);
            priceTv = itemView.findViewById(R.id.price);
            subTitleTv = itemView.findViewById(R.id.subTitle);
            numTv = itemView.findViewById(R.id.num);
        }
    }

    public void setData(List<GoodBean> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }
}
