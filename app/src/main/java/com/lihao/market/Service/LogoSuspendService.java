package com.lihao.market.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

import com.lihao.market.Dialog.LogoSuspend;
import com.lihao.market.Dialog.SizeEntity;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;

import java.util.HashMap;

public class LogoSuspendService extends Service
{
    private LogoSuspend logoSuspend;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        logoSuspend = new LogoSuspend(this);
        int width = SharedPreferencesUtils.getSpIntData(SharedPreferencesUtils.SUSPEND_WIDTH);
        int height = SharedPreferencesUtils.getSpIntData(SharedPreferencesUtils.SUSPEND_HEIGTH);
        logoSuspend.showSuspend(new SizeEntity(width, height), false);//从缓存中提取上一次显示的位置
        logoSuspend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (LoginUtil.getLogin())
                {
                    String account = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ACCOUNT);
                    HashMap<String, String> info = new HashMap<>();
                    info.put("name", account);
                    info.put("tel", account);
                    Intent intent = new MQIntentBuilder(getApplicationContext()).setCustomizedId(account).updateClientInfo(info).setClientInfo(info).build();
                    startActivity(intent);
                }
                else
                {
                    ToastUtils.s(getApplicationContext(), "请先登录");
                }
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (logoSuspend != null)
        {
            logoSuspend.dismissSuspend();
        }
    }
}
