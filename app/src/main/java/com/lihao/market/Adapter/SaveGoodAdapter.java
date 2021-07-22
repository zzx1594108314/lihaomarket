package com.lihao.market.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Activity.ProductDetailActivity;
import com.lihao.market.Bean.ProductBean;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class SaveGoodAdapter extends RecyclerView.Adapter<SaveGoodAdapter.ViewHolder>
{
    private Context mContext;

    private List<ProductBean> mData;

    private DeleteListener mListener;

    public SaveGoodAdapter(Context context, DeleteListener listener)
    {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.save_good_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        if (mData != null)
        {
            Glide.with(mContext).load(mData.get(position).getImg()).apply(new RequestOptions().centerCrop()).into(holder.imageView);
            holder.nameTv.setText(mData.get(position).getGoods_name());
            holder.priceTv.setText(mData.get(position).getShop_price());
            holder.deleteTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    delete(mData.get(position).getRec_id());
                }
            });
            holder.saveLayout.setOnClickListener(new View.OnClickListener()
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

        private TextView nameTv;

        private TextView priceTv;

        private ImageView deleteTv;

        private RelativeLayout saveLayout;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            nameTv = itemView.findViewById(R.id.name);
            priceTv = itemView.findViewById(R.id.price);
            deleteTv = itemView.findViewById(R.id.delete);
            saveLayout = itemView.findViewById(R.id.save_layout);
        }
    }

    private void delete(String deleteId)
    {
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=user&a=getdelcollection&rec_id=" + deleteId, params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        if ("0".equals(error))
                        {
                            mListener.delete();
                        }
                        else
                        {
                            ToastUtils.s(mContext, "删除失败");
                        }

                    } catch (Exception e)
                    {
                        Log.e("delete adapter", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg)
                {
                    super.onFailure(t, strMsg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(List<ProductBean> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface DeleteListener
    {
        void delete();
    }
}
