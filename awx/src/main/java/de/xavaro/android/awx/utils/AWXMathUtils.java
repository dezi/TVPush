package de.xavaro.android.awx.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AWXMathUtils
{
    public static NumberFormat twoDecimalsFormat = new DecimalFormat("#.##");

    public static double round(double value, int decimalCount)
    {
        double factor = Math.pow(10.0d, (double) decimalCount);
        return ((double) Math.round(value * factor)) / factor;
    }

    public static int percentToValue(int percent, int min, int max)
    {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        return Math.round(((float) min) + (((float) ((max - min) * percent)) / 100.0f));
    }

    public static int valueToPercent(int value, int min, int max)
    {
        if (value < min) value = min;
        if (value > max) value = max;

        return ((value - min) * 100) / (max - min);
    }

    public static String getFloatAsString(double value, NumberFormat format)
    {
        return format.format(value);
    }
}
