package de.xavaro.android.base;

import android.support.v7.widget.AppCompatTextView;
import android.graphics.drawable.Drawable;
import android.content.Context;

import de.xavaro.android.skills.CanRestoreBackground;
import de.xavaro.android.skills.CanRestoreBackgroundDelegate;
import de.xavaro.android.skills.CanRoundedCorners;
import de.xavaro.android.skills.CanRoundedCornersDelegate;

public class BaseTextView extends AppCompatTextView
        implements CanRoundedCorners, CanRestoreBackground
{
    CanRoundedCornersDelegate canRC;
    CanRestoreBackgroundDelegate canRB;

    public BaseTextView(Context context)
    {
        super(context);

        canRC = new CanRoundedCornersDelegate(this);
        canRB = new CanRestoreBackgroundDelegate(this);
    }

    @Override
    public void setBackgroundColor(int color)
    {
        super.setBackgroundColor(color);
        canRB.setBackgroundColor(color);
    }

    @Override
    public void setBackground(Drawable drawable)
    {
        super.setBackground(drawable);
        canRB.setBackground(drawable);
    }

    @Override
    public void setRoundedCorners(int radius, int color)
    {
        canRC.setRoundedCorners(radius, color);
    }

    @Override
    public void setRoundedCorners(int radius, int innerColor, int strokeColor)
    {
        canRC.setRoundedCorners(radius, innerColor, strokeColor);
    }

    @Override
    public void setRoundedCornersDIP(int radiusdip, int color)
    {
        canRC.setRoundedCornersDIP(radiusdip, color);
    }

    @Override
    public void setRoundedCornersDIP(int radiusdip, int innerColor, int strokeColor)
    {
        canRC.setRoundedCornersDIP(radiusdip, innerColor, strokeColor);
    }

    @Override
    public void saveBackground()
    {
        canRB.saveBackground();
        canRC.saveBackground();
    }

    @Override
    public void restoreBackground()
    {
        canRB.restoreBackground();
        canRC.restoreBackground();
    }
}
