package com.lihao.market.Fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lihao.market.Activity.HomeActivity;
import com.lihao.market.Activity.ProductListActivity;
import com.lihao.market.Adapter.ProductBestAdapter;
import com.lihao.market.Adapter.ProductHotAdapter;
import com.lihao.market.Adapter.ProductRecAdapter;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Bean.ItemBean;
import com.lihao.market.Bean.ItemList;
import com.lihao.market.Bean.ProductBean;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Dialog.CustomDialog;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Service.Downloadservice;
import com.lihao.market.Service.LogoSuspendService;
import com.lihao.market.Util.GlideImageLoader;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;


/**
 * 首页
 */
public class HomePageFragment extends BaseFragment implements View.OnClickListener
{
    private static final String ARG_SHOW_TEXT = "text";

    private static float fnow;

    private static float fmax;

    private List<ItemList> itemLists = new ArrayList<>();

    private Banner banner;

    private LinearLayout searchLayout;

    /**
     * 四款主推产品
     */
    private ImageView pic1;

    private ImageView pic2;

    private ImageView pic3;

    private ImageView pic4;

    /**
     * 所有产品类型
     */
    private GridView allGrid;

    private ProductRecAdapter recAdapter;

    /**
     * 精品标题
     */
    private ImageView bestImage;

    /**
     * 精品展示
     */
    private GridView bestGrid;

    private ProductBestAdapter bestAdapter;

    private ImageView parterView;

    private ImageView hotImage;

    private GridView hotGrid;

    private ProgressDialog progress;

    private boolean isBindService;

    private ProductHotAdapter hotAdapter;

    private List<ProductBean> productBeanList = new ArrayList<>();

    private Intent service;

