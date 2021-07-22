package com.lihao.market.Util;


import com.blankj.utilcode.util.SPUtils;

import java.util.Set;


/**
 * sp工具类
 */
public class SharedPreferencesUtils
{
    public static final String USER_DATA = "user_data";

    public static final String SECRET_NAME = "screatname";

    public static final String IDENTITY = "identity";

    public static final String ID = "id";

    public static final String ACCOUNT = "account";

    public static final String PASSWORD = "password";

    public static final String VERIFICATION_CODE = "verification_code";

    public static final String MOBILE = "mobile";

    public static final String AUTHORITY = "authority";

    public static final String RECENT_SEARCH = "recent_search";

    public static final String WX_OPEN_ID = "wx_open_id";

    public static final String WX_HEAD_URL = "wx_head_url";

    public static final String WX_USER_NAME = "wx_user_name";

    public static final String MEIQIA_TYPE = "meiqia_type";

    public static final String ADDRESS = "address";

    public static final String SUSPEND_WIDTH = "suspend_width";

    public static final String SUSPEND_HEIGTH = "suspend_heigth";

    public static final String SHOW_KEFU_DIALOG = "show_kefu_dialog";

    /**
     * 是否退出前台
     */
    public static final String APPLICATION_RECEPTION = "application_reception";

    /**
     * 是否首次启动App
     */
    public static final String FIRST_START_APP = "first_start_app";

    public static void saveSpStringData(String key, String value)
    {
        SPUtils.getInstance().put(key, value);
    }

    public static String getSpStringData(String key)
    {
        return SPUtils.getInstance().getString(key);
    }

    public static void saveSpIntData(String key, int value)
    {
        SPUtils.getInstance().put(key, value);
    }

    public static int getSpIntData(String key)
    {
        return SPUtils.getInstance().getInt(key);
    }

    public static void saveSpListData(String key, Set<String> data)
    {
        SPUtils.getInstance().put(key, data);
    }

    public static Set<String> getSpListData(String key)
    {
        return SPUtils.getInstance().getStringSet(key);
    }

    /**
     * SP 中移除该 key
     * @param key
     */
    public static void clearValue(String key)
    {
        SPUtils.getInstance().remove(key);
    }

}
