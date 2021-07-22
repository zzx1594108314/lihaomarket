package com.lihao.market.Bean;

import java.io.Serializable;
import java.util.List;

public class GoodBean implements Serializable
{
    private static final long serialVersionUID = 5L;

    /**
     * 商品id
     */
    private String goods_id;

    /**
     * 分类id
     */
    private String cat_id;

    /**
     * 产品名称
     */
    private String goods_name;

    /**
     * 产品价格
     */
    private String shop_price;

    /**
     * 销量
     */
    private String sales_volume;

    /**
     * 库存
     */
    private String goods_number;

    /**
     * 可用积分
     */
    private String integral;

    /**
     * 产品详情
     */
    private String goods_desc;

    /**
     * 产品首张图片,大图
     */
    private String goods_img;

    /**
     * 产品首张图片，小图
     */
    private String goods_thumb;

    /**
     * 产品图片
     */
    private List<PicList> picLists;

    /**
     * 产品特性
     */
    private List<PropertyList> propertyLists;

    /**
     * 产品key
     */
    private String productKey;

    /**
     * 产品values
     */
    private List<KindBean> kindBeans;

    /**
     * 收藏数量，是否收藏
     */
    private String collect_count;

    public String getGoods_id()
    {
        return goods_id;
    }

    public void setGoods_id(String goods_id)
    {
        this.goods_id = goods_id;
    }

    public String getCat_id()
    {
        return cat_id;
    }

    public void setCat_id(String cat_id)
    {
        this.cat_id = cat_id;
    }

    public String getGoods_name()
    {
        return goods_name;
    }

    public void setGoods_name(String goods_name)
    {
        this.goods_name = goods_name;
    }

    public String getShop_price()
    {
        return shop_price;
    }

    public void setShop_price(String shop_price)
    {
        this.shop_price = shop_price;
    }

    public String getSales_volume()
    {
        return sales_volume;
    }

    public void setSales_volume(String sales_volume)
    {
        this.sales_volume = sales_volume;
    }

    public String getGoods_number()
    {
        return goods_number;
    }

    public void setGoods_number(String goods_number)
    {
        this.goods_number = goods_number;
    }

    public String getIntegral()
    {
        return integral;
    }

    public void setIntegral(String integral)
    {
        this.integral = integral;
    }

    public String getGoods_desc()
    {
        return goods_desc;
    }

    public void setGoods_desc(String goods_desc)
    {
        this.goods_desc = goods_desc;
    }

    public String getGoods_img()
    {
        return goods_img;
    }

    public void setGoods_img(String goods_img)
    {
        this.goods_img = goods_img;
    }

    public String getGoods_thumb()
    {
        return goods_thumb;
    }

    public void setGoods_thumb(String goods_thumb)
    {
        this.goods_thumb = goods_thumb;
    }

    public List<PicList> getPicLists()
    {
        return picLists;
    }

    public void setPicLists(List<PicList> picLists)
    {
        this.picLists = picLists;
    }

    public List<PropertyList> getPropertyLists()
    {
        return propertyLists;
    }

    public void setPropertyLists(List<PropertyList> propertyLists)
    {
        this.propertyLists = propertyLists;
    }

    public String getProductKey()
    {
        return productKey;
    }

    public void setProductKey(String productKey)
    {
        this.productKey = productKey;
    }

    public List<KindBean> getKindBeans()
    {
        return kindBeans;
    }

    public void setKindBeans(List<KindBean> kindBeans)
    {
        this.kindBeans = kindBeans;
    }

    public String getCollect_count()
    {
        return collect_count;
    }

    public void setCollect_count(String collect_count)
    {
        this.collect_count = collect_count;
    }
}
