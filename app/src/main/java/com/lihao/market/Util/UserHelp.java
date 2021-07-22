package com.lihao.market.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hasee on 2016/12/19.
 */

public class UserHelp
{

    /**
     * 正则表达式 检验
     *
     * @param editText
     * @param rule
     * @return
     */
    public static boolean checkzhengze(EditText editText, String rule) {
        // 要验证的字符串
        String string = editText.getText().toString();
        // 验证规则
        String regEx = rule;
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(string);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();
        return rs;
    }

    /*
     * ，将时间转换为时间戳
     */
    public static String dateToStamp(String res) throws ParseException
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = sdf.parse(res);
        String time = String.valueOf(date.getTime() / 1000);
        return time;
    }


    //获取当前时间，
    public static String getPosttime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String time = sdf.format(date);
        return time;
    }

    //判断网络情况
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
