package com.lihao.market.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lihao.market.Base.BaseActivity;
import com.lihao.market.Broadcast.BroadCastManager;
import com.lihao.market.Fragment.CartPageFragment;
import com.lihao.market.Fragment.HomePageFragment;
import com.lihao.market.Fragment.PersonPageFragment;
import com.lihao.market.Fragment.PersonPageLoginFragment;
import com.lihao.market.Fragment.SearchPageFragment;
import com.lihao.market.Fragment.SortPageFragment;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Service.LogoSuspendService;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.LoginUtil;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;
import com.wby.utility.ToastUtil;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;


/**
 * 主页面 容纳首页，分类，搜索，，购物车，我的
 */
public class HomeActivity extends BaseActivity
{
    private RadioGroup mTabRadioGroup;

    private SparseArray<Fragment> mFragmentSparseArray;

    private HomePageFragment homePageFragment;

    private SortPageFragment sortPageFragment;

    private CartPageFragment cartPageFragment;

    private SearchPageFragment searchPageFragment;

    private PersonPageLoginFragment personPageLoginFragment;

    private PersonPageFragment personPageFragment;

    private RadioButton homeButton;

    private RadioButton sortButton;

    private RadioButton searchButton;

    private RadioButton cartButton;

    private RadioButton personButton;

    private long exitTime = 0;

