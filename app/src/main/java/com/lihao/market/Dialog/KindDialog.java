package com.lihao.market.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Adapter.ProductTypeAdapter;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.Custom.AmountView;
import com.lihao.market.R;

public class KindDialog extends Dialog implements ProductTypeAdapter.ProductInfoListener
{
    private TextView sureTv;

    private TextView nameTv;

    private TextView priceTv;

    private ImageView headIv;

    private TextView numTv;

    private ImageView closeIv;

    private GridView gridView;

    private AmountView amountView;

    private LinearLayout hasGood;

    private TextView noGood;

    private Context mContext;

    private GoodBean mBean;

    private ProductTypeAdapter adapter;

    private NumTypeCallback callback;

    private String productType;

    private String number = "1";

    private String typeId;

    //1:购买2：购物车
    private String flag;

    public KindDialog(@NonNull Context context, GoodBean bean, NumTypeCallback callback, String flag)
    {
        super(context, R.style.AlertDialogStyle);
        this.mContext = context;
        this.mBean = bean;
        this.callback = callback;
        this.flag = flag;
        setCustomDialog();
    }

    public KindDialog(@NonNull Context context, int themeResId)
    {
        super(context, themeResId);
    }

    protected KindDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
    }

    private void setCustomDialog()
    {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.kind_product_layout, null);
        sureTv = mView.findViewById(R.id.shopping);
        nameTv = mView.findViewById(R.id.name);
        priceTv = mView.findViewById(R.id.price);
        headIv = mView.findViewById(R.id.imageview);
        numTv = mView.findViewById(R.id.num);
        closeIv = mView.findViewById(R.id.close);
        gridView = mView.findViewById(R.id.grid);
        amountView = mView.findViewById(R.id.amountview);
        hasGood = mView.findViewById(R.id.hasgood);
        noGood = mView.findViewById(R.id.nogood);

        amountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener()
        {
            @Override
            public void onAmountChange(View view, int amount)
            {
                number = String.valueOf(amount);
            }
        });
        Glide.with(mContext).load(mBean.getGoods_thumb()).apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_small_common).error(R.mipmap.ic_small_common)).into(headIv);
        nameTv.setText(mBean.getGoods_name());
        priceTv.setText("¥" + mBean.getShop_price());
        numTv.setText(mBean.getGoods_number());
        amountView.setGoods_storage(Integer.parseInt(mBean.getGoods_number()));
        if (Integer.parseInt(mBean.getGoods_number()) > 0)
        {
            hasGood.setVisibility(View.VISIBLE);
            noGood.setVisibility(View.GONE);
        }
        else
        {
            hasGood.setVisibility(View.GONE);
            noGood.setVisibility(View.VISIBLE);
        }

        adapter = new ProductTypeAdapter(mContext, mBean.getKindBeans(), this);
        gridView.setAdapter(adapter);

        closeIv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.getInfo(productType, number, typeId, "0");
                dismiss();
            }
        });

        sureTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.getInfo(productType, number, typeId, flag);
                dismiss();
            }
        });

        super.setContentView(mView);
    }

    /**
     * 立即购买监听器
     *
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener)
    {
    }

    /**
     * 加入购物车监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener)
    {
    }

    public interface NumTypeCallback
    {
        void getInfo(String type, String num, String typeId, String flag);
    }

    @Override
    public void show()
    {
        super.show();

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity= Gravity.BOTTOM;
        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void getInfo(String price, String stock, String id, String attr)
    {
        typeId = attr;
        priceTv.setText(price);
        numTv.setText(stock);
        int account = Integer.parseInt(stock);
        amountView.setGoods_storage(account);
        if (account > 0)
        {
            hasGood.setVisibility(View.VISIBLE);
            noGood.setVisibility(View.GONE);
        }
        else
        {
            hasGood.setVisibility(View.GONE);
            noGood.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getType(String type)
    {
        productType = type;
    }
}
