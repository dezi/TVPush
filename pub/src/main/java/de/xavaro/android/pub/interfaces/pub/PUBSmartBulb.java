package de.xavaro.android.pub.interfaces.pub;

public interface PUBSmartBulb
{
    boolean setBulbState(int onoff);

    boolean setBulb(int onoff, int hue, int saturation, int brightness);

    boolean setBulbHSB(int hue, int saturation, int brightness);

    boolean setBulbHSOnly(int hue, int saturation);

    boolean setBulbBrightness(int brightness);
}
