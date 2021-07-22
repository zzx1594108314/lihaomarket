package com.lihao.market.Bean;

public class PointBean
{
    private static final long serialVersionUID = 9L;

    private String log_id;

    private String pay_points;

    private String change_time;

    private String change_desc;

    private String type;

    public String getLog_id()
    {
        return log_id;
    }

    public void setLog_id(String log_id)
    {
        this.log_id = log_id;
    }

    public String getPay_points()
    {
        return pay_points;
    }

    public void setPay_points(String pay_points)
    {
        this.pay_points = pay_points;
    }

    public String getChange_time()
    {
        return change_time;
    }

    public void setChange_time(String change_time)
    {
        this.change_time = change_time;
    }

    public String getChange_desc()
    {
        return change_desc;
    }

    public void setChange_desc(String change_desc)
    {
        this.change_desc = change_desc;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
