package de.xavaro.android.iot.base;

public interface IOTFixed
{
    boolean isFixed();

    void setFixedLat(Double fixedLat);
    void setFixedLon(Double fixedLon);
    void setFixedAlt(Float fixedAlt);

    Double getFixedLat();
    Double getFixedLon();
    Float getFixedAlt();
}
