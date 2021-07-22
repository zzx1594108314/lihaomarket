package com.lihao.market.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Bean.SortTitleBean;
import com.lihao.market.R;

import java.util.List;

public class SortTitleAdapter extends RecyclerView.Adapter<SortTitleAdapter.ViewHolder>
{
    private Context mContext;

    private List<SortTitleBean> mData;

    private int index = 0;

    public SortTitleAdapter(Context context)
    {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view  = LayoutInflater.from(mContext).inflate(R.layout.sort_title_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        if (mData != null)
        {
            holder.title.setText(mData.get(position).getName());
            holder.title.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setIndex(position);
                    getListener.onClick(position);
                    notifyDataSetChanged();
                }
            });

            if (position == getIndex())
            {
                holder.title.setBackgroundResource(R.color.sort_select_title);
                holder.title.setTextColor(mContext.getResources().getColor(R.color.sort_select_title_text));
            }
            else
            {
                holder.title.setBackgroundResource(R.color.sort_unselect_title);
                holder.title.setTextColor(mContext.getResources().getColor(R.color.sort_unselect_title_text));
            }
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

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }

    private int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public void setData(List<SortTitleBean> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface GetListener
    {
        void onClick(int position);
    }

    private GetListener getListener;

    public void setGetListener(GetListener getListener)
    {
        this.getListener = getListener;
    }
}
