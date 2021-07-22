package com.lihao.market.Bean;

import java.io.Serializable;

/**
 * 购物车
 */
public class CartBean implements Serializable
{
    private static final long serialVersionUID = 6L;

    private String rec_id;

    private String user_id;

    private String goods_id;

    private String goods_sn;

    private String product_id;

    private String goods_name;

    private String market_price;

    private String goods_price;

    private String goods_number;

    private String goods_attr;

    private String goods_thumb;

    private String attr_number;

    /**
     * 商品是否下架，失效，1：失效 0：正常
     */
    private String is_invalid;

    private String is_checked;

    public String getRec_id()
    {
        return rec_id;
    }

    public void setRec_id(String rec_id)
    {
        this.rec_id = rec_id;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getGoods_id()
    {
        return goods_id;
    }

    public void setGoods_id(String goods_id)
    {
        this.goods_id = goods_id;
    }

    public String getGoods_sn()
    {
        return goods_sn;
    }

    public void setGoods_sn(String goods_sn)
    {
        this.goods_sn = goods_sn;
    }

    public String getProduct_id()
    {
        return product_id;
    }

    public void setProduct_id(String product_id)
    {
        this.product_id = product_id;
    }

    public String getGoods_name()
    {
        return goods_name;
    }

    public void setGoods_name(String goods_name)
    {
        this.goods_name = goods_name;
    }

    public String getMarket_price()
    {
        return market_price;
    }

    public void setMarket_price(String market_price)
    {
        this.market_price = market_price;
    }

    public String getGoods_price()
    {
        return goods_price;
    }

    public void setGoods_price(String goods_price)
    {
        this.goods_price = goods_price;
    }

    public String getGoods_number()
    {
        return goods_number;
    }

    public void setGoods_number(String goods_number)
    {
        this.goods_number = goods_number;
    }

    public String getGoods_attr()
    {
        return goods_attr;
    }

    public void setGoods_attr(String goods_attr)
    {
        this.goods_attr = goods_attr;
    }

    public String getGoods_thumb()
    {
        return goods_thumb;
    }

    public void setGoods_thumb(String goods_thumb)
    {
        this.goods_thumb = goods_thumb;
    }

    public String getAttr_number()
    {
        return attr_number;
    }

    public void setAttr_number(String attr_number)
    {
        this.attr_number = attr_number;
    }

    public String getIs_invalid()
    {
        return is_invalid;
    }

    public void setIs_invalid(String is_invalid)
    {
        this.is_invalid = is_invalid;
    }

    public String getIs_checked()
    {
        return is_checked;
    }

    public void setIs_checked(String is_checked)
    {
        this.is_checked = is_checked;
    }
}
