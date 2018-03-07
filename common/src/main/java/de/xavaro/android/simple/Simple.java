package de.xavaro.android.simple;

import android.app.UiModeManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

public class Simple
{
    //region Basic defines.

    // @formatter:off

    public static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    public static final int PADDING_ZERO     = Simple.isTablet() ?  0 :  0;
    public static final int PADDING_TINY     = Simple.isTablet() ?  4 :  2;
    public static final int PADDING_SMALL    = Simple.isTablet() ?  8 :  4;
    public static final int PADDING_MEDIUM   = Simple.isTablet() ? 16 : 12;
    public static final int PADDING_NORMAL   = Simple.isTablet() ? 24 : 18;
    public static final int PADDING_LARGE    = Simple.isTablet() ? 32 : 26;
    public static final int PADDING_XLARGE   = Simple.isTablet() ? 40 : 30;

    public static final int ROUNDED_SMALL    = Simple.isTablet() ?  8 :  4;
    public static final int ROUNDED_MEDIUM   = Simple.isTablet() ? 16 : 12;

    // @formatter:on

    //endregion Basic defines.

    //region Device features.

    private static boolean istv;
    private static boolean istouch;
    private static boolean istablet;
    private static boolean iswidescreen;
    private static boolean isspeech;

    public static void checkFeatures(Context context)
    {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        istv = (uiModeManager != null) && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);

        PackageManager pmManager = context.getPackageManager();
        istouch = pmManager.hasSystemFeature("android.hardware.touchscreen");

        istablet = ((Resources.getSystem().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        int width = getDeviceWidth(context);
        int height = getDeviceHeight(context);
        iswidescreen = (width / (float) height) > (4 / 3f);

        isspeech = android.speech.SpeechRecognizer.isRecognitionAvailable(context);
    }

    public static boolean isTV()
    {
        return istv;
    }

    public static boolean isTouch()
    {
        return istouch;
    }

    public static boolean isTablet()
    {
        return istablet;
    }

    public static boolean isWideScreen()
    {
        return iswidescreen;
    }

    public static boolean isSpeech()
    {
        return isspeech;
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null) && netInfo.isConnectedOrConnecting();
    }

    public static int getDeviceWidth(Context context)
    {
        WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        if (wm == null) return 0;

        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        return size.x;
    }

    public static int getDeviceHeight(Context context)
    {
        WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        if (wm == null) return 0;

        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        return size.y;
    }

    public static int dipToPx(int dp)
    {
        return Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDip(int px)
    {
        return Math.round(px / Resources.getSystem().getDisplayMetrics().density);
    }

    //endregion Device features.

    //region Simple getters.

    public static String getDeviceUserName(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
    }

    public static String getDeviceModelName()
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer))
        {
            return model.toUpperCase();
        }

        return manufacturer.toUpperCase() + " " + model.toUpperCase();
    }

    public static String getFCMToken()
    {
        return FirebaseInstanceId.getInstance().getToken();
    }

    //endregion Simple getters.

    //region View manipulation.

    public static void setSizeNODip(View view, int width, int height)
    {
        if (view.getLayoutParams() == null)
        {
            view.setLayoutParams(new ViewGroup.MarginLayoutParams(WC, WC));
        }

        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
    }

    public static void setSizeDip(View view, int width, int height)
    {
        if (view.getLayoutParams() == null)
        {
            view.setLayoutParams(new ViewGroup.MarginLayoutParams(WC, WC));
        }

        view.getLayoutParams().width = width > 0 ? dipToPx(width) : width;
        view.getLayoutParams().height = height > 0 ? dipToPx(height) : height;
    }

    public static void setTextSizeDip(TextView textView, int size)
    {
        float real = size / textView.getContext().getResources().getConfiguration().fontScale;

        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, real);
    }

    public static void setPaddingDip(View view, int pad)
    {
        view.setPadding(dipToPx(pad), dipToPx(pad), dipToPx(pad), dipToPx(pad));
    }

    public static void setPaddingDip(View view, int left, int top, int right, int bottom)
    {
        view.setPadding(dipToPx(left), dipToPx(top), dipToPx(right), dipToPx(bottom));
    }

    public static void setMarginDip(View view, int margin)
    {
        if (view.getLayoutParams() == null)
            view.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));

        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = dipToPx(margin);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = dipToPx(margin);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = dipToPx(margin);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin = dipToPx(margin);
    }

    public static void setMarginDip(View view, int left, int top, int right, int bottom)
    {
        if (view.getLayoutParams() == null)
            view.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));

        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = dipToPx(left);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = dipToPx(top);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = dipToPx(right);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin = dipToPx(bottom);
    }

    public static void setRoundedCorners(View view, int radiusdip, int color)
    {
        setRoundedCorners(view, radiusdip, color, color);
    }

    public static void setRoundedCorners(View view, int radiusdip, int innerColor, int strokeColor)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(dipToPx(radiusdip));

        shape.setColor(innerColor);

        if (innerColor != strokeColor)
        {
            shape.setStroke(dipToPx(2), strokeColor);
        }

        view.setBackground(shape);
    }

    //endregion View manipulation.

    //region Smart helpers.

    public static void turnBeepOff(Context context)
    {
        if (! Simple.isTV())
        {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            }
            else
            {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        }
    }

    public static void turnBeepOn(Context context)
    {
        if (! Simple.isTV())
        {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
            }
            else
            {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
        }
    }
    //endregion Smart helpers.
}
