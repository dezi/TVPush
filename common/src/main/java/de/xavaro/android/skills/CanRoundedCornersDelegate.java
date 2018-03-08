package de.xavaro.android.skills;

import android.view.View;

import de.xavaro.android.simple.Simple;

public class CanRoundedCornersDelegate implements CanRoundedCorners
{
    private View view;

    public CanRoundedCornersDelegate(View view)
    {
        this.view = view;
    }

    public int radius;
    public int innerColor;
    public int strokeColor;

    public int radiusSaved;
    public int innerColorSaved;
    public int strokeColorSaved;

    public void setRoundedCorners(int radius, int color)
    {
        setRoundedCorners(radius, color, color);
    }

    public void setRoundedCornersDIP(int radiusdip, int color)
    {
        setRoundedCorners(Simple.dipToPx(radiusdip), color);
    }

    public void setRoundedCornersDIP(int radiusdip, int innerColor, int strokeColor)
    {
        setRoundedCorners(Simple.dipToPx(radiusdip), innerColor, strokeColor);
    }

    public void setRoundedCorners(int radius, int innerColor, int strokeColor)
    {
        this.radius = radius;
        this.innerColor = innerColor;
        this.strokeColor = strokeColor;
    }

    public void saveBackground()
    {
        radiusSaved = radius;
        innerColorSaved = innerColor;
        strokeColorSaved = strokeColor;
    }

    public void restoreBackground()
    {
        setRoundedCorners(radius, innerColor, strokeColor);
    }
}


