package de.xavaro.android.gui.plugin;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import pub.android.interfaces.ext.OnSpeechHandler;

import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIRainbowLayout;
import de.xavaro.android.gui.views.GUITextView;
import de.xavaro.android.gui.views.GUIIconView;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.R;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;

public class GUIToastBar extends GUIPlugin implements OnSpeechHandler
{
    private final static String LOGTAG = GUIToastBar.class.getSimpleName();

    private final Handler handler = new Handler();

    private GUIRainbowLayout colorFrame;
    private GUILinearLayout centerIcon;
    private GUILinearLayout iconBack;
    private GUILinearLayout centerCont;
    private GUITextView toastText;
    private GUIIconView menuIcon;

    private String toastMessage;
    private boolean hadResult;

    public GUIToastBar(Context context)
    {
        super(context);

        colorFrame = new GUIRainbowLayout(getContext());

        contentFrame.addView(colorFrame);

        centerCont = new GUILinearLayout(getContext());
        centerCont.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);
        centerCont.setOrientation(LinearLayout.VERTICAL);

        colorFrame.addView(centerCont);

        toastText = new GUITextView(getContext());
        toastText.setGravity(Gravity.CENTER_HORIZONTAL);
        toastText.setTextColor(Color.WHITE);
        toastText.setTextSizeDip(GUIDefs.FONTSIZE_SPEECH);

        centerCont.addView(toastText);

        if (Simple.isPhone())
        {
            centerIcon = new GUILinearLayout(getContext());
            centerIcon.setSizeDip(Simple.MP, Simple.WC);
            centerIcon.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);
            centerIcon.setOrientation(LinearLayout.VERTICAL);
            centerIcon.setPaddingDip(GUIDefs.PADDING_XXLARGE);

            contentFrame.addView(centerIcon);

            iconBack = new GUILinearLayout(getContext());
            iconBack.setSizeDip(Simple.WC, Simple.WC);
            iconBack.setPaddingDip(GUIDefs.PADDING_SMALL);
            iconBack.setRoundedCorners(GUIDefs.ROUNDED_NORMAL, GUIDefs.COLOR_GRAY);

            iconBack.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    GUI.instance.desktopActivity.displayMenu(true);
                }
            });

            centerIcon.addView(iconBack);

            menuIcon = new GUIIconView(getContext());
            menuIcon.setImageResource(R.drawable.menu_400);

            iconBack.addView(menuIcon);

            toastText.setMinLines(3);

            contentFrame.setBackgroundColor(Color.BLACK);
            colorFrame.setBackgroundColor(Color.BLACK);
            toastText.setBackgroundColor(Color.BLACK);

            centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_XLARGE, Color.BLACK);

            colorFrame.setSizeDip(Simple.MP, Simple.MP);
            centerCont.setSizeDip(Simple.MP, Simple.MP);
            toastText.setSizeDip(Simple.MP, Simple.WC);

            contentFrame.setPaddingDip(GUIDefs.PADDING_LARGE);
            colorFrame.setPaddingDip(GUIDefs.PADDING_LARGE);
            centerCont.setPaddingDip(GUIDefs.PADDING_LARGE);
            toastText.setPaddingDip(GUIDefs.PADDING_LARGE);
        }
        else
        {
            toastText.setSingleLine(true);
            toastText.setEllipsize(TextUtils.TruncateAt.START);

            contentFrame.setBackgroundColor(Color.TRANSPARENT);
            colorFrame.setBackgroundColor(Color.TRANSPARENT);
            toastText.setBackgroundColor(Color.TRANSPARENT);

            centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_LIGHT_TRANSPARENT);

            colorFrame.setSizeDip(Simple.MP, Simple.WC);
            centerCont.setSizeDip(Simple.MP, Simple.WC);
            toastText.setSizeDip(Simple.WC, Simple.WC);

            contentFrame.setPaddingDip(GUIDefs.PADDING_SMALL);
            colorFrame.setPaddingDip(GUIDefs.PADDING_SMALL);
            centerCont.setPaddingDip(GUIDefs.PADDING_SMALL);
            toastText.setPaddingDip(GUIDefs.PADDING_ZERO);
        }
    }

    public FrameLayout.LayoutParams getPreferredLayout()
    {
        return new FrameLayout.LayoutParams(Simple.MP, Simple.WC, Gravity.BOTTOM);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    @Override
    public void onActivateRemote()
    {
        if (toastMessage == null)
        {
            toastText.setTextColor(Color.GRAY);
            toastText.setText("Bitte drücken Sie die Mikrofon-Taste auf der Fernbedienung");
        }
    }

    @Override
    public void onSpeechReady()
    {
        handler.removeCallbacks(pleaseSpeekNow);
        handler.postDelayed(pleaseSpeekNow, hadResult ? 1000 : 0);

        hadResult = false;
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        JSONArray results = Json.getArray(speech, "results");
        if ((results == null) || (results.length() == 0)) return;

        JSONObject result = Json.getObject(results, 0);

        String text = Json.getString(result, "text");
        float conf = Json.getFloat(result, "conf");

        Log.d(LOGTAG, "onSpeechResults: conf=" + conf + " text=" + text);

        toastMessage = null;

        toastText.setTextColor(Color.WHITE);
        toastText.setText(text);
        colorFrame.start();

        hadResult = true;
    }

    public void displayPinCodeMessage(int timeout)
    {
        Log.d(LOGTAG, "displayPinCodeMessage: timeout=" + timeout);

        displayToastMessage("Bitte lesen Sie den PIN-Code vor", timeout, true);
    }

    public void displayToastMessage(String message, int seconds, boolean emphasis)
    {
        if (seconds <= 0) seconds = 2;
        if (seconds > 60) seconds = 60;

        toastMessage = message;

        toastText.setText(toastMessage);
        toastText.setTextColor(Color.LTGRAY);
        centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_DARK_TRANSPARENT);

        if (emphasis)
        {
            colorFrame.start(2);
        }
        else
        {
            colorFrame.stop();
        }

        GUI.instance.desktopActivity.bringToFront();

        makePost(toastDone, seconds * 1000);
    }

    private final Runnable toastDone = new Runnable()
    {
        @Override
        public void run()
        {
            colorFrame.stop();
            toastMessage = null;

            pleaseSpeekNow.run();
        }
    };

    private final Runnable pleaseSpeekNow = new Runnable()
    {
        @Override
        public void run()
        {
            if (toastMessage == null)
            {
                colorFrame.stop();
                toastText.setTextColor(Color.GRAY);
                toastText.setText("Bitte sprechen Sie jetzt");
                centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_LIGHT_TRANSPARENT);
            }
            else
            {
                makePost(pleaseSpeekNow, 1000);
            }
        }
    };

    private void makePost(Runnable runnable, int delay)
    {
        Simple.getHandler().removeCallbacks(toastDone);
        Simple.getHandler().removeCallbacks(pleaseSpeekNow);

        Simple.getHandler().postDelayed(runnable,delay);
    }
}