    public HomePageFragment()
    {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance(String param1)
    {
        HomePageFragment homeFragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        homeFragment.setArguments(args);
        return homeFragment;

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
                        if (getActivity() != null)
                        {
                            getActivity().unbindService(conn);
                        }
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
        View tv_home = inflater.inflate(R.layout.fragment_home_page, container, false);

        initView(tv_home);
//        initData();

        recAdapter = new ProductRecAdapter(getContext());
        bestAdapter = new ProductBestAdapter(getContext());
        hotAdapter = new ProductHotAdapter(getContext());

        if (getActivity() != null)
        {
            IntentFilter filter = new IntentFilter("com.broadcast.inithome");
            BroadCastManager.getInstance().registerReceiver(getActivity(), receiver, filter);
        }

        String wxUserName = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.WX_USER_NAME);
        String password = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.PASSWORD);
        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(wxUserName))
        {
            getVersion();
        }

        return tv_home;
    }

    private void initView(View view)
    {
        banner = view.findViewById(R.id.banner);
        searchLayout = view.findViewById(R.id.search);
        pic1 = view.findViewById(R.id.pic1);
        pic2 = view.findViewById(R.id.pic2);
        pic3 = view.findViewById(R.id.pic3);
        pic4 = view.findViewById(R.id.pic4);
        allGrid = view.findViewById(R.id.grid);
        bestImage = view.findViewById(R.id.jingpin);
        bestGrid = view.findViewById(R.id.jingpinview);
        parterView = view.findViewById(R.id.pinpai);
        hotImage = view.findViewById(R.id.remai);
        hotGrid = view.findViewById(R.id.remaiview);

        pic1.setOnClickListener(this);
        pic2.setOnClickListener(this);
        pic3.setOnClickListener(this);
        pic4.setOnClickListener(this);
    }

    private void resolve()
    {
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setImageLoader(new GlideImageLoader());
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        List<String> list = new ArrayList<>();
        List<ItemBean> beans = itemLists.get(0).getItemBeans();
        for (ItemBean itemBean : beans)
        {
            list.add(itemBean.getImg());
        }
        banner.setImages(list);
        banner.start();
    }


    private void initData()
    {
        //获取全量数据
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        try {
            params.put("id", "3");
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=console&c=view&a=view", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        if (object.getString("error").equals("0"))
                        {
                            DefaultHttpClient httpClient = (DefaultHttpClient) MyApplication.http.getHttpClient();
                            MyCookieStore.cookieStore = httpClient.getCookieStore();

                            JSONObject jsonData = object.getJSONObject("view");
                            String data = jsonData.getString("data");
                            JSONArray oba = new JSONArray(data);
                            for (int i = 0; i < oba.length(); i++)
                            {
                                JSONObject op = oba.getJSONObject(i);
                                ItemList itemList = new ItemList();
                                if (!"search".equals(op.getString("module")) && !"line".equals(op.getString("module"))
                                        && !"product".equals(op.getString("module")) && !"tab-down".equals(op.getString("module")))
                                {
                                    itemList.setModule(op.getString("module"));
                                    JSONObject jsonObject = op.getJSONObject("data");
                                    JSONArray array = jsonObject.getJSONArray("list");
                                    List<ItemBean> list = new ArrayList<>();
                                    for (int j = 0; j < array.length(); j++)
                                    {
                                        JSONObject json = array.getJSONObject(j);
                                        ItemBean itemBean = new ItemBean();
                                        itemBean.setImg(json.getString("img"));
//                                        itemBean.setSort(json.getString("sort"));
                                        itemBean.setDesc(json.getString("desc"));
                                        itemBean.setUrl(json.getString("url"));
                                        itemBean.setUrlCatetory(json.getString("urlCatetory"));
                                        itemBean.setUrlName(json.getString("urlName"));
                                        list.add(itemBean);
                                    }
                                    itemList.setItemBeans(list);
                                    itemLists.add(itemList);
                                }
                            }
                        }

                        resolve();
                        mainRecommend();
                        moreRecommend();
                        bestProduct();
                        cooperation();
                        hotProduct();
                        getHotDataFirst();
//                        showHotData();

                    } catch (Exception e)
                    {
                        Log.e("homeAll", e.toString());
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

    /**
     * 4个推荐
     */
    private void mainRecommend()
    {
        List<ItemBean> mainList = itemLists.get(2).getItemBeans();
        if (mainList.size() > 0)
        {
            Glide.with(getContext()).load(mainList.get(0).getImg())
                    .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_middle_common).error(R.mipmap.ic_middle_common)).into(pic1);

            if (mainList.size() > 1)
            {
                Glide.with(getContext()).load(mainList.get(1).getImg())
                        .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_middle_common).error(R.mipmap.ic_middle_common)).into(pic2);
            }

            if (mainList.size() > 1)
            {
                Glide.with(getContext()).load(mainList.get(2).getImg())
                        .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_middle_common).error(R.mipmap.ic_middle_common)).into(pic3);
            }

            if (mainList.size() > 1)
            {
                Glide.with(getContext()).load(mainList.get(3).getImg())
                        .apply(new RequestOptions().centerCrop().fallback(R.mipmap.ic_middle_common).error(R.mipmap.ic_middle_common)).into(pic4);
            }
        }
    }

    private void moreRecommend()
    {
        List<ItemBean> data = itemLists.get(1).getItemBeans();
        int count = data.size();
        int columns = (count % 2 == 0) ? count / 2 : count / 2 + 1;

        int length = 80;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (columns * (length + 5) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        allGrid.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        allGrid.setColumnWidth(itemWidth); // 设置列表项宽
        allGrid.setHorizontalSpacing(5); // 设置列表项水平间距
        allGrid.setStretchMode(GridView.NO_STRETCH);
        if (count <= 5)
        {
            allGrid.setNumColumns(count);
        }
        else
        {
            allGrid.setNumColumns(columns);
        }
        recAdapter.setData(data);
        allGrid.setAdapter(recAdapter);
    }

    private void bestProduct()
    {
        List<ItemBean> data = itemLists.get(3).getItemBeans();
        Glide.with(getContext()).load(data.get(0).getImg())
                .apply(new RequestOptions().centerCrop()).into(bestImage);

        List<ItemBean> bestList = itemLists.get(4).getItemBeans();
        bestAdapter.setData(bestList);
        bestGrid.setAdapter(bestAdapter);
    }

    private void cooperation()
    {
        List<ItemBean> data = itemLists.get(6).getItemBeans();
        Glide.with(getContext()).load(data.get(0).getImg())
                .apply(new RequestOptions().centerCrop()).into(parterView);
    }

    private void hotProduct()
    {
        List<ItemBean> data = itemLists.get(7).getItemBeans();
        Glide.with(getContext()).load(data.get(0).getImg())
                .apply(new RequestOptions().centerCrop()).into(hotImage);

    }

    /**
     * 获取热卖推荐数据
     */
    private void getHotDataFirst()
    {
        AjaxParams params = new AjaxParams();
        try {
            params.put("number", "6");
            params.put("cat_id", "858");
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=console&c=view&a=product", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        JSONObject object = new JSONObject(o.toString());
                        if (object.getString("error").equals("0"))
                        {
                            JSONArray oba = object.getJSONArray("product");
                            for (int i = 0; i < oba.length(); i++)
                            {
                                JSONObject op = oba.getJSONObject(i);
                                ProductBean productBean = new ProductBean();
                                productBean.setGoods_id(op.getString("goods_id"));
                                productBean.setGoods_name(op.getString("goods_name"));
                                productBean.setSale(op.getString("sale"));
                                productBean.setImg(op.getString("img"));
                                productBean.setMarketPrice(op.getString("marketPrice"));
                                productBean.setShop_price(op.getString("shop_price"));
                                productBean.setStock(op.getString("stock"));
                                productBeanList.add(productBean);
                            }
                        }
//                        hotAdapter.setData(productBeanList);
//                        hotGrid.setAdapter(hotAdapter);
                        getHotDataSecond();

                    } catch (Exception e)
                    {
                        Log.e("getHotDataFirst", e.toString());
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

    private void getHotDataSecond()
    {
        AjaxParams params = new AjaxParams();
        try {
            params.put("number", "2");
            params.put("cat_id", "5");
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=console&c=view&a=product", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        if (object.getString("error").equals("0"))
                        {
                            JSONArray oba = object.getJSONArray("product");
                            for (int i = 0; i < oba.length(); i++)
                            {
                                JSONObject op = oba.getJSONObject(i);
                                ProductBean productBean = new ProductBean();
                                productBean.setGoods_id(op.getString("goods_id"));
                                productBean.setGoods_name(op.getString("goods_name"));
                                productBean.setSale(op.getString("sale"));
                                productBean.setImg(op.getString("img"));
                                productBean.setMarketPrice(op.getString("marketPrice"));
                                productBean.setShop_price(op.getString("shop_price"));
                                productBean.setStock(op.getString("stock"));
                                productBeanList.add(productBean);
                            }
                        }
//                        hotAdapter.setData(productBeanList);
//                        hotGrid.setAdapter(hotAdapter);
                        getHotDataThird();

                    } catch (Exception e)
                    {
                        Log.e("getHotDataSecond", e.toString());
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

    private void getHotDataThird()
    {
        AjaxParams params = new AjaxParams();
        try {
            params.put("number", "2");
            params.put("cat_id", "3");
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=console&c=view&a=product", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        if (object.getString("error").equals("0"))
                        {
                            JSONArray oba = object.getJSONArray("product");
                            for (int i = 0; i < oba.length(); i++)
                            {
                                JSONObject op = oba.getJSONObject(i);
                                ProductBean productBean = new ProductBean();
                                productBean.setGoods_id(op.getString("goods_id"));
                                productBean.setGoods_name(op.getString("goods_name"));
                                productBean.setSale(op.getString("sale"));
                                productBean.setImg(op.getString("img"));
                                productBean.setMarketPrice(op.getString("marketPrice"));
                                productBean.setShop_price(op.getString("shop_price"));
                                productBean.setStock(op.getString("stock"));
                                productBeanList.add(productBean);
                            }
                        }
                        hotAdapter.setData(productBeanList);
                        hotGrid.setAdapter(hotAdapter);

                    } catch (Exception e)
                    {
                        Log.e("getHotDataThird", e.toString());
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
    public void onClick(View v)
    {
        if (itemLists.size() < 2)
        {
            return;
        }
        switch (v.getId())
        {
            case R.id.pic1:
                Intent intent1 = new Intent(getContext(), ProductListActivity.class);
                intent1.putExtra(KeySet.GOOD_URL, itemLists.get(2).getItemBeans().get(0).getUrl());
                intent1.putExtra(KeySet.TITLE, itemLists.get(2).getItemBeans().get(0).getUrlName());
                intent1.putExtra(KeySet.FROM_SEARCH, false);
                startActivity(intent1);
                break;
            case R.id.pic2:
                Intent intent2 = new Intent(getContext(), ProductListActivity.class);
                intent2.putExtra(KeySet.GOOD_URL, itemLists.get(2).getItemBeans().get(1).getUrl());
                intent2.putExtra(KeySet.TITLE, itemLists.get(2).getItemBeans().get(1).getUrlName());
                intent2.putExtra(KeySet.FROM_SEARCH, false);
                startActivity(intent2);
                break;
            case R.id.pic3:
                Intent intent3 = new Intent(getContext(), ProductListActivity.class);
                intent3.putExtra(KeySet.GOOD_URL, itemLists.get(2).getItemBeans().get(2).getUrl());
                intent3.putExtra(KeySet.TITLE, itemLists.get(2).getItemBeans().get(2).getUrlName());
                intent3.putExtra(KeySet.FROM_SEARCH, false);
                startActivity(intent3);
                break;
            case R.id.pic4:
                Intent intent4 = new Intent(getContext(), ProductListActivity.class);
                intent4.putExtra(KeySet.GOOD_URL, itemLists.get(2).getItemBeans().get(3).getUrl());
                intent4.putExtra(KeySet.TITLE, itemLists.get(2).getItemBeans().get(3).getUrlName());
                intent4.putExtra(KeySet.FROM_SEARCH, false);
                startActivity(intent4);
            default:
                break;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (getActivity() != null)
        {
            getActivity().stopService(service);
            BroadCastManager.getInstance().unregisterReceiver(getActivity(), receiver);
        }
    }

    /**
     * 获取版本
     */
    private void getVersion() {
        try {
            AjaxParams params = new AjaxParams();
            params.put("screatname", "");
            params.put("screatword", "");
            params.put("sign", "");
            params.put("Platform", "ANDROID-SC");
            MyApplication.http.post("http://118.178.59.142:5488/Boiler.asmx/GetVersion", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    Log.i("版本", o.toString());
                    try {
                        JSONObject object = new JSONObject(o.toString());
//                        initData();
                        if (object.getString("resultcode").equals("0"))
                        {
                            final JSONObject ob = object.getJSONObject("data");
                            if (Integer.valueOf(ob.getString("version")) > getversioncode())
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
                                            initData();
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext()))
                                            {
                                                showKefuDialog();
                                            }
                                            else
                                            {
                                                if (getActivity() != null)
                                                {
                                                    //已经有权限，可以直接显示悬浮窗
                                                    service = new Intent(getContext(), LogoSuspendService.class);
                                                    getActivity().startService(service);
                                                }
                                            }
                                            customDialog.dismiss();
                                        }
                                    }
                                });
                                customDialog.show();

                            }
                            else
                            {
                                initData();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext()))
                                {
                                    showKefuDialog();
                                }
                                else
                                {
                                    if (getActivity() != null)
                                    {
                                        //已经有权限，可以直接显示悬浮窗
                                        service = new Intent(getContext(), LogoSuspendService.class);
                                        getActivity().startService(service);
                                    }
                                }
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

    private void showKefuDialog()
    {
        String show = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.SHOW_KEFU_DIALOG);
        if (!"1".equals(show))
        {
            final CustomDialog kefuDialog = new CustomDialog(getContext());
            kefuDialog.setTitle("提示");
            kefuDialog.setContent("建议打开全局客服入口，方便快捷沟通");
            kefuDialog.setOnPositiveListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (getActivity() != null)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext()))
                        {
                            //没有权限，需要申请权限，因为是打开一个授权页面，所以拿不到返回状态的，所以建议是在onResume方法中从新执行一次校验
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                            startActivityForResult(intent, 100);
                        } else
                        {
                            //已经有权限，可以直接显示悬浮窗
                            service = new Intent(getContext(), LogoSuspendService.class);
                            getActivity().startService(service);
                        }
                    }
                    SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.SHOW_KEFU_DIALOG, "1");
                    kefuDialog.dismiss();
                }
            });
            kefuDialog.setOnNegativeListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    SharedPreferencesUtils.saveSpStringData(SharedPreferencesUtils.SHOW_KEFU_DIALOG, "1");
                    kefuDialog.dismiss();
                }
            });
            kefuDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (getActivity() != null && Settings.canDrawOverlays(getContext()))
                {
                    service = new Intent(getContext(), LogoSuspendService.class);
                    getActivity().startService(service);
                }
            }
        }
    }

    private void initProgressBar()
    {
        progress = new ProgressDialog(getContext());
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setProgress(1);
        progress.setTitle("正在下载……");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void downLoadCs(String url)
    {  //下载调用
        initProgressBar();
        removeOldApk();
        Intent intent = new Intent(getContext(), Downloadservice.class);
        intent.putExtra(Downloadservice.BUNDLE_KEY_DOWNLOAD_URL, url);
        isBindService = getActivity().bindService(intent, conn, BIND_AUTO_CREATE); //绑定服务即开始下载 调用onBind()
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

    private String getversionname()
    {
        PackageManager pm = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getversioncode()
    {
        PackageManager pm = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 2;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int msg = intent.getIntExtra("msg", 0);
            if (msg == 1)
            {
                getVersion();
            }
        }
    };
}
