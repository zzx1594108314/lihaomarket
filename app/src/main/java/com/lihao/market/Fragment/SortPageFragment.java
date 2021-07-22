package com.lihao.market.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Activity.HomeActivity;
import com.lihao.market.Adapter.SortProductAdapter;
import com.lihao.market.Adapter.SortTitleAdapter;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Bean.SortBean;
import com.lihao.market.Bean.SortTitleBean;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类
 */
public class SortPageFragment extends BaseFragment implements SortTitleAdapter.GetListener
{
    private static final String ARG_SHOW_TEXT = "text";

    private List<SortTitleBean> beans = new ArrayList<>();

    private SortTitleAdapter titleAdapter;

    private RecyclerView titleView;

    private LinearLayout searchLayout;

    private RecyclerView productView;

    private SortProductAdapter productAdapter;

    private List<SortBean> sortBeans = new ArrayList<>();

    public SortPageFragment()
    {}

    public static SortPageFragment newInstance(String param1)
    {
        SortPageFragment sortPageFragment = new SortPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        sortPageFragment.setArguments(args);
        return sortPageFragment;
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
        View sort = inflater.inflate(R.layout.fragment_sort_page, container, false);

        initView(sort);
        initListener();
        titleAdapter = new SortTitleAdapter(getContext());
        productAdapter = new SortProductAdapter(getContext());

        initTitle();
        //首次进入展示锅炉控制器
        initData("858");

        return sort;
    }

    private void initView(View view)
    {
        titleView = view.findViewById(R.id.titleView);
        productView = view.findViewById(R.id.product);
        searchLayout = view.findViewById(R.id.search);

        LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
                false);
        titleView.setLayoutManager(LayoutManager);

        LinearLayoutManager LayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
                false);
        productView.setLayoutManager(LayoutManager1);
    }

    private void initListener()
    {
        searchLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                HomeActivity activity = (HomeActivity)getActivity();
//                if (activity != null)
//                {
//                    activity.intentSearch();
//                }
            }
        });
    }

    private void initTitle()
    {
        AjaxParams params = new AjaxParams();
        try
        {
            MyApplication.http.post(KeySet.URL + "/mobile/index.php?m=category&a=simple", params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray array = object.getJSONArray("category");
                        for (int i = 0; i < array.length(); i++)
                        {
                            SortTitleBean titleBean = new SortTitleBean();
                            JSONObject op = array.getJSONObject(i);
                            titleBean.setId(op.getString("cat_id"));
                            titleBean.setName(op.getString("cat_name"));
                            beans.add(titleBean);
                        }
                        titleAdapter.setData(beans);
                        titleView.setAdapter(titleAdapter);

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Throwable t, String strMsg)
                {
                    super.onFailure(t, strMsg);
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        titleAdapter.setGetListener(this);

    }

    private void initData(String id)
    {
        showLoadingDialog();
        AjaxParams params = new AjaxParams();
        try {
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=category&a=getchildcategory&id=" + id, params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    try
                    {
                        hideLoadingDialog();
                        sortBeans.clear();
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray array = object.getJSONArray("category");
                        for (int i = 0; i < array.length(); i++)
                        {
                            List<SortBean> beans = new ArrayList<>();
                            SortBean sortBean = new SortBean();
                            JSONObject op = array.getJSONObject(i);
                            sortBean.setName(op.getString("name"));
                            sortBean.setHaschild(op.getString("haschild"));
                            sortBean.setUrl(op.getString("url"));
                            sortBean.setCat_img(op.getString("cat_img"));
                            if ("1".equals(op.getString("haschild")))
                            {
                                JSONArray jsonArray = op.getJSONArray("cat_id");
                                for (int j = 0; j < jsonArray.length(); j++)
                                {
                                    SortBean bean = new SortBean();
                                    JSONObject json = jsonArray.getJSONObject(j);
                                    bean.setId(json.getString("id"));
                                    bean.setName(json.getString("name"));
                                    bean.setUrl(json.getString("url"));
                                    bean.setCat_img(json.getString("cat_img"));
                                    beans.add(bean);
                                }
                            }
                            sortBean.setSortItems(beans);
                            sortBeans.add(sortBean);
                        }

                        productAdapter.setData(sortBeans);
                        productView.setAdapter(productAdapter);

                    } catch (Exception e)
                    {
                        Log.e("sortPage", e.toString());
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
    public void onClick(int position)
    {
        String id = beans.get(position).getId();
        initData(id);
    }
}
