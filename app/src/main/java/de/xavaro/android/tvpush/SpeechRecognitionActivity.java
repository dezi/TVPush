package de.xavaro.android.tvpush;

import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUIActivity;
import de.xavaro.android.gui.views.GUIRelativeLayout;
import de.xavaro.android.gui.views.GUIRainbowLayout;
import de.xavaro.android.gui.smart.GUIRegistration;
import de.xavaro.android.gui.smart.GUISpeech;
import de.xavaro.android.gui.smart.GUISpeechCallback;

import de.xavaro.android.gui.views.GUITextView;
import de.xavaro.android.iot.handler.IOTHandleStot;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;

public class SpeechRecognitionActivity extends GUIActivity implements GUISpeechCallback
{
    private final static String LOGTAG = SpeechRecognitionActivity.class.getSimpleName();

    private final Handler handler = new Handler();

    private GUIRainbowLayout colorFrame;
    private GUITextView speechText;
    private GUISpeech recognition;

    private boolean hadResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        RelativeLayout outerFrame = new RelativeLayout(this);
        Simple.setSizeDip(outerFrame, Simple.MP, Simple.MP);

        topframe.addView(outerFrame);

        colorFrame = new GUIRainbowLayout(this);

        outerFrame.addView(colorFrame);

        GUIRelativeLayout centerCont = new GUIRelativeLayout(this);
        centerCont.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);

        colorFrame.addView(centerCont);

        speechText = new GUITextView(this);
        speechText.setGravity(Gravity.CENTER_HORIZONTAL);
        speechText.setTextColor(Color.WHITE);
        speechText.setTextSizeDip(GUIDefs.FONTSIZE_SPEECH);

        centerCont.addView(speechText);

        if (Simple.isPhone())
        {
            speechText.setMinLines(3);

            outerFrame.setBackgroundColor(Color.BLACK);
            colorFrame.setBackgroundColor(Color.BLACK);
            speechText.setBackgroundColor(Color.BLACK);

            centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_XLARGE, Color.BLACK);

            Simple.setSizeDip(colorFrame, Simple.MP, Simple.MP);
            Simple.setSizeDip(centerCont, Simple.MP, Simple.MP);
            Simple.setSizeDip(speechText, Simple.MP, Simple.WC);

            Simple.setPaddingDip(outerFrame, GUIDefs.PADDING_LARGE);
            Simple.setPaddingDip(colorFrame, GUIDefs.PADDING_LARGE);
            Simple.setPaddingDip(centerCont, GUIDefs.PADDING_LARGE);
            Simple.setPaddingDip(speechText, GUIDefs.PADDING_LARGE);
        }
        else
        {
            if (Simple.isTV())
            {
                outerFrame.setGravity(Gravity.BOTTOM);
            }
            else
            {
                outerFrame.setGravity(Gravity.TOP);
            }

            speechText.setSingleLine(true);
            speechText.setEllipsize(TextUtils.TruncateAt.START);

            outerFrame.setBackgroundColor(Color.TRANSPARENT);
            colorFrame.setBackgroundColor(Color.TRANSPARENT);
            speechText.setBackgroundColor(Color.TRANSPARENT);

            centerCont.setRoundedCornersDip(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_LIGHT_TRANSPARENT);

            Simple.setSizeDip(colorFrame, Simple.MP, Simple.WC);
            Simple.setSizeDip(centerCont, Simple.MP, Simple.WC);
            Simple.setSizeDip(speechText, Simple.WC, Simple.WC);

            Simple.setPaddingDip(outerFrame, GUIDefs.PADDING_SMALL);
            Simple.setPaddingDip(colorFrame, GUIDefs.PADDING_SMALL);
            Simple.setPaddingDip(centerCont, GUIDefs.PADDING_SMALL);
            Simple.setPaddingDip(speechText, GUIDefs.PADDING_ZERO);
        }
    }

    @Override
    public void onStart()
    {
        Log.d(LOGTAG, "onStart:");

        super.onStart();

        recognition = new GUISpeech(this, this);
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
