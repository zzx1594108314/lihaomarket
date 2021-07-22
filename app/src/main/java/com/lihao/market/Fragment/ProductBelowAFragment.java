package com.lihao.market.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.market.Adapter.ProductDetailImageAdapter;
import com.lihao.market.Base.BaseFragment;
import com.lihao.market.Bean.GoodBean;
import com.lihao.market.R;

import java.util.ArrayList;
import java.util.List;

public class ProductBelowAFragment extends BaseFragment
{
    private RecyclerView recyclerView;

    private ProductDetailImageAdapter adapter;

    private List<String> list = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_product_below_a, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new ProductDetailImageAdapter(getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        if (adapter != null)
        {
            adapter.setData(list);
        }

        return view;
    }

    public void setGoodData(GoodBean bean)
    {
        String desc = bean.getGoods_desc();
//        String url = desc.substring(desc.lastIndexOf("src="), desc.lastIndexOf("title"));
//        urlPath = KeySet.URL + url.substring(url.lastIndexOf("/i"), url.lastIndexOf("\""));
        list = getPicList(desc);
    }

    private List<String> getPicList(String s)
    {
        List<String> mylist = new ArrayList<>();
        List<String> list = new ArrayList<>();
        int start,end,start1,end1;
        start=start1=-1;
        end=end1=0;
        s=s.replace(" ","");
        for (int i=0;i<s.length()-4;i++)
        {
            String a=s.substring(i,i+3);
            if(s.substring(i,i+3).equals("<p>"))start=i;
            String a1=s.substring(i,i+4);
            if(s.substring(i,i+4).equals("</p>")) {
                end = i+4;
                //String s1=s.substring(start,end);
                for(int j=start;j<end-5;j++)
                {
                    if(s.substring(j,j+5).equals("src=\""))
                    {
                        start1 = j;
                    }
                    if(start1 != -1 && start1 != j && s.substring(j, j+5).equals("style"))
                    {
                        end1 = j;
                        mylist.add((s.substring(start1, end1)).trim());
                        start1 =-1;
                        end1 = 0;
                    }
                }
                start=-1;
                end=0;
            }
        }
        String urlPath;
        for (String image : mylist)
        {
            urlPath = image.substring(image.lastIndexOf("/image"), image.lastIndexOf("\""));
            list.add(urlPath);
        }
        return list;
    }
}
