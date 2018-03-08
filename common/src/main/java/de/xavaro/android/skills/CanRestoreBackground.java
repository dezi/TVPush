package de.xavaro.android.skills;

import android.graphics.drawable.Drawable;

public interface CanRestoreBackground
{
    void setBackgroundColor(int color);
    void setBackground(Drawable drawable);

    void saveBackground();
    void restoreBackground();
}
