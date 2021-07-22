package com.lihao.market.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.PayTask;
import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.Constants;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.PayResult;
import com.lihao.market.Util.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wby.utility.ToastUtil;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 支付方式
 */
public class PayModeActivity extends BaseActivity implements View.OnClickListener
{
    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_AUTH_FLAG = 2;

    private TextView backTv;

    private TextView titleTv;

    private TextView priceTv;

    private TextView payTv;

    private CheckBox wxBox;

    private CheckBox zfbBox;

    private String sn_orider;

    private String order_id;

    private String price;

    private IWXAPI api;

    private int payType;

    @Override
    public int getLayoutId()
    {
        return R.layout.aactivity_pay_mode;
    }

    @Override
    public void initView()
    {
        backTv = findViewById(R.id.tv_back);
        titleTv = findViewById(R.id.tv_title);
        priceTv = findViewById(R.id.price);
        payTv = findViewById(R.id.pay_text);
        wxBox = findViewById(R.id.weichat_checkbox);
        zfbBox = findViewById(R.id.zhifubao_checkbox);

        IntentFilter filter = new IntentFilter("com.broadcast.payupdate");
        BroadCastManager.getInstance().registerReceiver(this, payReceiver, filter);
    }

    @Override
    public void initData()
    {
        titleTv.setText("在线支付");
        Intent intent = getIntent();
        if (intent != null)
        {
            sn_orider = intent.getStringExtra("order_sn");
//            price = intent.getStringExtra("price");
            order_id = intent.getStringExtra("order_id");
//            priceTv.setText("¥" + price);
        }
        regToWx();
        needPay();
    }

    @Override
    public void initListener()
    {
        backTv.setOnClickListener(this);
        payTv.setOnClickListener(this);

        wxBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean isCheck = ((CheckBox) v).isChecked();
                if (isCheck)
                {
                    payType = 1;
                    zfbBox.setChecked(false);
                    payTv.setText("微信支付");
                }
            }
        });

        zfbBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean isCheck = ((CheckBox) v).isChecked();
                if (isCheck)
                {
                    payType = 2;
                    wxBox.setChecked(false);
                    payTv.setText("支付宝支付");
                }
            }
        });
    }

    private void regToWx()
    {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
    }

    private void needPay()
    {
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        params.put("order_sn", sn_orider);
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=onlinepay&a=getIndex", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        if ("0".equals(error))
                        {
                            String order_amount = object.getString("order_amount");
                            String price_format = object.getString("price_format");
                            priceTv.setText(price_format);
                            price = order_amount;
                        }
                        else
                        {
                            ToastUtils.l(getApplicationContext(), "非法操作，无法支付");
                            finish();
                        }
                    } catch (Exception e)
                    {
                        Log.e("needPay", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wxPay()
    {
        int total = 10000;
        try
        {
            Number number = Float.parseFloat(price) * 100;
            total = number.intValue();
            if (total <= 0)
            {
                return;
            }
        }catch (Exception e)
        {
            Log.e("wxpay", "e: " + e);
        }
        
        AjaxParams params = new AjaxParams();
        try {
            params.put("outtradeno", sn_orider);
            params.put("totalfee", String.valueOf(total));
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post("http://118.178.59.142:5488/Boiler.asmx/wxPay", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("resultcode");
                        if ("0".equals(error))
                        {
                            JSONObject message = object.getJSONObject("message");
                            PayReq request = new PayReq();
                            request.appId = message.getString("appid");
                            request.partnerId = message.getString("partnerid");
                            request.prepayId = message.getString("prepayid");
                            request.packageValue = "Sign=WXPay";
                            request.nonceStr= message.getString("noncestr");
                            request.timeStamp= message.getString("timestamp");
                            request.sign= message.getString("sign");
                            api.sendReq(request);
                        }
                    } catch (Exception e)
                    {
                        Log.e("wxPay", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_back:
                setResult(3);
                finish();
                break;
            case R.id.pay_text:
                if (payType == 1)
                {
                    if (!api.isWXAppInstalled())
                    {
                        ToastUtils.s(PayModeActivity.this, "您的手机未安装微信");
                    }
                    else
                    {
                        wxPay();
                    }
                }
                else if (payType == 2)
                {
                    if (isAliPayInstalled(this))
                    {
                        aliPay();
                    }
                    else
                    {
                        ToastUtils.s(PayModeActivity.this, "您的手机未安装支付宝");
                    }
                }
                else
                {
                    ToastUtils.s(PayModeActivity.this, "请选择支付方式");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        BroadCastManager.getInstance().unregisterReceiver(this, payReceiver);
    }

    @Override
    public void onBackPressed()
    {
        setResult(3);
        super.onBackPressed();
    }

    BroadcastReceiver payReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int msg = intent.getIntExtra("msg", 0);
            switch (msg)
            {
                case 1:
                    pay();
                    break;
                case 2:
                    ToastUtils.s(PayModeActivity.this, "支付失败");
                    break;
            }
            
        }
    };
    
    private void pay()
    {
        AjaxParams params = new AjaxParams();
        try {
            params.put("order_sn", sn_orider);
            params.put("order_id", order_id);
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=flow&a=getordersn", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        if ("0".equals(error))
                        {
                            setResult(2);
                            finish();
                        }
                    } catch (Exception e)
                    {
                        Log.e("update_pay", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aliPay()
    {
        int total = 10000;
        try
        {
            Number number = Float.parseFloat(price);
            total = number.intValue();
            if (total <= 0)
            {
                return;
            }
        }catch (Exception e)
        {
            Log.e("wxpay", "e: " + e);
        }

        AjaxParams params = new AjaxParams();
        try {
            params.put("tradeno", sn_orider);
            params.put("totalfee", String.valueOf(total));
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post("http://118.178.59.142:5488/Boiler.asmx/aliPay", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("resultcode");
                        if ("0".equals(error))
                        {
                            String payInfo = object.getString("message");
                            zfbPay(payInfo);
                        }
                    } catch (Exception e)
                    {
                        Log.e("wxPay", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测是否安装支付宝
     * @param context
     * @return
     */
    public static boolean isAliPayInstalled(Context context)
    {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }


    private void zfbPay(String info)
    {
        final String orderInfo = info;   // 订单信息

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayModeActivity.this);
                Map<String,String> result = alipay.payV2(orderInfo,true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg)
        {
            super.handleMessage(msg);
            PayResult payResult = new PayResult((Map<String, String>) msg.obj);
            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000"))
            {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                pay();
            }
//            else
//            {
//                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//            }
        }
    };
}
