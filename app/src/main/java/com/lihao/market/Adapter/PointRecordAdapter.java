package com.lihao.market.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Bean.PointBean;
import com.lihao.market.R;

import java.util.List;

public class PointRecordAdapter extends RecyclerView.Adapter<PointRecordAdapter.ViewHolder>
{
    private Context mContext;

    private List<PointBean> mData;

    //0:积分1：余额
    private String type;

    public PointRecordAdapter(Context context, String type)
    {
        this.mContext = context;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.point_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if (mData != null)
        {
            holder.descTv.setText(mData.get(position).getChange_desc());
            holder.timeTv.setText(mData.get(position).getChange_time());
            holder.typeTv.setText(mData.get(position).getType());
            if ("0".equals(type))
            {
                holder.pointTv.setText(mData.get(position).getPay_points() + " 积分");
            }
            else
            {
                holder.pointTv.setText(mData.get(position).getPay_points());
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mData == null ? 0 : mData.size();
    }

    public void setData(List<PointBean> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView descTv;

        private TextView timeTv;

        private TextView typeTv;

        private TextView pointTv;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            descTv = itemView.findViewById(R.id.desc);
            timeTv = itemView.findViewById(R.id.time);
            typeTv = itemView.findViewById(R.id.type);
            pointTv = itemView.findViewById(R.id.point);
        }
    }
}
