package com.lihao.market.Http;


import org.apache.http.client.CookieStore;

/**
 * 用于存储CookieStore，请求处于同一session
 */
public class MyCookieStore
{
    private static final String TAG = "MyCookieStore";

    public static CookieStore cookieStore = null;

}
