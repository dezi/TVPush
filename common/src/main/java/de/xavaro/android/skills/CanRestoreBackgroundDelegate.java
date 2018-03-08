package de.xavaro.android.skills;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

public class CanRestoreBackgroundDelegate implements CanRestoreBackground
{
    private View view;

    private int color = Color.TRANSPARENT;
    private Drawable drawable;

    private int colorSaved;
    private Drawable drawableSaved;

    public CanRestoreBackgroundDelegate(View view)
    {
        this.view = view;
    }

    @Override
    public void setBackgroundColor(int color)
    {
        this.color = color;
    }

    @Override
    public void setBackground(Drawable drawable)
    {
        this.drawable = drawable;
    }

    @Override
    public void saveBackground()
    {
        colorSaved = color;
        drawableSaved = drawable;
    }

    @Override
    public void restoreBackground()
    {
        if (drawableSaved != null)
        {
            view.setBackground(drawableSaved);
        }
        else
        {
            view.setBackgroundColor(colorSaved);
        }
    }


}
