package com.lihao.market.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lihao.market.Activity.BindActivity;
import com.lihao.market.Activity.ForgetPasswordActivity;
import com.lihao.market.Activity.HomeActivity;
import com.lihao.market.Activity.PayModeActivity;
import com.lihao.market.Activity.QuickLoginActivity;
import com.lihao.market.Activity.RegisterActivity;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.Constants;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wby.utility.ToastUtil;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

/**
 * 个人中心登录页
 */
public class PersonPageLoginFragment extends BaseFragment implements View.OnClickListener
{
    private static final String ARG_SHOW_TEXT = "text";

    private static final int LOGIN_RESULT = 10000;

    private EditText inputNumber;

    private EditText inputCode;

    private ImageView numberDelete;

    private ImageView codeDelete;

    private CheckBox showBox;

    private TextView loginTv;

    private TextView registerTv;

    private ImageView weiChat;

    private TextView forgetTv;

    private TextView quickTv;

    private IWXAPI api;

    public PersonPageLoginFragment()
    {}

    public static PersonPageLoginFragment newInstance(String param1)
    {
        PersonPageLoginFragment personPageFragment = new PersonPageLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        personPageFragment.setArguments(args);
        return personPageFragment;
    }

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
        View person = inflater.inflate(R.layout.fragment_login, container, false);

        initView(person);
        initListener();
        initData();
        regToWx();

        return person;
    }

    private void regToWx()
    {
        api = WXAPIFactory.createWXAPI(getContext(), Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
    }

    private void initView(View view)
    {
        inputNumber = view.findViewById(R.id.input);
        inputCode = view.findViewById(R.id.edt_pwd);
        numberDelete = view.findViewById(R.id.account_delete);
        codeDelete = view.findViewById(R.id.pwd_delete);
        showBox = view.findViewById(R.id.checkbox);
        loginTv = view.findViewById(R.id.login);
        registerTv = view.findViewById(R.id.new_user);
        forgetTv = view.findViewById(R.id.forget_pwd);
        quickTv = view.findViewById(R.id.quick_login);
        weiChat = view.findViewById(R.id.weichat);
    }

    private void initData()
    {
        IntentFilter filter = new IntentFilter("com.broadcast.bindwx");
        BroadCastManager.getInstance().registerReceiver(getActivity(), bindReceiver, filter);
    }

    private void initListener()
    {
        numberDelete.setOnClickListener(this);
        codeDelete.setOnClickListener(this);
        loginTv.setOnClickListener(this);
        registerTv.setOnClickListener(this);
        forgetTv.setOnClickListener(this);
        quickTv.setOnClickListener(this);
        weiChat.setOnClickListener(this);
        showBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int passwordLength = inputCode.getText().length();
                inputCode.setInputType(isChecked ?
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
                inputCode.setSelection(passwordLength);
            }
        });
        inputNumber.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                {
                    numberDelete.setVisibility(View.VISIBLE);
                }
                else
                {
                    numberDelete.setVisibility(View.GONE);
                }
            }
        });
        inputCode.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                {
                    codeDelete.setVisibility(View.VISIBLE);
                }
                else
                {
                    codeDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.account_delete:
                inputNumber.setText("");
                break;
            case R.id.pwd_delete:
                inputCode.setText("");
                break;
            case R.id.login:
                login();
                break;
            case R.id.new_user:
                Intent register = new Intent(getContext(), RegisterActivity.class);
                startActivity(register);
                break;
            case R.id.forget_pwd:
                Intent forget = new Intent(getContext(), ForgetPasswordActivity.class);
                startActivity(forget);
                break;
            case R.id.quick_login:
                Intent quick = new Intent(getContext(), QuickLoginActivity.class);
                startActivityForResult(quick, LOGIN_RESULT);
                break;
            case R.id.weichat:
                if (!api.isWXAppInstalled())
                {
                    ToastUtils.s(getContext(), "您的手机未安装微信");
                }
                else
                {
                    final SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat_sdk_demo_test";
                    api.sendReq(req);
                }
                break;
            default:
                break;
        }
    }

    private void login()
    {
        if (TextUtils.isEmpty(inputNumber.getText()))
        {
            ToastUtils.s(getContext(), "请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(inputCode.getText()))
        {
            ToastUtils.s(getContext(), "请输入密码");
            return;
        }

        AjaxParams params = new AjaxParams();
        try {
            params.put("username", inputNumber.getText().toString());
            params.put("password", inputCode.getText().toString());
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String result = object.getString("status");
                        String message = object.getString("info");
                        if ("y".equals(result))
                        {
                            HomeActivity activity = (HomeActivity)getActivity();
                            if (activity != null)
                            {
                                activity.reLoadFragView();
                            }
                            LoginUtil.setLogin(true);
                            SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.ACCOUNT, inputNumber.getText().toString());
                            SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.PASSWORD, inputCode.getText().toString());
                        }
                        else
                        {
                            LoginUtil.setLogin(false);
                        }
                        ToastUtils.s(getContext(), message);

                    } catch (Exception e)
                    {
                        Log.e("getCode", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                    ToastUtils.s(getContext(), "登录失败");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RESULT && data != null)
        {
            Bundle bundle = data.getExtras();
            switch (resultCode)
            {
                case 1:
                    String result = bundle.getString(KeySet.QUICK_LOGIN);
                    if ("y".equals(result))
                    {
                        HomeActivity activity = (HomeActivity)getActivity();
                        if (activity != null)
                        {
                            activity.reLoadFragView();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver bindReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int msg = intent.getIntExtra("msg", 0);
            switch (msg)
            {
                case 1:
                    String bind = intent.getStringExtra("bind");
                    String name = intent.getStringExtra("name");
                    String headUrl = intent.getStringExtra("headUrl");
                    Intent bindIntent = new Intent(getContext(), BindActivity.class);
                    bindIntent.putExtra("bind", bind);
                    bindIntent.putExtra("name", name);
                    bindIntent.putExtra("headUrl", headUrl);
                    startActivity(bindIntent);
                    break;
                case 2:
                    HomeActivity activity = (HomeActivity)getActivity();
                    if (activity != null)
                    {
                        activity.reLoadFragView();
                    }
                    LoginUtil.setLogin(true);
                    break;
            }

        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        BroadCastManager.getInstance().unregisterReceiver(getActivity(), bindReceiver);
    }
}
