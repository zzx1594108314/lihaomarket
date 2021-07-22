package com.lihao.market.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Activity.ProductListActivity;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeywordItemAdapter extends RecyclerView.Adapter<KeywordItemAdapter.ViewHolder>
{
    private Context mContext;

    private List<String> mData;

    public KeywordItemAdapter(Context context)
    {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        if (mData != null)
        {
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

                    Set<String> newData = new HashSet<>();
                    newData.add(mData.get(position));
                    Set<String> data = SharedPreferencesUtils.getSpListData(SharedPreferencesUtils.RECENT_SEARCH);
                    if (data.size() > 8)
                    {
                        List<String> list = new ArrayList<>(data);
                        list = list.subList(0, 8);
                        data = new HashSet<>(list);
                    }
                    newData.addAll(data);
                    SharedPreferencesUtils.saveSpListData(SharedPreferencesUtils.RECENT_SEARCH, newData);
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nameTv;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name);
        }
    }

    public void setData(List<String> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }
}
