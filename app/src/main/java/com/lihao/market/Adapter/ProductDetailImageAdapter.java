package com.lihao.market.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;

import java.util.List;

public class ProductDetailImageAdapter extends RecyclerView.Adapter<ProductDetailImageAdapter.ViewHolder>
{
    private Context mContext;

    private List<String> mData;

    public ProductDetailImageAdapter(Context context)
    {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_detail_image_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if (mData != null)
        {
            Glide.with(mContext).load(KeySet.URL + mData.get(position)).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount()
    {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

    public void setData(List<String> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }
}
