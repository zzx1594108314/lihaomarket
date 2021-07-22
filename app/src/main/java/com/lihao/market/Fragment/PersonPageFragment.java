package com.lihao.market.Fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Activity.AboutActivity;
import com.lihao.market.Activity.BalanceActivity;
import com.lihao.market.Activity.HomeActivity;
import com.lihao.market.Activity.OrderInfoActivity;
import com.lihao.market.Activity.PointActivity;
import com.lihao.market.Activity.SaveGoodActivity;
import com.lihao.market.Activity.SettingInfoActivity;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Custom.CircleImageView;
import com.lihao.market.Dialog.CustomDialog;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Service.Downloadservice;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static android.content.Context.BIND_AUTO_CREATE;


/**
 * 个人中心
 */
public class PersonPageFragment extends BaseFragment implements View.OnClickListener
{
    private static final String ARG_SHOW_TEXT = "text";

    private static final int LOGIN_RESULT = 10000;

    private static float fnow;

    private static float fmax;

    private CircleImageView circleIv;

    private TextView userName;

    private TextView allOrderTv;

    private ImageView readyPayIv;

    private TextView readyPayNum;

    private ImageView readyTeamIv;

    private ImageView readyGetIv;

    private TextView readyGetNum;

    private ImageView readyCommentIv;

    private ImageView readyExchangeIv;

    private TextView settingTv;

    private RelativeLayout settingLayout;

    private TextView versionTv;

    private LinearLayout saveLayout;

    private RelativeLayout aboutLayout;

    private LinearLayout pointLayout;

    private TextView pointTv;

    private LinearLayout balanceLayout;

    private TextView balanceTv;

    private TextView cashLayout;

    private String name;

    private String sex;

    private String headUrl;

    private String verName;

    private ProgressDialog progress;

    private boolean isBindService;

    private String balance;

    private String payPoint;

    public PersonPageFragment()
    {}

    public static PersonPageFragment newInstance(String param1)
    {
        PersonPageFragment personPageFragment = new PersonPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        personPageFragment.setArguments(args);
        return personPageFragment;
    }

