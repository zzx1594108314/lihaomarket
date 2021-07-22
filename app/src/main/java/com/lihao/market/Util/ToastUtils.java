package com.lihao.market.Util;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/*
* Toast 简单封装使用*/
public class ToastUtils {
    private static Application app;

    private ToastUtils() {
    }

    public static void init(Application app) {
        ToastUtils.app = app;
    }

    public static void s(String msg) {
        if (app == null) return;
        s(app, msg);
    }

    public static void l(String msg) {
        if (app == null) return;
        l(app, msg);
    }

    public static void s(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static void l(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
