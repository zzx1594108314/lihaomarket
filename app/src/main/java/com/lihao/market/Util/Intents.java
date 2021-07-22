package com.lihao.market.Util;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by hasee on 2016/12/17.
 */

public class Intents
{

    private static Intents intents;

    /**
     * 单例模式
     *
     * @return
     */
    public static Intents getIntents() {
        if (intents == null)
            intents = new Intents();
        return intents;
    }

    /**
     * activity间不带参数的跳转
     *
     * @param context 当前界面
     * @param cs      跳转去的界面
     */
    public void Intent(Context context, Class<?> cs) {
        android.content.Intent i = new android.content.Intent(context, cs);
        context.startActivity(i);
    }

    /**
     * @param context 当前界面
     * @param cs      跳转去的界面
     * @param bundle  携带的参数bundle
     */
    public void Intent(Context context, Class<?> cs, Bundle bundle) {
        android.content.Intent i = new android.content.Intent(context, cs);
        if (bundle != null)
            i.putExtras(bundle);
        context.startActivity(i);
    }
}
