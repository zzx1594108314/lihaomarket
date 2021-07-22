package com.lihao.market.Bean;

import java.io.Serializable;
import java.util.List;

public class SortBean implements Serializable
{
    private static final long serialVersionUID = 3L;

    private String id;

    private String name;

    private String url;

    private String cat_img;

    private String haschild;

    private List<SortBean> sortItems;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCat_img()
    {
        return cat_img;
    }

    public void setCat_img(String cat_img)
    {
        this.cat_img = cat_img;
    }

    public String getHaschild()
    {
        return haschild;
    }

    public void setHaschild(String haschild)
    {
        this.haschild = haschild;
    }

    public List<SortBean> getSortItems()
    {
        return sortItems;
    }

    public void setSortItems(List<SortBean> sortItems)
    {
        this.sortItems = sortItems;
    }
}
