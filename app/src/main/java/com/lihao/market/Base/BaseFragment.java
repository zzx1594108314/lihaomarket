package com.lihao.market.Base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment
{
    private ProgressDialog progressDialg = null;

    private ProgressDialog progressDialogwating = null;

    private ProgressDialog Dialg = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 显示加载Dialog
     */
    protected void showLoadingDialog()
    {
        if (progressDialg == null)
        {
            progressDialg = new ProgressDialog(getContext());
            progressDialg.setCancelable(false);
            progressDialg.setMessage("加载中...");
        }
        progressDialg.show();

    }

    protected void showLoadingDialog(Context context)
    {
        if (progressDialg == null)
        {
            progressDialg = new ProgressDialog(context);
            progressDialg.setCancelable(false);
            progressDialg.setMessage("加载中...");
        }
        progressDialg.show();

    }

    /**
     * 隐藏加载Dialog
     */
    protected void hideLoadingDialog()
    {
        if (progressDialg != null)
        {
            progressDialg.dismiss();
        }
    }

    //显示等待进度条
    protected void showWaitingCmdDialog()
    {
        if (progressDialogwating == null)
        {
            progressDialogwating = new ProgressDialog(getContext());
            progressDialogwating.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialogwating.setCancelable(false);
            progressDialogwating.setIndeterminate(false);
            progressDialogwating.setMessage("等待设备响应...");

        }
        progressDialogwating.show();

    }

    //隐藏等待进度条
    protected void hideWaitingDialog()
    {
        if (progressDialogwating != null)
        {
            progressDialogwating.dismiss();
        }
    }

    /**
     * 显示加载Dialog
     */
    protected void showStringLoadingDialog(String s)
    {
        if (Dialg == null)
        {
            Dialg = new ProgressDialog(getContext());
            Dialg.setCancelable(false);
            Dialg.setMessage(s);
        }
        Dialg.show();

    }

    /**
     * 隐藏加载Dialog
     */
    protected void hideStringLoadingDialog()
    {
        if (Dialg != null)
        {
            Dialg.dismiss();
        }
    }
}
