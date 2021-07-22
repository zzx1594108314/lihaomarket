package com.lihao.market.Util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

import com.lihao.market.R;

/*EditText 删除文本内容自定义控件
输入框为空时，图标隐藏 输入框有内容时 图标显示 然后可点击删除 左边文本内容。
 **/
public class ClearableEditText extends AppCompatEditText
{

    private Context mContext;
    //显示图标
    private Drawable innerImg;

    public ClearableEditText(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        innerImg = mContext.getResources().getDrawable(R.mipmap.ic_delete);
        //文本变动监听
        addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });

        setDrawable();
    }

    //根据文本长度判断 来设置图标显示位置 上下左右
    private void setDrawable() {
        if (length() < 1)
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        else
            setCompoundDrawablesWithIntrinsicBounds(null, null, innerImg, null);
    }

    /*触碰事件 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (innerImg != null && event.getAction() == MotionEvent.ACTION_UP) ;
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        Rect rect = new Rect();
        getGlobalVisibleRect(rect);
        rect.left = rect.right - 100;

        if (rect.contains(x, y))
            setText("");

        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
    }
}
