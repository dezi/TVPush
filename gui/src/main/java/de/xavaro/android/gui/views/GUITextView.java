package de.xavaro.android.gui.views;

import android.support.v7.widget.AppCompatTextView;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.util.TypedValue;

import de.xavaro.android.gui.skills.GUICanRestoreBackground;
import de.xavaro.android.gui.skills.GUICanRestoreBackgroundDelegate;
import de.xavaro.android.gui.skills.GUICanRoundedCorners;
import de.xavaro.android.gui.skills.GUICanRoundedCornersDelegate;

public class GUITextView extends AppCompatTextView
        implements GUICanRoundedCorners, GUICanRestoreBackground
{
    public GUITextView(Context context)
    {
        super(context);

        initSkills();
    }

    //region Dip implementation.

    public void setTextSizeDip(int textSizeDip)
    {
        float real = textSizeDip / getContext().getResources().getConfiguration().fontScale;

        setTextSize(TypedValue.COMPLEX_UNIT_DIP, real);
    }

    //endregion Dip implementation.

    //region Skills implementation.

    private GUICanRoundedCornersDelegate canRC;
    private GUICanRestoreBackgroundDelegate canRB;

    private void initSkills()
    {
        canRB = new GUICanRestoreBackgroundDelegate(this);
        canRC = new GUICanRoundedCornersDelegate(this);
    }

    @Override
    public void setBackgroundColor(int color)
    {
        super.setBackgroundColor(color);
        canRB.setBackgroundColor(color);
    }

    @Override
    public int getBackgroundColor()
    {
        return canRB.getBackgroundColor();
    }

    @Override
    public void setBackground(Drawable drawable)
    {
        super.setBackground(drawable);
        canRB.setBackground(drawable);
    }

    @Override
    public Drawable getBackground()
    {
        return canRB.getBackground();
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
    public void setRoundedCornersDip(int radiusdip, int color)
    {
        canRC.setRoundedCornersDip(radiusdip, color);
    }

    @Override
    public void setRoundedCornersDip(int radiusdip, int innerColor, int strokeColor)
    {
        canRC.setRoundedCornersDip(radiusdip, innerColor, strokeColor);
    }

    @Override
    public int getRadius()
    {
        return canRC.getRadius();
    }

    @Override
    public int getRadiusDip()
    {
        return canRC.getRadiusDip();
    }

    @Override
    public int getInnerColor()
    {
        return canRC.getInnerColor();
    }

    @Override
    public int getStrokeColor()
    {
        return canRC.getStrokeColor();
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

    //endregion Skills implementation.
}
