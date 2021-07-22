package com.lihao.market.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Bean.AddressBean;
import com.lihao.market.R;

import java.util.List;

public class AddressChooseAdapter extends RecyclerView.Adapter<AddressChooseAdapter.ViewHolder>
{
    private Context mContext;

    private List<AddressBean> mData;

    private chooseDataListener mListener;

    private int index = 0;

    public AddressChooseAdapter(Context context, chooseDataListener listener)
    {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.address_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        if (mData != null)
        {
            holder.name.setText(mData.get(position).getName());
            holder.name.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.getChoose(mData.get(position).getId(), mData.get(position).getName(), index + 1);
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
        private TextView name;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }

    public void setData(List<AddressBean> data, int position)
    {
        this.mData = data;
        this.index = position;
        notifyDataSetChanged();
    }

    public interface chooseDataListener
    {
        void getChoose(String id, String name, int position);
    }
}
