package de.xavaro.android.gui.base;

import de.xavaro.android.gui.simple.Simple;

public class GUIDefs
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

    public final static int FONTSIZE_SPEECH = Simple.isTablet() ? 24 : 30;

    public final static int COLOR_LIGHT_TRANSPARENT  = 0x22000000;
    public final static int COLOR_MEDIUM_TRANSPARENT = 0x44000000;
    public static final int COLOR_TV_FOCUS           = 0xffffcc00;
    public static final int COLOR_TV_FOCUS_HIGHLIGHT = 0xffff0000;

    public static final Character UTF_LEFT = 0x25c0;
    public static final Character UTF_RIGHT = 0x25b6;
    public static final Character UTF_UP = 0x25b2;
    public static final Character UTF_DOWN = 0x25bc;

    public static final String UTF_REWIND = UTF_LEFT.toString() + UTF_LEFT;
    public static final String UTF_FAST_FORWARD = UTF_RIGHT.toString() + UTF_RIGHT;

    // @formatter:on
}
