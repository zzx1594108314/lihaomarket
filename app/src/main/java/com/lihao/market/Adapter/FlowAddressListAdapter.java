package com.lihao.market.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Bean.AddressBean;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.List;

public class FlowAddressListAdapter extends RecyclerView.Adapter<FlowAddressListAdapter.ViewHolder>
{
    private Context mContext;

    private List<AddressBean> mData;

    private RefreshListener mListener;

    public FlowAddressListAdapter(Context context, RefreshListener listener)
    {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.flow_address_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        if (mData != null)
        {
            holder.nameTv.setText(mData.get(position).getConsignee());
            holder.phoneTv.setText(mData.get(position).getMobile());
            holder.addressTv.setText(mData.get(position).getAddress());
//            if (position == getIndex())
//            {
//                holder.checkBox.setChecked(true);
//                holder.flagTv.setText("默认地址");
//            }
//            else
//            {
//                holder.checkBox.setChecked(false);
//                holder.flagTv.setText("设为默认");
//            }
            holder.checkBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    boolean isChecked = ((CheckBox)v).isChecked();
                    if (isChecked)
                    {
                        makeAddress(mData.get(position).getId());
                        SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.ADDRESS, mData.get(position).getAddress());
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

//            holder.deleteLayout.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    deleteAddress(mData.get(position).getId());
//                }
//            });
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
//            deleteLayout = itemView.findViewById(R.id.delete_layout);
        }
    }

    private void makeAddress(String id)
    {
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=flow&a=getset_address&address_id=" + id, params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String status = object.getString("status");
                        if ("1".equals(status))
                        {
                            mListener.refresh();
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

    public void setData(List<AddressBean> data)
    {
        this.mData = data;
        new Handler().post(new Runnable()
        {
            @Override
            public void run()
            {
                notifyDataSetChanged();
            }
        });
    }

    public interface RefreshListener
    {
        void refresh();

        void editAddress(String name, String phone, String id);
    }
}
