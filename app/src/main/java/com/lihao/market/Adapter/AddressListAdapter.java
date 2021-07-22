package com.lihao.market.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Activity.AddAddressActivity;
import com.lihao.market.Bean.AddressBean;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder>
{
    private Context mContext;

    private List<AddressBean> mData;

    private int index = 0;

    private RefreshListener mListener;

    public AddressListAdapter(Context context, RefreshListener listener)
    {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.address_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {
        if (mData != null)
        {
            holder.nameTv.setText(mData.get(position).getConsignee());
            holder.phoneTv.setText(mData.get(position).getMobile());
            holder.addressTv.setText(mData.get(position).getAddress());
            if (position == getIndex())
            {
                holder.checkBox.setChecked(true);
                holder.flagTv.setText("默认地址");
                SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.ADDRESS, mData.get(position).getAddress());
                makeAddress(mData.get(position).getId(), false);
            }
            else
            {
                holder.checkBox.setChecked(false);
                holder.flagTv.setText("设为默认");
            }
            holder.checkBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    boolean isChecked = ((CheckBox)v).isChecked();
                    if (isChecked)
                    {
                        setIndex(position);
                        makeAddress(mData.get(position).getId(), true);
                    }
                }
            });

            holder.editLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.editAddress(mData.get(position).getConsignee(), mData.get(position).getMobile(), mData.get(position).getId());
                }
            });

            holder.deleteLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteAddress(mData.get(position).getId());
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

        private TextView phoneTv;

        private TextView addressTv;

        private CheckBox checkBox;

        private TextView flagTv;

        private LinearLayout editLayout;

        private LinearLayout deleteLayout;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name);
            phoneTv = itemView.findViewById(R.id.phone);
            addressTv = itemView.findViewById(R.id.address);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            flagTv = itemView.findViewById(R.id.address_flag);
            editLayout = itemView.findViewById(R.id.edit_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
        }
    }

    public void setData(List<AddressBean> data)
    {
        this.mData = data;
        if (mData != null && mData.size() == 0)
        {
            SharedPreferencesUtils.clearValue(SharedPreferencesUtils.ADDRESS);
        }
        new Handler().post(new Runnable()
        {
            @Override
            public void run()
            {
                notifyDataSetChanged();
            }
        });
    }

    private int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    private void deleteAddress(String id)
    {
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=user&a=getdrop&address_id=" + id, params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String errorCode = object.getString("status");
                        if ("n".equals(errorCode))
                        {
                            String message = object.getString("info");
                            ToastUtils.s(mContext, message);
                        }
                        else
                        {
                            mListener.refresh();
                            ToastUtils.s(mContext, "删除成功");
                        }
                    } catch (Exception e)
                    {
                        Log.e("login", e.toString());
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

    private void makeAddress(String id, final boolean isSet)
    {
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=user&a=ajax_make_address&address_id=" + id, params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        if (isSet)
                        {
                            notifyDataSetChanged();
                        }
                    } catch (Exception e)
                    {
                        Log.e("makeAddress", e.toString());
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

    public interface RefreshListener
    {
        void refresh();

        void editAddress(String name, String phone, String id);
    }
}