    private ServiceConnection conn = new ServiceConnection() { //通过ServiceConnection间接可以拿到某项服务对象

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Downloadservice.DownloadBinder binder = (Downloadservice.DownloadBinder) service;
            Downloadservice downloadServise = binder.getService();

            //接口回调，下载进度
            downloadServise.setOnProgressListener(new Downloadservice.OnProgressListener() {
                @Override
                public void onProgress(int fraction, int max) {
                    fnow = (float) fraction / 1024 / 1024;
                    fmax = (float) max / 1024 / 1024;
                    progress.setProgress(fraction);
                    progress.setMax(max);
                    progress.setProgressNumberFormat(String.format("%.2fM/%.2fM", fnow, fmax));
                    //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
                    if (fraction == max && isBindService) {
                        // fnow=max/1024/1024;
                        // progress.setProgress(max);
                        progress.dismiss();
                        getActivity().getApplicationContext().unbindService(conn);
                        isBindService = false;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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
        View person = inflater.inflate(R.layout.fragment_person_page, container, false);

        initView(person);
        initListener();
        initData();

        return person;
    }

    private void initView(View view)
    {
        circleIv = view.findViewById(R.id.circle_image);
        userName = view.findViewById(R.id.name);
        allOrderTv = view.findViewById(R.id.all_order);
        readyPayIv = view.findViewById(R.id.ready_pay);
        readyPayNum = view.findViewById(R.id.payment_num);
        readyTeamIv = view.findViewById(R.id.ready_team);
        readyGetIv = view.findViewById(R.id.ready_receive);
        readyGetNum = view.findViewById(R.id.ready_get_num);
        readyCommentIv = view.findViewById(R.id.ready_comment);
        readyExchangeIv = view.findViewById(R.id.ready_exchange);
        settingTv = view.findViewById(R.id.setting);
        settingLayout = view.findViewById(R.id.setting_layout);
        versionTv = view.findViewById(R.id.version);
        saveLayout = view.findViewById(R.id.save_layout);
        aboutLayout = view.findViewById(R.id.about_layout);
        pointLayout = view.findViewById(R.id.point_layout);
        pointTv = view.findViewById(R.id.point_text);
        balanceLayout = view.findViewById(R.id.balance_layout);
        balanceTv = view.findViewById(R.id.balance);
        cashLayout = view.findViewById(R.id.cash_layout);
    }

    private void initListener()
    {
        circleIv.setOnClickListener(this);
        allOrderTv.setOnClickListener(this);
        readyPayIv.setOnClickListener(this);
        readyTeamIv.setOnClickListener(this);
        readyGetIv.setOnClickListener(this);
        readyCommentIv.setOnClickListener(this);
        readyExchangeIv.setOnClickListener(this);
        settingTv.setOnClickListener(this);
        versionTv.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        saveLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        pointLayout.setOnClickListener(this);
        balanceLayout.setOnClickListener(this);
        cashLayout.setOnClickListener(this);
    }

    private void initData()
    {
        if (LoginUtil.getLogin())
        {
            queryUserInfo();
            queryOrderInfo();
            queryPoint();
        }

        versionTv.setText(getVerName(getContext()));
    }

    private void queryUserInfo()
    {
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=profile&a=getIndex", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        String error = object.getString("error");
                        if ("0".equals(error))
                        {
                            JSONObject json = object.getJSONObject("user_info");
                            name = json.getString("nick_name");
                            sex = json.getString("sex");
                            userName.setText(name);
                            headUrl = json.getString("user_picture");
                            Glide.with(getContext()).load(headUrl)
                                    .apply(new RequestOptions().centerCrop().fallback(R.mipmap.user_default).error(R.mipmap.user_default)).into(circleIv);
                        }

                    } catch (Exception e)
                    {
                        Log.e("queryUserInfo", e.toString());
                        ToastUtils.l(getContext(), "登录失效，请退出重新进入");
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryOrderInfo()
    {
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&a=getIndex", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        String payCount = object.getString("pay_count");
                        String confirmCount = object.getString("confirmed_count");
                        try
                        {
                            if (Integer.parseInt(payCount) > 0)
                            {
                                readyPayNum.setText(payCount);
                                readyPayNum.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                readyPayNum.setVisibility(View.GONE);
                            }
                            if (Integer.parseInt(confirmCount) > 0)
                            {
                                readyGetNum.setText(confirmCount);
                                readyGetNum.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                readyGetNum.setVisibility(View.GONE);
                            }
                        }catch (Exception e)
                        {
                            Log.e("queryOrderInfo","e: " + e);
                        }


                    } catch (Exception e)
                    {
                        Log.e("queryOrderInfo", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryPoint()
    {
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=account&a=getIndex", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        payPoint = object.getString("pay_points");
                        balance = object.getString("money");
                        pointTv.setText(payPoint);
                        balanceTv.setText(balance);

                    } catch (Exception e)
                    {
                        Log.e("queryPoint", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        if (!hidden)
        {
            queryOrderInfo();
            queryPoint();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.circle_image:
                break;
            case R.id.all_order:
                Intent allOrder = new Intent(getContext(), OrderInfoActivity.class);
                allOrder.putExtra(KeySet.ORDER_INDEX, "0");
                startActivity(allOrder);
                break;
            case R.id.ready_pay:
                Intent pay = new Intent(getContext(), OrderInfoActivity.class);
                pay.putExtra(KeySet.ORDER_INDEX, "1");
                startActivity(pay);
                break;
            case R.id.ready_team:
                break;
            case R.id.ready_receive:
                Intent confirm = new Intent(getContext(), OrderInfoActivity.class);
                confirm.putExtra(KeySet.ORDER_INDEX, "2");
                startActivity(confirm);
                break;
            case R.id.ready_comment:
                break;
            case R.id.ready_exchange:
                break;
            case R.id.point_layout:
                Intent point = new Intent(getContext(), PointActivity.class);
                startActivity(point);
                break;
            case R.id.balance_layout:
            case R.id.cash_layout:
                Intent balance = new Intent(getContext(), BalanceActivity.class);
                balance.putExtra("balance", this.balance);
                balance.putExtra("point", payPoint);
                startActivity(balance);
                break;
            case R.id.setting_layout:
                Intent set = new Intent(getContext(), SettingInfoActivity.class);
                set.putExtra("name", name);
                set.putExtra("sex", sex);
                set.putExtra("url", headUrl);
                startActivityForResult(set, 1);
                break;
            case R.id.version:
                getVersion();
                break;
            case R.id.save_layout:
                Intent save = new Intent(getContext(), SaveGoodActivity.class);
                startActivity(save);
                break;
            case R.id.about_layout:
                Intent about = new Intent(getContext(), AboutActivity.class);
                startActivity(about);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            switch (resultCode)
            {
                case 1:
                    if (data != null)
                    {
                        Bundle bundle = data.getExtras();
                        if (bundle != null)
                        {
                            String result = bundle.getString("logout");
                            if ("y".equals(result))
                            {
                                HomeActivity activity = (HomeActivity) getActivity();
                                if (activity != null)
                                {
                                    activity.logoutView();
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    if (LoginUtil.getLogin())
                    {
                        queryUserInfo();
                    }
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 获取版本
     */
    private void getVersion() {
        try {
            AjaxParams params = new AjaxParams();
            params.put("Platform", "ANDROID-SC");
            MyApplication.http.post("http://118.178.59.142:5488/Boiler.asmx/GetVersion", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    Log.i("版本", o.toString());
                    try {
                        JSONObject object = new JSONObject(o.toString());
                        if (object.getString("resultcode").equals("0")) {
                            final JSONObject ob = object.getJSONObject("data");
                            if (Integer.valueOf(ob.getString("version")) > getVerCode(getContext()))
                            {
                                String info = ob.getString("title");
                                final String updateflag = ob.getString("updateflag");

                                final CustomDialog customDialog = new CustomDialog(getContext());
                                customDialog.setTitle("版本更新");
                                customDialog.setContent(info);
                                customDialog.setOnPositiveListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        try
                                        {
                                            downLoadCs(ob.getString("url"));
                                        }
                                        catch (Exception ex)
                                        {
                                            Log.e("getversion", "e: " + ex);
                                        }
                                    }
                                });
                                customDialog.setOnNegativeListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        if ("1".equals(updateflag))
                                        {
                                            Intent intent = new Intent();
                                            intent.setAction("exit_app");
                                            getActivity().sendBroadcast(intent);
                                        }
                                        else
                                        {
                                            customDialog.dismiss();
                                        }
                                    }
                                });
                                customDialog.show();

                            }
                            else
                            {
                                ToastUtils.s(getContext(), "当前最新版本");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取版本号
    private String getVerName(Context context)
    {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            verName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    //获取版本号
    private static int getVerCode(Context context)
    {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 2;
    }

    private void downLoadCs(String url)
    {  //下载调用
        initProgressBar();
        removeOldApk();
        Intent intent = new Intent(getContext(), Downloadservice.class);
        intent.putExtra(Downloadservice.BUNDLE_KEY_DOWNLOAD_URL, url);
        isBindService = getActivity().bindService(intent, conn, BIND_AUTO_CREATE); //绑定服务即开始下载 调用onBind()
    }

    private void initProgressBar() {
        progress = new ProgressDialog(getActivity());
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setProgress(1);
        progress.setTitle("正在下载……");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    /**
     * 删除上次更新存储在本地的apk
     */
    private void removeOldApk()
    {
        //获取老ＡＰＫ的存储路径
        String f = getActivity().getExternalFilesDir("lihao/") + "/lihaomarket.apk";
        File fileName = new File(f);
        if (fileName != null && fileName.exists() && fileName.isFile()) {
            fileName.delete();
        }
    }

}
