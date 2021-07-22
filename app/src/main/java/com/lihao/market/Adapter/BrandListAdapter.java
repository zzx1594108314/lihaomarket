package com.lihao.market.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Bean.BrandBean;
import com.lihao.market.R;

import java.util.ArrayList;
import java.util.List;

public class BrandListAdapter extends RecyclerView.Adapter<BrandListAdapter.ViewHolder>
{
    private Context mContext;

    private List<BrandBean> mData;

    private BrandListener mLisener;

    private List<BrandBean> brandBeans = new ArrayList<>();

    private List<Boolean> check = new ArrayList<>();

    public BrandListAdapter(Context context, BrandListener listener)
    {
        mContext = context;
        mLisener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.brand_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {
        if (mData != null)
        {
            holder.name.setText(mData.get(position).getBrandName());
            holder.hook.setEnabled(false);
            if (check.get(position))
            {
                holder.name.setTextColor(mContext.getResources().getColor(R.color.sort_select_title_text));
                holder.hook.setChecked(true);
            }
            else
            {
                holder.name.setTextColor(mContext.getResources().getColor(R.color.current_time_text));
                holder.hook.setChecked(false);
            }

            holder.brandLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if ("全部".equals(mData.get(position).getBrandName()))
                    {
                        brandBeans.clear();
                        List<BrandBean> list = new ArrayList<>();
                        list.add(mData.get(position));
                        holder.name.setTextColor(mContext.getResources().getColor(R.color.sort_select_title_text));
                        holder.hook.setChecked(true);
                        for (int i = 0; i < check.size(); i++)
                        {
                            check.set(i, false);
                        }
                        check.set(position, true);
                        mLisener.setBrandInfo(list);
                    }
                    else
                    {
                        if (holder.hook.isChecked())
                        {
                            holder.name.setTextColor(mContext.getResources().getColor(R.color.current_time_text));
                            holder.hook.setChecked(false);
                            check.set(position, false);
                            brandBeans.remove(mData.get(position));
                        }
                        else
                        {
                            holder.name.setTextColor(mContext.getResources().getColor(R.color.sort_select_title_text));
                            holder.hook.setChecked(true);
                            check.set(position, true);
                            brandBeans.add(mData.get(position));
                        }
                        check.set(0, false);
                        mLisener.setBrandInfo(brandBeans);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return mData == null ? 0 : mData.size();
    }

    public void setData(List<BrandBean> data)
    {
        mData = data;
        for (int i = 0; i < data.size(); i++)
        {
            check.add(false);
        }
        notifyDataSetChanged();
    }

    public void restoreData()
    {
        brandBeans.clear();
        List<BrandBean> list = new ArrayList<>();
        if (mData != null)
        {
            list.add(mData.get(0));
        }
        for (int i = 0; i < check.size(); i++)
        {
            check.set(i, false);
        }
        check.set(0, true);
        mLisener.setBrandInfo(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name;

        private CheckBox hook;

        private RelativeLayout brandLayout;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.brand_name);
            hook = itemView.findViewById(R.id.hook_checkbox);
            brandLayout = itemView.findViewById(R.id.brand);
        }
    }

    public interface BrandListener
    {
        void setBrandInfo(List<BrandBean> info);
    }
}
