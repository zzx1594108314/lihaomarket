package com.lihao.market.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lihao.market.R;

/**
 * Created by hasee on 2017/4/6.
 */

public class CustomDialog extends Dialog
{
    private TextView editText;

    private TextView positiveButton;

    private TextView negativeButton;

    private TextView title;

    private TextView subTitle;

    public CustomDialog(Context context)
    {
        super(context, R.style.AlertDialogStyle);
        setCustomDialog();
    }

    public CustomDialog(Context context, int layout)
    {
        super(context, R.style.AlertDialogStyle);
        setCustomDialog(layout);
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_normal_layout, null);
        title = mView.findViewById(R.id.title);
        editText = mView.findViewById(R.id.content);
        subTitle = mView.findViewById(R.id.subTitle);
        positiveButton = mView.findViewById(R.id.positiveButton);
        negativeButton = mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);
    }

    private void setCustomDialog(int layout)
    {
        View mView = LayoutInflater.from(getContext()).inflate(layout, null);
        title = mView.findViewById(R.id.title);
        editText = mView.findViewById(R.id.content);
        positiveButton = mView.findViewById(R.id.positiveButton);
        negativeButton = mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);
    }

    public View getEditText() {
        return editText;
    }

    @Override
    public void setContentView(int layoutResID) {
    }

    public CustomDialog setTitle(String titles)
    {
        title.setText(titles);
        return this;
    }

    public CustomDialog setSubTitle(String subTitles)
    {
        subTitle.setText(subTitles);
        subTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public CustomDialog setContent(String content)
    {
        editText.setText(content);
        return this;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     *
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    /**
     * 取消键监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }
}