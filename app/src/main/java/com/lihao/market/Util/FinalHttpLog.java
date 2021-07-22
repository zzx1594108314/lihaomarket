package com.lihao.market.Util;

import android.util.Log;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class FinalHttpLog extends FinalHttp
{
    @Override
    public void get(String url, AjaxCallBack<? extends Object> callBack) {
        // TODO Auto-generated method stub
        Log.i("HTML", url);
        super.get(url, callBack);
    }

    @Override
    public void post(String url, AjaxParams params,
                     AjaxCallBack<? extends Object> callBack) {
        // TODO Auto-generated method stub
        super.post(url, params, callBack);
    }
}
