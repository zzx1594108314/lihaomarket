package com.lihao.market.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Activity.OrderDetailActivity;
import com.lihao.market.Bean.OrderBean;
import com.lihao.market.Custom.MyGridView;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class OrderInfoAdapter extends RecyclerView.Adapter<OrderInfoAdapter.ViewHolder>
{
    private Context mContext;

    private OrderListAdapter adapter;

    private QueryOrderListener mListener;

    public OrderInfoAdapter(Context context, QueryOrderListener listener)
    {
        this.mContext = context;
        this.mListener = listener;
    }

    private List<OrderBean> mData;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        if (mData != null)
        {
            holder.subTitle.setText(mData.get(position).getOrder_status());
            holder.orderId.setText(mData.get(position).getOrder_sn());
            holder.time.setText(mData.get(position).getOrder_time());
            holder.orderNum.setText("共" + mData.get(position).getOrder_goods_num() + "款");
            holder.price.setText(mData.get(position).getTotal_fee());
            if (KeySet.ORDER_STATUS_1.equals(mData.get(position).getOrder_status()))
            {
                holder.readyPay.setVisibility(View.VISIBLE);
                holder.readyConfirm.setVisibility(View.GONE);
                holder.delOrderLayout.setVisibility(View.GONE);
                holder.overOrderLayout.setVisibility(View.GONE);
                holder.confirmGetLayout.setVisibility(View.GONE);
            }
            else if (KeySet.ORDER_STATUS_2.equals(mData.get(position).getOrder_status()))
            {
                holder.readyPay.setVisibility(View.GONE);
                holder.readyConfirm.setVisibility(View.VISIBLE);
                holder.delOrderLayout.setVisibility(View.GONE);
                holder.overOrderLayout.setVisibility(View.GONE);
                holder.confirmGetLayout.setVisibility(View.GONE);
            }
            else if (KeySet.ORDER_STATUS_3.equals(mData.get(position).getOrder_status()))
            {
                holder.readyPay.setVisibility(View.GONE);
                holder.readyConfirm.setVisibility(View.GONE);
                holder.delOrderLayout.setVisibility(View.VISIBLE);
                holder.overOrderLayout.setVisibility(View.GONE);
                holder.confirmGetLayout.setVisibility(View.GONE);
            }
            else if (KeySet.ORDER_STATUS_4.equals(mData.get(position).getOrder_status()))
            {
                holder.readyPay.setVisibility(View.GONE);
                holder.readyConfirm.setVisibility(View.GONE);
                holder.delOrderLayout.setVisibility(View.GONE);
                holder.overOrderLayout.setVisibility(View.VISIBLE);
                holder.confirmGetLayout.setVisibility(View.GONE);
            }
            else if (KeySet.ORDER_STATUS_5.equals(mData.get(position).getOrder_status()))
            {
                holder.readyPay.setVisibility(View.GONE);
                holder.readyConfirm.setVisibility(View.GONE);
                holder.delOrderLayout.setVisibility(View.GONE);
                holder.overOrderLayout.setVisibility(View.GONE);
                holder.confirmGetLayout.setVisibility(View.VISIBLE);
            }

            adapter = new OrderListAdapter(mContext);
            adapter.setData(mData.get(position).getOrder_goods());
            holder.gridView.setAdapter(adapter);

            //取消订单
            holder.cancelTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    cancelOrder(mData.get(position).getOrder_id());
                }
            });
            //删除订单
            holder.delOrder.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteOrder(mData.get(position).getOrder_id());
                }
            });
            //立即支付
            holder.buyTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String price = mData.get(position).getTotal_fee().substring(1);
                    mListener.payOrder(mData.get(position).getOrder_sn(), mData.get(position).getOrder_id(), price);
                }
            });
            //申请售后
            holder.saleTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String account = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ACCOUNT);
                    HashMap<String, String> info = new HashMap<>();
                    info.put("name", account);
                    info.put("tel", account);
                    Intent intent = new MQIntentBuilder(mContext).setCustomizedId(account).setClientInfo(info).build();
                    mContext.startActivity(intent);
                }
            });
            //订单结束删除
            holder.overDel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteOrder(mData.get(position).getOrder_id());
                }
            });
            holder.confirmGet.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    confirmGet(mData.get(position).getOrder_id());
                }
            });
            holder.saleGet.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String account = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ACCOUNT);
                    HashMap<String, String> info = new HashMap<>();
                    info.put("name", account);
                    info.put("tel", account);
                    Intent intent = new MQIntentBuilder(mContext).setCustomizedId(account).setClientInfo(info).build();
                    mContext.startActivity(intent);
                }
            });
            holder.seeTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent info = new Intent(mContext, OrderDetailActivity.class);
                    info.putExtra("orderId", mData.get(position).getOrder_id());
                    mContext.startActivity(info);
                }
            });
            holder.delSee.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent info = new Intent(mContext, OrderDetailActivity.class);
                    info.putExtra("orderId", mData.get(position).getOrder_id());
                    mContext.startActivity(info);
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
        private TextView subTitle;

        private TextView orderId;

        private TextView time;

        private TextView orderNum;

        private TextView price;

        private MyGridView gridView;

        private RelativeLayout readyPay;

        private TextView buyTv;

        private TextView cancelTv;

        private RelativeLayout readyConfirm;

        private TextView saleTv;

        private TextView seeTv;

        private RelativeLayout delOrderLayout;

        private TextView delSee;

        private TextView delOrder;

        private RelativeLayout overOrderLayout;

        private TextView overDel;

        //确认收货布局，已发货
        private RelativeLayout confirmGetLayout;

        private TextView confirmGet;

        private TextView saleGet;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            subTitle = itemView.findViewById(R.id.subTitle);
            orderId = itemView.findViewById(R.id.order_id);
            time = itemView.findViewById(R.id.order_time);
            orderNum = itemView.findViewById(R.id.num);
            price = itemView.findViewById(R.id.price);
            gridView = itemView.findViewById(R.id.gridview);
            readyPay = itemView.findViewById(R.id.ready_buy_layout);
            readyConfirm = itemView.findViewById(R.id.ready_get_layout);
            buyTv = itemView.findViewById(R.id.buy_text);
            cancelTv = itemView.findViewById(R.id.clear_order);
            saleTv = itemView.findViewById(R.id.after_sale);
            seeTv = itemView.findViewById(R.id.see_order);
            delOrderLayout = itemView.findViewById(R.id.order_del_layout);
            delSee = itemView.findViewById(R.id.del_see_order);
            delOrder = itemView.findViewById(R.id.del_text);
            overOrderLayout = itemView.findViewById(R.id.over_order_layout);
            overDel = itemView.findViewById(R.id.over_del_text);
            confirmGetLayout = itemView.findViewById(R.id.confirm_order_layout);
            confirmGet = itemView.findViewById(R.id.confirm_order);
            saleGet = itemView.findViewById(R.id.sale_text);
        }
    }

    public void setData(List<OrderBean> data)
    {
        this.mData = data;
        notifyDataSetChanged();
    }

    private void cancelOrder(String orderId)
    {
        try {
            AjaxParams params = new AjaxParams();
            params.put("order_id", orderId);
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=order&a=getcancel", params, new AjaxCallBack<Object>()
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
                            if (mListener != null)
                            {
                                mListener.queryOrder();
                            }
                        }
                    } catch (Exception e)
                    {
                        Log.e("cancelOrder", e.toString());
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

    private void deleteOrder(String orderId)
    {
        try {
            AjaxParams params = new AjaxParams();
            params.put("order_id", orderId);
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=order&a=getdelorder", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("y");
                        if ("1".equals(error))
                        {
                            if (mListener != null)
                            {
                                mListener.queryOrder();
                            }
                        }
                    } catch (Exception e)
                    {
                        Log.e("DeleteOrder", e.toString());
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

    private void confirmGet(String orderId)
    {
        try {
            AjaxParams params = new AjaxParams();
            params.put("order_id", orderId);
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=order&a=GetAffirmReceived", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("y");
                        if ("1".equals(error))
                        {
                            if (mListener != null)
                            {
                                mListener.queryOrder();
                            }
                        }
                        else
                        {
                            ToastUtils.s(mContext, "操作失败");
                        }
                    } catch (Exception e)
                    {
                        Log.e("DeleteOrder", e.toString());
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

    public interface QueryOrderListener
    {
        void queryOrder();

        void payOrder(String order_sn, String orderId, String price);
    }
}
