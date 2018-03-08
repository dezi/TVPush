package de.xavaro.android.skills;

import android.view.View;

public interface CanRoundedCorners
{
    void setRoundedCorners(int radius, int color);
    void setRoundedCorners(int radius, int innerColor, int strokeColor);

    void setRoundedCornersDIP(int radiusdip, int color);
    void setRoundedCornersDIP(int radiusdip, int innerColor, int strokeColor);

    void saveBackground();
    void restoreBackground();
}
