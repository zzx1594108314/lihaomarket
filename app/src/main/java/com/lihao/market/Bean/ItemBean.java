package com.lihao.market.Bean;

import java.io.Serializable;

public class ItemBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String img;

    private String sort;

    private String desc;

    private String url;

    private String urlCatetory;

    private String urlName;

    public String getImg()
    {
        return img;
    }

    public void setImg(String img)
    {
        this.img = img;
    }

    public String getSort()
    {
        return sort;
    }

    public void setSort(String sort)
    {
        this.sort = sort;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrlCatetory()
    {
        return urlCatetory;
    }

    public void setUrlCatetory(String urlCatetory)
    {
        this.urlCatetory = urlCatetory;
    }

    public String getUrlName()
    {
        return urlName;
    }

    public void setUrlName(String urlName)
    {
        this.urlName = urlName;
    }
}
