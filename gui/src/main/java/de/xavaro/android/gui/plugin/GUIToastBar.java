package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.smart.GUISpeechCallback;
import de.xavaro.android.gui.views.GUIRainbowLayout;
import de.xavaro.android.gui.views.GUIRelativeLayout;
import de.xavaro.android.gui.views.GUITextView;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;

public class GUIToastBar extends GUIPlugin implements GUISpeechCallback
{
    private final static String LOGTAG = GUIToastBar.class.getSimpleName();

    private final Handler handler = new Handler();

    private GUIRelativeLayout centerCont;
    private GUIRainbowLayout colorFrame;
    private GUITextView speechText;

    private String toastMessage;
    private boolean hadResult;

    public GUIToastBar(Context context)
    {
        super(context);

        colorFrame = new GUIRainbowLayout(getContext());

        contentFrame.addView(colorFrame);

        centerCont = new GUIRelativeLayout(getContext());
        centerCont.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);

        colorFrame.addView(centerCont);

        speechText = new GUITextView(getContext());
        speechText.setGravity(Gravity.CENTER_HORIZONTAL);
        speechText.setTextColor(Color.WHITE);
        speechText.setTextSizeDip(GUIDefs.FONTSIZE_SPEECH);

        centerCont.addView(speechText);

        if (Simple.isPhone())
        {
            speechText.setMinLines(3);

            contentFrame.setBackgroundColor(Color.BLACK);
            colorFrame.setBackgroundColor(Color.BLACK);
            speechText.setBackgroundColor(Color.BLACK);

            centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_XLARGE, Color.BLACK);

            colorFrame.setSizeDip(Simple.MP, Simple.MP);
            centerCont.setSizeDip(Simple.MP, Simple.MP);
            speechText.setSizeDip(Simple.MP, Simple.WC);

            contentFrame.setPaddingDip(GUIDefs.PADDING_LARGE);
            colorFrame.setPaddingDip(GUIDefs.PADDING_LARGE);
            centerCont.setPaddingDip(GUIDefs.PADDING_LARGE);
            speechText.setPaddingDip(GUIDefs.PADDING_LARGE);
        }
        else
        {
            speechText.setSingleLine(true);
            speechText.setEllipsize(TextUtils.TruncateAt.START);

            contentFrame.setBackgroundColor(Color.TRANSPARENT);
            colorFrame.setBackgroundColor(Color.TRANSPARENT);
            speechText.setBackgroundColor(Color.TRANSPARENT);

            centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_LIGHT_TRANSPARENT);

            colorFrame.setSizeDip(Simple.MP, Simple.WC);
            centerCont.setSizeDip(Simple.MP, Simple.WC);
            speechText.setSizeDip(Simple.WC, Simple.WC);

            contentFrame.setPaddingDip(GUIDefs.PADDING_SMALL);
            colorFrame.setPaddingDip(GUIDefs.PADDING_SMALL);
            centerCont.setPaddingDip(GUIDefs.PADDING_SMALL);
            speechText.setPaddingDip(GUIDefs.PADDING_ZERO);
        }

        GUI.instance.speechListener.setCallback(this);
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
            speechText.setTextColor(Color.GRAY);
            speechText.setText("Bitte drücken Sie die Mikrofon-Taste auf der Fernbedienung");
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

        speechText.setTextColor(Color.WHITE);
        speechText.setText(text);
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

        speechText.setText(toastMessage);
        speechText.setTextColor(Color.LTGRAY);
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
                speechText.setTextColor(Color.GRAY);
                speechText.setText("Bitte sprechen Sie jetzt");
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

