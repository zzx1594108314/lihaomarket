package com.lihao.market.Bean;

import java.io.Serializable;
import java.util.List;

public class ItemList implements Serializable
{
    private static final long serialVersionUID = 1000L;

    private String module;

    private List<ItemBean> itemBeans;

    public String getModule()
    {
        return module;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public List<ItemBean> getItemBeans()
    {
        return itemBeans;
    }

    public void setItemBeans(List<ItemBean> itemBeans)
    {
        this.itemBeans = itemBeans;
    }
}
