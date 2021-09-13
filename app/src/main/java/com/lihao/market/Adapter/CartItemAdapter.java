package com.lihao.market.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.lihao.market.Bean.CartBean;
import com.lihao.market.Bean.ProductBean;
import com.lihao.market.Custom.AmountView;
import com.lihao.market.Custom.CartAmountView;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder>
{
    private Context mContext;

    private List<CartBean> mData;

    private DeleteListener mListener;

    private SelectNumListener numListener;

    //商品的id
    private List<String> recId = new ArrayList<>();

    //通过全选按钮得到的商品id
    private List<String> all = new ArrayList<>();

    //checkbox的状态
    private Map<Integer, Boolean> checkStatus = new HashMap<>();

    private int selectNum = 0;

    //是否全选
    private boolean allSelect;

    private boolean isUseAll = false;

    public CartItemAdapter(Context context, DeleteListener listener, SelectNumListener numListener)
    {
        this.mContext = context;
        this.mListener = listener;
        this.numListener = numListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {
        if (mData != null)
        {
            if (isUseAll)
            {
                holder.checkBox.setChecked(allSelect);
                if (allSelect)
                {
                    recId = all;
                }
                else
                {
                    recId.clear();
                }
            }
            else
            {
                if ("1".equals(mData.get(position).getIs_checked()))
                {
                    selectNum++;
                    holder.checkBox.setChecked(true);
                    if (selectNum == mData.size())
                    {
                        numListener.showAllSelect(true);
                    }
                    recId.add(mData.get(position).getRec_id());
                }
                else
                {
                    holder.checkBox.setChecked(false);
                }
            }
//            if (checkStatus.size() == mData.size())
//            {
//                holder.checkBox.setChecked(checkStatus.get(position));
//            }
            holder.titleTv.setText(mData.get(position).getGoods_name());
            holder.subTitleTv.setText(mData.get(position).getGoods_attr());
            holder.priceTv.setText("¥" + mData.get(position).getGoods_price());
            holder.amountView.setGoods_storage(Integer.parseInt(mData.get(position).getAttr_number()));
            holder.amountView.getEditView().setText(mData.get(position).getGoods_number());
            Glide.with(mContext).load(mData.get(position).getGoods_thumb())
                    .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_small_common).error(R.mipmap.ic_small_common)).into(holder.imageView);

            if ("1".equals(mData.get(position).getIs_invalid()))
            {
                holder.checkBox.setVisibility(View.GONE);
                holder.amountView.setVisibility(View.GONE);
                holder.titleTv.setText(mData.get(position).getGoods_name() + "(已失效)");
            }


            holder.deleteTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    delete(mData.get(position).getRec_id());
                }
            });

            holder.checkBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    boolean isCheck = ((CheckBox)v).isChecked();
                    if (isUseAll)
                    {
                        isUseAll = false;
                        all.clear();
                    }
                    if (isCheck)
                    {
                        if (selectNum > mData.size() - 1)
                        {
                            selectNum = mData.size() - 1;
                        }
                        selectNum++;
                        if (selectNum == mData.size())
                        {
                            numListener.showAllSelect(true);
                        }
                        recId.add(mData.get(position).getRec_id());
                        cartLabelCount(mData.get(position).getRec_id(),"1");
                    }
                    else
                    {
                        if (selectNum < 1)
                        {
                            selectNum = 1;
                        }
                        selectNum--;
                        if (selectNum < mData.size())
                        {
                            numListener.showAllSelect(false);
                        }
                        recId.remove(mData.get(position).getRec_id());
                        cartLabelCount(mData.get(position).getRec_id(), "0");
                    }
                    numListener.deleteIdCallBack(recId);
                }
            });

            holder.amountView.setOnCartChangeListener(new CartAmountView.OnCartChangeListener()
            {
                @Override
                public void onCartChange(View view, int amount, int origin)
                {
                    if (holder.checkBox.isChecked())
                    {
                        changeGoodNumber(mData.get(position).getRec_id(), amount);
                    }
                    else
                    {
                        holder.amountView.getEditView().setText(String.valueOf(origin));
                        ToastUtils.s(mContext, "该商品未勾选无法进行增减");
                    }
                }
            });

            holder.saveTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saveGood(mData.get(position).getGoods_id());
                }
            });

            numListener.deleteIdCallBack(recId);

        }
    }

    @Override
    public int getItemCount()
    {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private CheckBox checkBox;

        private ImageView imageView;

        private TextView titleTv;

        private TextView subTitleTv;

        private TextView priceTv;

        private CartAmountView amountView;

        private TextView deleteTv;

        private TextView saveTv;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            imageView = itemView.findViewById(R.id.imageview);
            titleTv = itemView.findViewById(R.id.title);
            subTitleTv = itemView.findViewById(R.id.subTitle);
            priceTv = itemView.findViewById(R.id.price);
            amountView = itemView.findViewById(R.id.amountview);
            deleteTv = itemView.findViewById(R.id.delete_cart);
            saveTv = itemView.findViewById(R.id.save_cart);
        }
    }

    public void setData(List<CartBean> data)
    {
        this.mData = data;
        isUseAll = false;
        selectNum = 0;
        initData(false);
        notifyDataSetChanged();
    }

    private void delete(String id)
    {
        try {
            AjaxParams params = new AjaxParams();
            params.put("id", id);
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=cart&a=getdeletecart", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String message = object.getString("msg");
                        if (object.getString("error").equals("0"))
                        {
                            mListener.deleteCart();
                        }
                        ToastUtils.s(mContext, message);

                    } catch (Exception e)
                    {
                        Log.e("delete,cart", e.toString());
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

    private void cartLabelCount(String cart_id, String status)
    {
        StringBuffer buffer = new StringBuffer();
        if (recId.size() > 0)
        {
            for (String id : recId)
            {
                buffer.append(id).append(",");
            }
        }
        else
        {
            buffer.append("");
        }
        try {
            AjaxParams params = new AjaxParams();
            params.put("type", "0");
            params.put("rec_id", buffer.toString());
            params.put("cart_id", cart_id + ",");
            params.put("status", status);
            params.put("sel_flag", "cart_sel_flag");
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=cart&a=getcart_label_count", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String cartNum = object.getString("cart_number");
                        String price = object.getString("goods_amount");
                        numListener.selectNumber(cartNum, price);

                    } catch (Exception e)
                    {
                        Log.e("cartLabelCount", e.toString());
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

    private void changeGoodNumber(String cart_id, int amount)
    {
        StringBuffer buffer = new StringBuffer();
        if (recId.size() > 0)
        {
            for (String id : recId)
            {
                buffer.append(id).append(",");
            }
        }
        else
        {
            buffer.append("");
        }
        try {
            AjaxParams params = new AjaxParams();
            params.put("number", String.valueOf(amount));
            params.put("rec_id", buffer.toString());
            params.put("cart_id", cart_id);
            params.put("act_id", "0");
            params.put("none", "0");
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=cart&a=getcart_goods_number", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String cartNum = object.getString("cart_number");
                        String price = object.getString("goods_amount");
                        numListener.selectNumber(cartNum, price);

                    } catch (Exception e)
                    {
                        Log.e("changeGoodNumber", e.toString());
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

    private void saveGood(String orderid)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(orderid).append(Uri.encode(","));

        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=cart&a=getheart&id=" + buffer.toString() + "&status=0", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String msg = object.getString("msg");
                        ToastUtils.s(mContext, msg);

                    } catch (Exception e)
                    {
                        Log.e("saveGood", e.toString());
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

    public void allSelect(boolean isAllSelect, boolean isUse)
    {
        allSelect = isAllSelect;
        isUseAll = isUse;
        initData(isAllSelect);
        if (isAllSelect)
        {
            selectNum = mData.size();
        }
        else
        {
            selectNum = 0;
        }

        all.clear();
        for (int i = 0; i < mData.size(); i++)
        {
            if ("0".equals(mData.get(i).getIs_invalid()))
            {
                all.add(mData.get(i).getRec_id());
            }
        }
        if (isUseAll)
        {
            if (allSelect)
            {
                numListener.selectCallBack(all, "1");
                numListener.deleteIdCallBack(all);
            }
            else
            {
                numListener.selectCallBack(all, "2");
                numListener.deleteIdCallBack(new ArrayList<String>());
            }
        }

        notifyDataSetChanged();
    }

    private void initData(boolean isAll)
    {
        if (mData != null)
        {
            checkStatus.clear();
            recId.clear();
            for (int i = 0; i < mData.size(); i++)
            {
                checkStatus.put(i, isAll);
                if (isAll)
                {
                    if ("0".equals(mData.get(i).getIs_invalid()))
                    {
                        recId.add(mData.get(i).getRec_id());
                    }
                }
            }
        }
    }

    public interface DeleteListener
    {
        void deleteCart();
    }

    public interface SelectNumListener
    {
        void selectCallBack(List<String> ids, String status);

        void selectNumber(String num, String totalprice);

        void showAllSelect(boolean all);

        void deleteIdCallBack(List<String> ids);
    }

}
