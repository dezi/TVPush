package de.xavaro.android.skills;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

import de.xavaro.android.simple.Simple;

public class CanRoundedCornersDelegate implements CanRoundedCorners
{
    public View view;

    public boolean used;

    public int radius;
    public int innerColor;
    public int strokeColor;

    public int radiusSaved;
    public int innerColorSaved;
    public int strokeColorSaved;

    public CanRoundedCornersDelegate(View view)
    {
        this.view = view;
    }

    public void setRoundedCorners(int radius, int color)
    {
        setRoundedCorners(radius, color, color);
    }

    public void setRoundedCornersDip(int radiusdip, int color)
    {
        setRoundedCorners(Simple.dipToPx(radiusdip), color);
    }

    public void setRoundedCornersDip(int radiusdip, int innerColor, int strokeColor)
    {
        setRoundedCorners(Simple.dipToPx(radiusdip), innerColor, strokeColor);
    }

    public void setRoundedCorners(int radius, int innerColor, int strokeColor)
    {
        used = true;

        this.radius = radius;
        this.innerColor = innerColor;
        this.strokeColor = strokeColor;

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(radius);

        shape.setColor(innerColor);

        if (innerColor != strokeColor)
        {
            shape.setStroke(Simple.dipToPx(2), strokeColor);
        }

        view.setBackground(shape);
    }

    @Override
    public int getRadius()
    {
        return radius;
    }

    @Override
    public int getRadiusDip()
    {
        return Simple.pxToDip(radius);
    }

    @Override
    public int getInnerColor()
    {
        return innerColor;
    }

    @Override
    public int getStrokeColor()
    {
        return strokeColor;
    }

    public void saveBackground()
    {
        radiusSaved = radius;
        innerColorSaved = innerColor;
        strokeColorSaved = strokeColor;
    }

    public void restoreBackground()
    {
        if (used) setRoundedCorners(radiusSaved, innerColorSaved, strokeColorSaved);
    }
}


