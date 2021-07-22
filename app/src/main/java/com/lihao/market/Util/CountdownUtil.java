package com.lihao.market.Util;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CountdownUtil
{
    //设置单例能不能避免一直在请求
    private static CountDownTimer timer;

    public static List<CountDownTimer> timers = new ArrayList<>();

    //这是倒计时执行方法
    public static void RunTimer(final TextView tvCodeWr, final String name)
    {
        timer = new CountDownTimer(60 * 1000, 1000)
        {
            @Override
            public void onFinish()
            {
                if (tvCodeWr != null)
                {
                    tvCodeWr.setText(name);
                    tvCodeWr.setClickable(true);
                    tvCodeWr.setEnabled(true);
                }

                cancel();
            }

            @Override
            public void onTick(long millisUntilFinished)
            {
                if (tvCodeWr != null)
                {
                    tvCodeWr.setClickable(false);
                    tvCodeWr.setEnabled(false);
                    tvCodeWr.setText(name + "(" + millisUntilFinished / 1000 + ")");
                }
            }
        }.start();
    }

    /**
     * 倒计时
     *
     * @param textView textView
     * @param time     倒计时间，秒
     */
    public static void timeCount1(final TextView textView, long time)
    {
        timer = new CountDownTimer(time * 1000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                textView.setText(formatMiss(millisUntilFinished));
            }

            @Override
            public void onFinish()
            {
                cancel();
            }
        }.start();
    }

    /**
     * 倒计时
     *
     * @param textView1 textView1
     * @param textView2 textView2
     * @param textView3 textView3
     * @param time      倒计时间，秒
     */
    public static void timeCount2(final TextView textView1, final TextView textView2,
                                  final TextView textView3, final long time, final LinearLayout timeLayout,
                                  final TextView timeTv, final int position)
    {
        long rightTime = time - System.currentTimeMillis() / 1000;
        if (rightTime < 0)
        {
            timeLayout.setVisibility(View.GONE);
            timeTv.setVisibility(View.VISIBLE);
            return;
        }
        timer = new CountDownTimer(rightTime * 1000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                String[] temp = formatMiss(millisUntilFinished).split(":");
                textView1.setText(temp[0]);
                textView2.setText(temp[1]);
                textView3.setText(temp[2]);
            }

            @Override
            public void onFinish()
            {
                if (timers.size() > 0)
                {
                    timers.get(position).cancel();
                }
                timeLayout.setVisibility(View.GONE);
                timeTv.setVisibility(View.VISIBLE);
            }
        }.start();
        timers.add(timer);
    }

    //这个方法可以在activity或者fragment销毁的时候调用，防止内存泄漏
    public static void cancel()
    {
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }

    public static void cancelTimers()
    {
        if (timers != null && timers.size() > 0)
        {
            for (CountDownTimer countDownTimer : timers)
            {
                if (countDownTimer != null)
                {
                    countDownTimer.cancel();
                }
            }
        }
    }

    // 将毫秒转化成小时分钟秒
    public static String formatMiss(long millisec)
    {
        int hour = 00;
        int minute = 00;
        int second = (int) millisec / 1000;
        String resultStr = "";
        if (second >= 60)
        {
            minute = second / 60;//取整
            second = second % 60;//取余
            if (minute >= 60)
            {
                hour = minute / 60;
                minute = minute % 60;
                if (hour < 10)
                {
                    if (minute < 10)
                    {
                        if (second < 10)
                        {
                            resultStr = "0" + hour + ":" + "0" + minute + ":" + "0" + second;
                        } else
                        {
                            resultStr = "0" + hour + ":" + "0" + minute + ":" + second;
                        }
                    } else
                    {
                        if (second < 10)
                        {
                            resultStr = "0" + hour + ":" + minute + ":" + "0" + second;
                        } else
                        {
                            resultStr = "0" + hour + ":" + minute + ":" + second;
                        }

                    }
                } else
                {
                    if (minute < 10)
                    {
                        if (second < 10)
                        {
                            resultStr = hour + ":" + "0" + minute + ":" + "0" + second;
                        } else
                        {
                            resultStr = hour + ":" + "0" + minute + ":" + second;
                        }
                    } else
                    {
                        if (second < 10)
                        {
                            resultStr = hour + ":" + minute + ":" + "0" + second;
                        } else
                        {
                            resultStr = hour + ":" + minute + ":" + second;
                        }

                    }
                }
            } else
            {
                if (minute < 10)
                {
                    if (second < 10)
                    {
                        resultStr = "00" + ":" + "0" + minute + ":" + "0" + second;
                    } else
                    {
                        resultStr = "00" + ":" + "0" + minute + ":" + second;
                    }
                } else
                {
                    if (second < 10)
                    {
                        resultStr = "00" + ":" + minute + ":" + "0" + second;
                    } else
                    {
                        resultStr = "00" + ":" + minute + ":" + second;
                    }
                }

            }
        } else
        {
            if (second < 10)
            {
                resultStr = "0" + hour + ":" + "0" + minute + ":" + "0" + second;
            } else
            {
                resultStr = "0" + hour + ":" + "0" + minute + ":" + second;
            }
        }
        return resultStr;
    }

    public static void clearTimers()
    {
        if (timers != null)
        {
            timers.clear();
        }
    }

}
