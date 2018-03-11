package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.smart.GUIRegistration;
import de.xavaro.android.gui.smart.GUISpeech;
import de.xavaro.android.gui.smart.GUISpeechCallback;
import de.xavaro.android.gui.views.GUIRainbowLayout;
import de.xavaro.android.gui.views.GUIRelativeLayout;
import de.xavaro.android.gui.views.GUITextView;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.iot.handler.IOTHandleStot;

public class GUISpeechRecogniton extends GUIPlugin implements GUISpeechCallback
{
    private final static String LOGTAG = GUISpeechRecogniton.class.getSimpleName();

    private final Handler handler = new Handler();

    private GUIRainbowLayout colorFrame;
    private GUITextView speechText;
    private GUISpeech recognition;

    private boolean hadResult;

    public GUISpeechRecogniton(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate();

        pluginFrameParams.gravity = Gravity.BOTTOM;
        pluginFrame.setLayoutParams(pluginFrameParams);

        colorFrame = new GUIRainbowLayout(getContext());

        pluginFrame.addView(colorFrame);

        GUIRelativeLayout centerCont = new GUIRelativeLayout(getContext());
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

            pluginFrame.setBackgroundColor(Color.BLACK);
            colorFrame.setBackgroundColor(Color.BLACK);
            speechText.setBackgroundColor(Color.BLACK);

            centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_XLARGE, Color.BLACK);

            colorFrame.setSizeDip(Simple.MP, Simple.MP);
            centerCont.setSizeDip(Simple.MP, Simple.MP);
            speechText.setSizeDip(Simple.MP, Simple.WC);

            pluginFrame.setPaddingDip(GUIDefs.PADDING_LARGE);
            colorFrame.setPaddingDip(GUIDefs.PADDING_LARGE);
            centerCont.setPaddingDip(GUIDefs.PADDING_LARGE);
            speechText.setPaddingDip(GUIDefs.PADDING_LARGE);
        }
        else
        {
            speechText.setSingleLine(true);
            speechText.setEllipsize(TextUtils.TruncateAt.START);

            pluginFrame.setBackgroundColor(Color.TRANSPARENT);
            colorFrame.setBackgroundColor(Color.TRANSPARENT);
            speechText.setBackgroundColor(Color.TRANSPARENT);

            centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_LIGHT_TRANSPARENT);

            colorFrame.setSizeDip(Simple.MP, Simple.WC);
            centerCont.setSizeDip(Simple.MP, Simple.WC);
            speechText.setSizeDip(Simple.WC, Simple.WC);

            pluginFrame.setPaddingDip(GUIDefs.PADDING_SMALL);
            colorFrame.setPaddingDip(GUIDefs.PADDING_SMALL);
            centerCont.setPaddingDip(GUIDefs.PADDING_SMALL);
            speechText.setPaddingDip(GUIDefs.PADDING_ZERO);
        }
    }

    @Override
    public void onStart()
    {
        Log.d(LOGTAG, "onStart:");

        super.onStart();

        recognition = new GUISpeech(getContext(), this);
    }

    @Override
    public void onResume()
    {
        Log.d(LOGTAG, "onResume:");

        super.onResume();

        if (recognition != null)
        {
            recognition.startListening();
        }
    }

    @Override
    public void onPause()
    {
        Log.d(LOGTAG, "onPause:");

        super.onPause();

        if (recognition != null)
        {
            recognition.stopListening();
        }
    }

    @Override
    public void onStop()
    {
        Log.d(LOGTAG, "onStop:");

        super.onStop();

        if (recognition != null)
        {
            recognition.destroy();
            recognition = null;
        }
    }

    @Override
    public void onBackPressed()
    {
        Log.d(LOGTAG, "onBackPressed:");

        if (recognition != null)
        {
            recognition.destroy();
            recognition = null;

            GUIRegistration.speechRecognitionInhibitUntil = System.currentTimeMillis() + (8 * 1000);
        }

        super.onBackPressed();
    }

    @Override
    public void onActivateRemote()
    {
        speechText.setTextColor(Color.GRAY);
        speechText.setText("Bitte drücken Sie die Mikrofon-Taste auf der Fernbedienung");
    }

    @Override
    public void onSpeechReady()
    {
        handler.removeCallbacks(pleaseSpeekNow);
        handler.postDelayed(pleaseSpeekNow, hadResult ? 3000 : 0);

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

        speechText.setTextColor(Color.WHITE);
        speechText.setText(text);

        colorFrame.start();

        hadResult = true;

        IOTHandleStot.sendSTOT(speech);
    }

    private final Runnable pleaseSpeekNow = new Runnable()
    {
        @Override
        public void run()
        {
            speechText.setTextColor(Color.GRAY);
            speechText.setText("Bitte sprechen Sie jetzt");

            colorFrame.stop();
        }
    };

}

