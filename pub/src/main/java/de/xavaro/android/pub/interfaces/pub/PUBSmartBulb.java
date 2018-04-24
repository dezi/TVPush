package de.xavaro.android.pub.interfaces.pub;

public interface PUBSmartBulb
{
    boolean setBulbState(int onoff);

    boolean setBulbBrightness(int brightness);

    boolean setBulbRGB(int rgbcolor);
    boolean setBulbRGB(int rgbcolor, int brightness);
    boolean setBulbRGB(int rgbcolor, int brightness, int onoff);

    boolean setBulbHSB(int hue, int saturation);
    boolean setBulbHSB(int hue, int saturation, int brightness);
    boolean setBulbHSB(int hue, int saturation, int brightness, int onoff);
}
