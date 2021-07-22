package com.lihao.market.Dialog;

public class SizeEntity
{
    private int width;
    private int height;

    public SizeEntity()
    {
    }

    public SizeEntity(int width, int height)
    {
        setWidth(width);
        setHeight(height);
    }


    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

}
