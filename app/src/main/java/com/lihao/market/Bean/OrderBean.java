package com.lihao.market.Bean;

import java.io.Serializable;
import java.util.List;

public class OrderBean implements Serializable
{
    private static final long serialVersionUID = 7L;

    private String order_id;

    private String order_sn;

    private String order_time;

    private String order_status;

    /**
     * 收货人
     */
    private String consignee;

    private List<GoodBean> order_goods;

    private String order_goods_num;

    private String address;

    private String address_detail;

    private String total_fee;

    private String order_url;

    /**
     * 0：待付款 2：待收货
     */
    private String pay_status;

    //订单取消
    private String order_del;

    private String delete_del;

    private String mobile;

    private String integral;

    private String formated_integral_money;

    private String formated_order_amount;

    public String getOrder_id()
    {
        return order_id;
    }

    public void setOrder_id(String order_id)
    {
        this.order_id = order_id;
    }

    public String getOrder_sn()
    {
        return order_sn;
    }

    public void setOrder_sn(String order_sn)
    {
        this.order_sn = order_sn;
    }

    public String getOrder_time()
    {
        return order_time;
    }

    public void setOrder_time(String order_time)
    {
        this.order_time = order_time;
    }

    public String getOrder_status()
    {
        return order_status;
    }

    public void setOrder_status(String order_status)
    {
        this.order_status = order_status;
    }

    public String getConsignee()
    {
        return consignee;
    }

    public void setConsignee(String consignee)
    {
        this.consignee = consignee;
    }

    public List<GoodBean> getOrder_goods()
    {
        return order_goods;
    }

    public void setOrder_goods(List<GoodBean> order_goods)
    {
        this.order_goods = order_goods;
    }

    public String getOrder_goods_num()
    {
        return order_goods_num;
    }

    public void setOrder_goods_num(String order_goods_num)
    {
        this.order_goods_num = order_goods_num;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress_detail()
    {
        return address_detail;
    }

    public void setAddress_detail(String address_detail)
    {
        this.address_detail = address_detail;
    }

    public String getTotal_fee()
    {
        return total_fee;
    }

    public void setTotal_fee(String total_fee)
    {
        this.total_fee = total_fee;
    }

    public String getOrder_url()
    {
        return order_url;
    }

    public void setOrder_url(String order_url)
    {
        this.order_url = order_url;
    }

    public String getPay_status()
    {
        return pay_status;
    }

    public void setPay_status(String pay_status)
    {
        this.pay_status = pay_status;
    }

    public String getOrder_del()
    {
        return order_del;
    }

    public void setOrder_del(String order_del)
    {
        this.order_del = order_del;
    }

    public String getDelete_del()
    {
        return delete_del;
    }

    public void setDelete_del(String delete_del)
    {
        this.delete_del = delete_del;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getIntegral()
    {
        return integral;
    }

    public void setIntegral(String integral)
    {
        this.integral = integral;
    }

    public String getFormated_integral_money()
    {
        return formated_integral_money;
    }

    public void setFormated_integral_money(String formated_integral_money)
    {
        this.formated_integral_money = formated_integral_money;
    }

    public String getFormated_order_amount()
    {
        return formated_order_amount;
    }

    public void setFormated_order_amount(String formated_order_amount)
    {
        this.formated_order_amount = formated_order_amount;
    }
}
