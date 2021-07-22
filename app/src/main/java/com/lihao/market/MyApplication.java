package com.lihao.market;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.guoxiaoxing.phoenix.core.listener.ImageLoader;
import com.guoxiaoxing.phoenix.picker.Phoenix;
import com.lihao.market.Util.FinalHttpLog;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.wby.EEApplication;


public class MyApplication extends EEApplication
{
    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();

        http = new FinalHttpLog();
        http.configUserAgent("baseAndroid/1.0");
        http.configTimeout(10000);

//        CrashReport.initCrashReport(getApplicationContext(), "ed4e55a73e", false);

//        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>()
//        {
//            @Override
//            public void accept(Throwable e) throws Exception
//            {
//                if (e instanceof SocketException)
//                {
//                    ToastUtils.s(context, "网络异常");
//                }
//                else if (e instanceof TimeoutException || e instanceof SocketTimeoutException)
//                {
//                    ToastUtils.s(context,"请求超时");
//                }
//                else if (e instanceof JsonParseException)
//                {
//                    ToastUtils.s(context,"数据解析失败");
//                }
//                else
//                {
//                    ToastUtils.s(context, "服务器异常");
//                }
//            }
//        });

        MQConfig.init(this, "fa16586f90f480b9bc458f08db73b9fe", new OnInitCallback()
        {
            @Override
            public void onSuccess(String s)
            {
                SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.MEIQIA_TYPE, "1");
            }

            @Override
            public void onFailure(int i, String s)
            {
                SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.MEIQIA_TYPE, "2");
            }
        });

        Phoenix.config()
                .imageLoader(new ImageLoader() {
                    @Override
                    public void loadImage(Context mContext, ImageView imageView
                            , String imagePath, int type) {
                        Glide.with(mContext)
                                .load(imagePath)
                                .into(imageView);
                    }
                });

    }


    public static Context getContext()
    {
        return context;
    }
}
