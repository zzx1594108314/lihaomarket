package com.lihao.market.Util;

import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 时间工具类
 */
public class TimeUtil
{
    /**
     * 显示时间
     */
    public static String showTime(long time)
    {
        String timeString;

        if (TimeUtils.isToday(time))
        {
            timeString = TimeUtils.millis2String(time, new SimpleDateFormat("HH:mm", Locale.getDefault()));
            return timeString;
        }
        else
        {
            timeString = TimeUtils.millis2String(time, new SimpleDateFormat("MM-dd", Locale.getDefault()));
            return timeString;
        }

    }
}