    private int fragmentFlag = 0;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_home;
    }

    @Override
    public void initView()
    {
        mTabRadioGroup = findViewById(R.id.tabs_rg);
        homeButton = findViewById(R.id.rb_fragment_homepage);
        sortButton = findViewById(R.id.rb_fragment_sort);
        searchButton = findViewById(R.id.rb_fragment_search);
        cartButton = findViewById(R.id.rb_fragment_cart);
        personButton = findViewById(R.id.rb_fragment_person);

        mFragmentSparseArray = new SparseArray<>();

        Drawable drawableFirst = getResources().getDrawable(R.drawable.home_home);
        drawableFirst.setBounds(0, 0, 70, 70);//第一0是距左右边距离，第二0是距上下边距离，第三50长度,第四宽度50
        homeButton.setCompoundDrawables(null, drawableFirst, null, null);//只放上面

        Drawable drawableSearch = getResources().getDrawable(R.drawable.sort_sort);
        drawableSearch.setBounds(0, 0, 70, 70);//第一0是距左右边距离，第二0是距上下边距离，第三50长度,第四宽度50
        sortButton.setCompoundDrawables(null, drawableSearch, null, null);//只放上面

        Drawable drawableMe = getResources().getDrawable(R.drawable.search_search);
        drawableMe.setBounds(0, 0, 70, 70);//第一0是距左右边距离，第二0是距上下边距离，第三50长度,第四宽度50
        searchButton.setCompoundDrawables(null, drawableMe, null, null);//只放上面


        Drawable chaxun = getResources().getDrawable(R.drawable.cart_cart);
        chaxun.setBounds(0, 0, 70, 70);//第一0是距左右边距离，第二0是距上下边距离，第三50长度,第四宽度50
        cartButton.setCompoundDrawables(null, chaxun, null, null);//只放上面

        Drawable person = getResources().getDrawable(R.drawable.person_person);
        person.setBounds(0, 0, 70, 70);//第一0是距左右边距离，第二0是距上下边距离，第三50长度,第四宽度50
        personButton.setCompoundDrawables(null, person, null, null);//只放上面

    }

    @Override
    public void initData()
    {
        //添加标题显示
        homePageFragment = HomePageFragment.newInstance("首页");
        sortPageFragment = SortPageFragment.newInstance("分类");
        searchPageFragment = SearchPageFragment.newInstance("搜索");
        cartPageFragment = CartPageFragment.newInstance("购物车");
        personPageLoginFragment = PersonPageLoginFragment.newInstance("我");

        mFragmentSparseArray.append(R.id.rb_fragment_homepage, homePageFragment);
        mFragmentSparseArray.append(R.id.rb_fragment_sort, sortPageFragment);
        mFragmentSparseArray.append(R.id.rb_fragment_search, searchPageFragment);
        mFragmentSparseArray.append(R.id.rb_fragment_cart, cartPageFragment);
        mFragmentSparseArray.append(R.id.rb_fragment_person, personPageLoginFragment);

        // 默认显示第一个 首页界面
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mFragmentSparseArray.get(R.id.rb_fragment_homepage)).commit();

        String wxUserName = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.WX_USER_NAME);
        String password = SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.PASSWORD);
        if (TextUtils.isEmpty(password))
        {
            if (!TextUtils.isEmpty(wxUserName))
            {
                authLoginWx(wxUserName);
            }
        }
        else
        {
            autoLogin();
        }

        Intent intent = getIntent();
        if (intent != null)
        {
            fragmentFlag = intent.getIntExtra("fragment_flag", 0);
            if (fragmentFlag == 1)
            {
                mTabRadioGroup.check(cartButton.getId());
                if (!cartPageFragment.isAdded())
                {
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(R.id.rb_fragment_cart)).commit();
                }
                getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(R.id.rb_fragment_cart))
                        .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                        .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                        .hide(mFragmentSparseArray.get(R.id.rb_fragment_person))
                        .hide(mFragmentSparseArray.get(R.id.rb_fragment_search)).commit();
            }
            else if (fragmentFlag == 2)
            {
                mTabRadioGroup.check(personButton.getId());
                reLoadFragView();
            }
            else if (fragmentFlag == 3)
            {
                mTabRadioGroup.check(searchButton.getId());
                if (!searchPageFragment.isAdded())
                {
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(R.id.rb_fragment_search)).commit();
                }
                getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(R.id.rb_fragment_search))
                        .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                        .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                        .hide(mFragmentSparseArray.get(R.id.rb_fragment_person))
                        .hide(mFragmentSparseArray.get(R.id.rb_fragment_cart)).commit();
            }
        }

        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 具体的fragment切换逻辑可以根据应用调整，例如使用show()/hide()
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        mFragmentSparseArray.get(checkedId)).commit();

                switch (checkedId)
                {
                    case R.id.rb_fragment_homepage:
                        if (!homePageFragment.isAdded())
                        {
                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(checkedId)).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(checkedId))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_search))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_cart))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_person)).commit();
                        break;
                    case R.id.rb_fragment_sort:
                        if (!sortPageFragment.isAdded())
                        {
                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(checkedId)).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(checkedId))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_search))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_person))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_cart)).commit();
                        break;
                    case R.id.rb_fragment_search:
                        if (!searchPageFragment.isAdded())
                        {
                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(checkedId)).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(checkedId))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_person))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_cart)).commit();
                        break;
                    case R.id.rb_fragment_cart:
                        if (!cartPageFragment.isAdded())
                        {
                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(checkedId)).commit();
                        }
                        getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(checkedId))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_person))
                                .hide(mFragmentSparseArray.get(R.id.rb_fragment_search)).commit();
                        break;
                    case R.id.rb_fragment_person:
                        if (LoginUtil.getLogin())
                        {
                            if (personPageFragment == null)
                            {
                                personPageFragment = PersonPageFragment.newInstance("我");
                                mFragmentSparseArray.put(R.id.rb_fragment_person, personPageFragment);
                            }

                            if (!personPageFragment.isAdded())
                            {
                                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(R.id.rb_fragment_person)).commit();
                            }
                            getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(R.id.rb_fragment_person))
                                    .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                                    .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                                    .hide(mFragmentSparseArray.get(R.id.rb_fragment_search))
                                    .hide(mFragmentSparseArray.get(R.id.rb_fragment_cart)).commit();
                        }
                        else
                        {
                            if (!personPageLoginFragment.isAdded())
                            {
                                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(checkedId)).commit();
                            }
                            getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(checkedId))
                                    .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                                    .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                                    .hide(mFragmentSparseArray.get(R.id.rb_fragment_search))
                                    .hide(mFragmentSparseArray.get(R.id.rb_fragment_cart)).commit();
                        }
                        break;
                    default:
                        break;
                }

            }
        });

    }

    @Override
    public void initListener()
    {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                ToastUtils.s(this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            }
            else
            {
                Intent intent = new Intent();
                intent.setAction("exit_app");
                sendBroadcast(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void reLoadFragView()
    {
        personPageFragment = PersonPageFragment.newInstance("我");
        mFragmentSparseArray.put(R.id.rb_fragment_person, personPageFragment);

        /*从FragmentManager中移除*/
        getSupportFragmentManager().beginTransaction().remove(personPageLoginFragment).commit();

        if (!personPageFragment.isAdded())
        {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(R.id.rb_fragment_person)).commit();
        }
        getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(R.id.rb_fragment_person))
                .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                .hide(mFragmentSparseArray.get(R.id.rb_fragment_search))
                .hide(mFragmentSparseArray.get(R.id.rb_fragment_cart)).commit();
    }

    public void intentSearch()
    {
        mTabRadioGroup.check(searchButton.getId());
    }

    public void logoutView()
    {
        if (personPageLoginFragment == null)
        {
            personPageLoginFragment = PersonPageLoginFragment.newInstance("我");
        }
        mFragmentSparseArray.put(R.id.rb_fragment_person, personPageLoginFragment);

        /*从FragmentManager中移除*/
        getSupportFragmentManager().beginTransaction().remove(personPageFragment).commit();

        if (!personPageLoginFragment.isAdded())
        {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragmentSparseArray.get(R.id.rb_fragment_person)).commit();
        }
        getSupportFragmentManager().beginTransaction().show(mFragmentSparseArray.get(R.id.rb_fragment_person))
                .hide(mFragmentSparseArray.get(R.id.rb_fragment_homepage))
                .hide(mFragmentSparseArray.get(R.id.rb_fragment_sort))
                .hide(mFragmentSparseArray.get(R.id.rb_fragment_search))
                .hide(mFragmentSparseArray.get(R.id.rb_fragment_cart)).commit();
    }

    private void authLoginWx(String wxUser)
    {
        AjaxParams params = new AjaxParams();
        try {
            params.put("username", wxUser);
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login&a=getdologin", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        Intent intent = new Intent("com.broadcast.inithome");
                        intent.putExtra("msg", 1);
                        BroadCastManager.getInstance().sendBroadCast(HomeActivity.this, intent);
                        personPageFragment = PersonPageFragment.newInstance("我");
                        mFragmentSparseArray.put(R.id.rb_fragment_person, personPageFragment);
                        LoginUtil.setLogin(true);

                    } catch (Exception e)
                    {
                        Log.e("authLoginWx", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                    Intent intent = new Intent("com.broadcast.inithome");
                    intent.putExtra("msg", 1);
                    BroadCastManager.getInstance().sendBroadCast(HomeActivity.this, intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoLogin()
    {
        AjaxParams params = new AjaxParams();
        try {
            params.put("username", SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.ACCOUNT));
            params.put("password", SharedPreferencesUtils.getSpStringData(SharedPreferencesUtils.PASSWORD));
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=user&c=login", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        Intent intent = new Intent("com.broadcast.inithome");
                        intent.putExtra("msg", 1);
                        BroadCastManager.getInstance().sendBroadCast(HomeActivity.this, intent);

                        JSONObject object = new JSONObject(o.toString());
                        String result = object.getString("status");
                        if ("y".equals(result))
                        {
                            personPageFragment = PersonPageFragment.newInstance("我");
                            mFragmentSparseArray.put(R.id.rb_fragment_person, personPageFragment);
                            LoginUtil.setLogin(true);
                        }
                        else
                        {
                            LoginUtil.setLogin(false);
                        }

                    } catch (Exception e)
                    {
                        Log.e("autoLogin", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                    super.onFailure(t, strMsg);
                    hideLoadingDialog();
                    Intent intent = new Intent("com.broadcast.inithome");
                    intent.putExtra("msg", 1);
                    BroadCastManager.getInstance().sendBroadCast(HomeActivity.this, intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
