package com.lihao.market.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lihao.market.Activity.BindActivity;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.Util.Constants;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.entry);
        
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
		api.registerApp(Constants.APP_ID);
        api.handleIntent(getIntent(), this);

    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT).show();
		
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//			goToShowMsg((ShowMessageFromWX.Req) req);
			break;
		case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
			break;
		default:
			break;
		}
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			String code = ((SendAuth.Resp) resp).code;
			//获取accesstoken
			getAccessToken(code);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			break;
		default:
			break;
		}
		
	}

	private void getAccessToken(String code)
	{
		//获取授权
		AjaxParams params = new AjaxParams();
		params.put("code", code);
		params.put("os", "android");
		try {
			MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
			MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login&a=GetAuthLogin", params, new AjaxCallBack<Object>() {
				@Override
				public void onSuccess(Object o) {
					super.onSuccess(o);
					try
					{
						JSONObject object = new JSONObject(o.toString());
						String status = object.getString("status");
						if ("n".equals(status))
						{
							JSONObject json = object.getJSONObject("info");
							String name = json.getString("nickname");
							String headUrl = json.getString("headimgurl");
							String bind_ident = object.getString("bind_ident");
							Intent intent = new Intent("com.broadcast.bindwx");
							intent.putExtra("msg", 1);
							intent.putExtra("bind", bind_ident);
							intent.putExtra("name", name);
							intent.putExtra("headUrl", headUrl);
							BroadCastManager.getInstance().sendBroadCast(WXEntryActivity.this, intent);
							finish();
						}
						else
						{
							Intent intent = new Intent("com.broadcast.bindwx");
							intent.putExtra("msg", 2);
							BroadCastManager.getInstance().sendBroadCast(WXEntryActivity.this, intent);
							String user_name = object.getString("user_name");
							SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.WX_USER_NAME, user_name);
							String mobile_phone = object.getString("mobile_phone");
							SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.ACCOUNT, mobile_phone);
							finish();
						}
					} catch (Exception e)
					{
						Log.e("getAccessToken", e.toString());
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


	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
		
		StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);
		
		finish();
	}
}