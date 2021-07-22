package com.lihao.market.Util;

import android.content.Context;

import com.lihao.market.MyApplication;


/**
 * 获取string
 */
public class ResUtils
{

    public static String getString(int resId)
    {
        Context context = MyApplication.getContext();

        String string = context.getString(resId);
        return string;
    }
}
