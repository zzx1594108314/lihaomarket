package com.lihao.market.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Bean.SortBean;
import com.lihao.market.R;

import java.util.List;

public class SortProductAdapter extends RecyclerView.Adapter<SortProductAdapter.ViewHolder>
{
    private Context mContext;

    private List<SortBean> mData;

    private SortGridAdapter adapter;

    public SortProductAdapter(Context context)
    {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sort_grid_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if (mData != null && mData.size() > 0)
        {
            String hasChild = mData.get(position).getHaschild();
            adapter = new SortGridAdapter(mContext);
            holder.title.setText(mData.get(position).getName());
            if ("1".equals(hasChild))
            {
                adapter.setData(mData.get(position).getSortItems());
            }
            else
            {
                adapter.setData(mData);
            }
            holder.gridView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount()
    {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title;

        private GridView gridView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            gridView = itemView.findViewById(R.id.grid);
        }
    }

    public void setData(List<SortBean> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }
}
