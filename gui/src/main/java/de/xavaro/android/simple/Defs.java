package de.xavaro.android.simple;

public class Defs
{
    // @formatter:off

    public static final int PADDING_ZERO     = Simple.isTablet() ?  0 :  0;
    public static final int PADDING_TINY     = Simple.isTablet() ?  4 :  2;
    public static final int PADDING_SMALL    = Simple.isTablet() ?  8 :  4;
    public static final int PADDING_MEDIUM   = Simple.isTablet() ? 16 : 12;
    public static final int PADDING_NORMAL   = Simple.isTablet() ? 24 : 18;
    public static final int PADDING_LARGE    = Simple.isTablet() ? 32 : 26;
    public static final int PADDING_XLARGE   = Simple.isTablet() ? 40 : 30;

    public static final int ROUNDED_SMALL    = Simple.isTablet() ?  8 :  4;
    public static final int ROUNDED_MEDIUM   = Simple.isTablet() ? 16 : 12;
    public static final int ROUNDED_NORMAL   = Simple.isTablet() ? 24 : 18;
    public static final int ROUNDED_XLARGE   = Simple.isTablet() ? 40 : 30;

    public final static int FONTSIZE_LARGE  = Simple.isTablet() ? 36 : 30;
    public final static int FONTSIZE_SPEECH = Simple.isTablet() ? 30 : 30;

    public final static int COLOR_LIGHT_TRANSPARENT = 0x22000000;

    // @formatter:on
}
