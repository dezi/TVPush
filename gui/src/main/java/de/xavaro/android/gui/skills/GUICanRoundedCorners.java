package de.xavaro.android.gui.skills;

public interface GUICanRoundedCorners
{
    void setRoundedCorners(int radius, int color);
    void setRoundedCorners(int radius, int innerColor, int strokeColor);

    void setRoundedCornersDip(int radiusdip, int color);
    void setRoundedCornersDip(int radiusdip, int innerColor, int strokeColor);

    int getRadius();
    int getRadiusDip();

    int getInnerColor();
    int getStrokeColor();

    void saveBackground();
    void restoreBackground();
}
