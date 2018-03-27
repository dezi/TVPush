package de.xavaro.android.gui.base;

import android.graphics.Color;

import de.xavaro.android.gui.simple.Simple;

public class GUIDefs
{
    // @formatter:off

    //
    // User preferences.
    //

    public final static int MAP_INITIAL_ZOOM = 20;
    public final static double MAP_MOVE_STEP = 0.000002;

    public static final int PADDING_ZERO     = Simple.isTablet() ?  0 :  0;
    public static final int PADDING_TINY     = Simple.isTablet() ?  4 :  2;
    public static final int PADDING_SMALL    = Simple.isTablet() ?  8 :  4;
    public static final int PADDING_MEDIUM   = Simple.isTablet() ? 16 : 12;
    public static final int PADDING_NORMAL   = Simple.isTablet() ? 24 : 18;
    public static final int PADDING_LARGE    = Simple.isTablet() ? 32 : 26;
    public static final int PADDING_XLARGE   = Simple.isTablet() ? 40 : 30;

    public static final int ROUNDED_ZERO     = Simple.isTablet() ?  0 :  0;
    public static final int ROUNDED_SMALL    = Simple.isTablet() ?  8 :  4;
    public static final int ROUNDED_MEDIUM   = Simple.isTablet() ? 16 : 12;
    public static final int ROUNDED_NORMAL   = Simple.isTablet() ? 20 : 18;
    public static final int ROUNDED_XLARGE   = Simple.isTablet() ? 40 : 30;

    public final static int FONTSIZE_LARGE   = Simple.isTablet() ? 36 : 30;
    public final static int FONTSIZE_SPEECH  = Simple.isTablet() ? 24 : 30;
    public final static int FONTSIZE_HEADERS = Simple.isTablet() ? 16 : 16;
    public final static int FONTSIZE_INFOS   = Simple.isTablet() ? 14 : 12;
    public final static int FONTSIZE_BUTTONS = Simple.isTablet() ? 16 : 16;
    public final static int FONTSIZE_TITLE   = Simple.isTablet() ? 18 : 16;

    public final static int COLOR_LIGHT_TRANSPARENT  = 0x22000000;
    public final static int COLOR_MEDIUM_TRANSPARENT = 0x44000000;
    public final static int COLOR_NORMAL_TRANSPARENT = 0x66000000;
    public final static int COLOR_DARK_TRANSPARENT   = 0x88000000;
    public static final int COLOR_BACKGROUND_DIM     = 0x77000000;
    public static final int COLOR_LIGHT_GRAY         = 0xffcccccc;

    public final static int COLOR_PLUGIN_INNER_TRANSPARENT = 0x88888888;
    public final static int COLOR_PLUGIN_FRAME_NORMAL      = 0xffffffff;
    public final static int COLOR_PLUGIN_FRAME_HIGHLIGHT   = 0xffffffaa;

    public static final int COLOR_TV_FOCUS           = 0xffffcc00;
    public static final int COLOR_TV_FOCUS_HIGHLIGHT = 0xffff0000;

    public final static int TEXT_COLOR_HEADERS = Color.BLACK;
    public final static int TEXT_COLOR_INFOS   = Color.BLACK;
    public final static int TEXT_COLOR_ALERTS  = Color.RED;
    public final static int TEXT_COLOR_SPECIAL = Color.BLUE;

    public final static int STATUS_COLOR_RED   = 0xaaaa0000;
    public final static int STATUS_COLOR_GREEN = 0xaa00aa00;
    public final static int STATUS_COLOR_BLUE  = 0xaa0000aa;
    public final static int STATUS_COLOR_INACT = 0xff666666;

    public static final int MIN_EMS_DIALOGS = Simple.isTablet() ? 20 : 12;
    public static final int MAX_EMS_DIALOGS = Simple.isTablet() ? 20 : 12;

    public static final int ICON_PADD       = PADDING_SMALL;
    public static final int ICON_SIZE       = Simple.isTablet() ?  50 :  50;
    public static final int CROSSHAIR_SIZE  = Simple.isTablet() ? 150 : 150;

    //
    // Nice characters.
    //

    public static final Character UTF_OK           = 0x2316;

    public static final Character UTF_LEFT         = 0x27A1; // 0x25c0;
    public static final Character UTF_RIGHT        = 0x2B05; // 0x25b6;
    public static final Character UTF_UP           = 0x2B06; // 0x25b2;
    public static final Character UTF_DOWN         = 0x2B07; // 0x25bc;

    public static final Character UTF_REWIND       = 0x23EA; // "" + UTF_LEFT + UTF_LEFT;
    public static final Character UTF_FAST_FORWARD = 0x23E9; // "" + UTF_RIGHT + UTF_RIGHT;

    public static final String UTF_MOVE            = "" + UTF_LEFT + UTF_RIGHT + UTF_UP + UTF_DOWN;
    public static final String UTF_ZOOMIN          = "" + UTF_REWIND;
    public static final String UTF_ZOOMOUT         = "" + UTF_FAST_FORWARD;

    // @formatter:on
}
