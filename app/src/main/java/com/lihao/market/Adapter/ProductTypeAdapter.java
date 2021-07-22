package com.lihao.market.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lihao.market.Bean.KindBean;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.List;

public class ProductTypeAdapter extends BaseAdapter
{
    private List<KindBean> mData;

    private Context mContext;

    private ViewHolder holder;

    private ProductInfoListener mListener;

    private ProgressDialog progressDialg = null;

    private int index = 0;

    private boolean first = true;

    public ProductTypeAdapter(Context context, List<KindBean> data, ProductInfoListener listener)
    {
        this.mData = data;
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public int getCount()
    {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.product_type_layout, null);
            holder.name = convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(mData.get(position).getLabel());
        if (position == getIndex())
        {
            holder.name.setTextColor(mContext.getResources().getColor(R.color.sort_select_title_text));
            holder.name.setBackgroundResource(R.drawable.text_squire_red);
        }
        else
        {
            holder.name.setTextColor(mContext.getResources().getColor(R.color.text_un_select_color));
            holder.name.setBackgroundResource(R.drawable.text_squire_grey);
        }
        holder.name.setPadding(40, 0, 40, 0);

        if (position == 0 && first)
        {
            first = false;
            setIndex(0);
            getKindInfo(mData.get(0).getGood_id(), mData.get(0).getId());
            mListener.getType(mData.get(0).getLabel());
        }

        holder.name.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setIndex(position);
                getKindInfo(mData.get(position).getGood_id(), mData.get(position).getId());
                mListener.getType(mData.get(position).getLabel());
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public interface ProductInfoListener
    {
        void getInfo(String price, String stock, String id, String attr);

        void getType(String type);
    }

    private class ViewHolder
    {
        private TextView name;
    }

    private void getKindInfo(final String id, final String attr)
    {
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=goods&a=price&id=" + id + "&attr=" + attr + "&number=1", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        String price = object.getString("shop_price");
                        String stock = object.getString("attr_number");
                        mListener.getInfo(price, stock, id, attr);
                    } catch (Exception e)
                    {
                        Log.e("getKindInfo", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示加载Dialog
     */
    protected void showLoadingDialog()
    {
        if (progressDialg == null)
        {
            progressDialg = new ProgressDialog(mContext);
            progressDialg.setCancelable(false);
            progressDialg.setMessage("加载中...");
        }
        progressDialg.show();

    }

    /**
     * 隐藏加载Dialog
     */
    protected void hideLoadingDialog()
    {
        if (progressDialg != null)
        {
            progressDialg.dismiss();
        }
    }
}
