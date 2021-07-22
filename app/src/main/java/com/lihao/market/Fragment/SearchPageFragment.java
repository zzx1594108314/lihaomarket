package com.lihao.market.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Activity.ProductListActivity;
import com.lihao.market.Adapter.KeywordItemAdapter;
import com.lihao.market.Adapter.RecentSearchAdapter;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Http.MyCookieStore;
import com.lihao.market.MyApplication;
import com.lihao.market.R;
import com.lihao.market.Util.KeySet;
import com.lihao.market.Util.SharedPreferencesUtils;
import com.lihao.market.Util.ToastUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 搜索
 */
public class SearchPageFragment extends BaseFragment implements View.OnClickListener
{
    private static final String ARG_SHOW_TEXT = "text";

    private EditText searchEt;

    private TextView searchTv;

    private GridView recentView;

    private ImageView deleteIv;

    private RecyclerView searchView;

    private LinearLayout recentLayout;

    private KeywordItemAdapter itemAdapter;

    private RecentSearchAdapter searchAdapter;

    public SearchPageFragment()
    {}

    public static SearchPageFragment newInstance(String param1)
    {
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        searchPageFragment.setArguments(args);
        return searchPageFragment;
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
        View search = inflater.inflate(R.layout.fragment_search_page, container, false);

        initView(search);
        initListener();
        initData();

        return search;
    }

    private void initView(View view)
    {
        searchEt = view.findViewById(R.id.search_edit);
        searchTv = view.findViewById(R.id.searchtv);
        recentView = view.findViewById(R.id.recent_grid);
        deleteIv = view.findViewById(R.id.delete_recent);
        searchView = view.findViewById(R.id.search_item);
        recentLayout = view.findViewById(R.id.recent_layout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
                false);
        searchView.setLayoutManager(linearLayoutManager);
        itemAdapter = new KeywordItemAdapter(getContext());
        searchView.setAdapter(itemAdapter);

        searchAdapter = new RecentSearchAdapter(getContext());
    }

    private void initListener()
    {
        searchTv.setOnClickListener(this);
        deleteIv.setOnClickListener(this);

        searchEt.addTextChangedListener(new TextWatcher()
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
                    showKeyWords(s.toString());
                    recentLayout.setVisibility(View.GONE);
                    searchView.setVisibility(View.VISIBLE);
                }
                else
                {
                    recentLayout.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initData()
    {
        Set<String> data = SharedPreferencesUtils.getSpListData(SharedPreferencesUtils.RECENT_SEARCH);
        List<String> list = new ArrayList<>(data);
        searchAdapter.setData(list);
        recentView.setAdapter(searchAdapter);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.searchtv:
                Intent intent = new Intent(getContext(), ProductListActivity.class);
                intent.putExtra(KeySet.TITLE, "搜索");
                intent.putExtra(KeySet.FROM_SEARCH, true);
                intent.putExtra(KeySet.KEYWORD, searchEt.getText().toString());
                startActivity(intent);

                List<String> newData = new ArrayList<>();
                newData.add(searchEt.getText().toString());
                Set<String> data = SharedPreferencesUtils.getSpListData(SharedPreferencesUtils.RECENT_SEARCH);
                List<String> list = new ArrayList<>(data);
                if (list.size() > 8)
                {
                    list = list.subList(0, 8);
                }
                newData.addAll(list);

                Set<String> save = new HashSet<>(newData);
                SharedPreferencesUtils.saveSpListData(SharedPreferencesUtils.RECENT_SEARCH, save);
                break;
            case R.id.delete_recent:
                SharedPreferencesUtils.clearValue(SharedPreferencesUtils.RECENT_SEARCH);
                clearRecentSearch();
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);

        if (!hidden)
        {
            clearRecentSearch();
        }
    }

    private void clearRecentSearch()
    {
        Set<String> data = SharedPreferencesUtils.getSpListData(SharedPreferencesUtils.RECENT_SEARCH);
        List<String> list = new ArrayList<>(data);
        if (searchAdapter != null)
        {
            searchAdapter.setData(list);
            recentView.setAdapter(searchAdapter);
        }
    }

    private void showKeyWords(String word)
    {
        try {
            AjaxParams params = new AjaxParams();
            MyApplication.http.configCookieStore(MyCookieStore.cookieStore);
            MyApplication.http.get(KeySet.URL + "/mobile/index.php?m=search&a=getsearch_keyword&kwords=" + Uri.encode(word), params, new AjaxCallBack<Object>()
            {
                @Override
                public void onSuccess(Object o)
                {
                    super.onSuccess(o);
                    try
                    {
                        List<String> item = new ArrayList<>();
                        JSONObject object = new JSONObject(o.toString());
                        JSONArray array = object.getJSONArray("keywords_list");
                        for (int i = 0; i < array.length(); i++)
                        {
                            JSONObject op = array.getJSONObject(i);
                            item.add(op.getString("keyword"));
                        }
                        if (itemAdapter != null)
                        {
                            itemAdapter.setData(item);
                        }

                    } catch (Exception e)
                    {
                        Log.e("login", e.toString());
                    }
                }

                @Override
                public void onFailure(Throwable t, String strMsg)
                {
                    super.onFailure(t, strMsg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
