package com.lihao.market.wxapi;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.R;
import com.lihao.market.Util.Constants;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler
{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@SuppressLint("LongLogTag")
	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX)
		{
			if (resp.errCode == 0)
			{
				Intent intent = new Intent("com.broadcast.payupdate");
				intent.putExtra("msg", 1);
				BroadCastManager.getInstance().sendBroadCast(this, intent);
				finish();
				//支付成功
			}
			else if (resp.errCode == -1)
			{
				//支付失败
				Intent intent = new Intent("com.broadcast.payupdate");
				intent.putExtra("msg", 2);
				BroadCastManager.getInstance().sendBroadCast(this, intent);
				finish();
			}
			else
			{
				//取消支付
				finish();
			}
		}
	}
}