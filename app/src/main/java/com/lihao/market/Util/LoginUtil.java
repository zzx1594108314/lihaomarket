package com.lihao.market.Util;

public class LoginUtil
{
    private static boolean isLogin;

    public static void setLogin(boolean login)
    {
        isLogin = login;
    }

    /**
     * 判断是否登录
     */
    public static boolean getLogin()
    {
        return isLogin;
    }
}
