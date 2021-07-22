package com.lihao.market.Bean;

import java.util.List;

public class JoinCartBean
{
    private String warehouse_id;

    private String area_id;

    private int quick;

    private List<String> spec;

    private int goods_id;

    private int store_id;

    private String number;

    private int parent;

    private int cart_type;

    public String getWarehouse_id()
    {
        return warehouse_id;
    }

    public void setWarehouse_id(String warehouse_id)
    {
        this.warehouse_id = warehouse_id;
    }

    public String getArea_id()
    {
        return area_id;
    }

    public void setArea_id(String area_id)
    {
        this.area_id = area_id;
    }

    public int getQuick()
    {
        return quick;
    }

    public void setQuick(int quick)
    {
        this.quick = quick;
    }

    public List<String> getSpec()
    {
        return spec;
    }

    public void setSpec(List<String> spec)
    {
        this.spec = spec;
    }

    public int getGoods_id()
    {
        return goods_id;
    }

    public void setGoods_id(int goods_id)
    {
        this.goods_id = goods_id;
    }

    public int getStore_id()
    {
        return store_id;
    }

    public void setStore_id(int store_id)
    {
        this.store_id = store_id;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public int getParent()
    {
        return parent;
    }

    public void setParent(int parent)
    {
        this.parent = parent;
    }

    public int getCart_type()
    {
        return cart_type;
    }

    public void setCart_type(int cart_type)
    {
        this.cart_type = cart_type;
    }
}
