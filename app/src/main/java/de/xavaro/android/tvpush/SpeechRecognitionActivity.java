package de.xavaro.android.tvpush;

import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.base.BaseActivity;
import de.xavaro.android.base.BaseRelativeLayout;
import de.xavaro.android.base.BaseRainbowLayout;
import de.xavaro.android.base.BaseRegistration;
import de.xavaro.android.base.BaseSpeech;
import de.xavaro.android.base.BaseSpeechCallback;

import de.xavaro.android.iot.comm.IOTMessage;
import de.xavaro.android.simple.Defs;
import de.xavaro.android.simple.Json;
import de.xavaro.android.simple.Simple;

public class SpeechRecognitionActivity extends BaseActivity implements BaseSpeechCallback
{
    private final static String LOGTAG = SpeechRecognitionActivity.class.getSimpleName();

    private final Handler handler = new Handler();

    private BaseRainbowLayout colorFrame;
    private BaseSpeech recognition;
    private TextView speechText;
    private boolean hadResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        RelativeLayout outerFrame = new RelativeLayout(this);
        Simple.setSizeDip(outerFrame, Simple.MP, Simple.MP);

        topframe.addView(outerFrame);

        colorFrame = new BaseRainbowLayout(this);

        outerFrame.addView(colorFrame);

        BaseRelativeLayout centerCont = new BaseRelativeLayout(this);
        centerCont.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);

        colorFrame.addView(centerCont);

        speechText = new TextView(this);
        speechText.setGravity(Gravity.CENTER_HORIZONTAL);
        speechText.setTextColor(Color.WHITE);

        Simple.setTextSizeDip(speechText, Defs.FONTSIZE_SPEECH);

        centerCont.addView(speechText);

        if (Simple.isPhone())
        {
            speechText.setMinLines(3);

            outerFrame.setBackgroundColor(Color.BLACK);
            colorFrame.setBackgroundColor(Color.BLACK);
            speechText.setBackgroundColor(Color.BLACK);

            centerCont.setRoundedCornersDip(Defs.ROUNDED_XLARGE, Color.BLACK);

            Simple.setSizeDip(colorFrame, Simple.MP, Simple.MP);
            Simple.setSizeDip(centerCont, Simple.MP, Simple.MP);
            Simple.setSizeDip(speechText, Simple.MP, Simple.WC);

            Simple.setPaddingDip(outerFrame, Defs.PADDING_LARGE);
            Simple.setPaddingDip(colorFrame, Defs.PADDING_LARGE);
            Simple.setPaddingDip(centerCont, Defs.PADDING_LARGE);
            Simple.setPaddingDip(speechText, Defs.PADDING_LARGE);
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

            centerCont.setRoundedCornersDip(Defs.ROUNDED_MEDIUM, Defs.COLOR_LIGHT_TRANSPARENT);

            Simple.setSizeDip(colorFrame, Simple.MP, Simple.WC);
            Simple.setSizeDip(centerCont, Simple.MP, Simple.WC);
            Simple.setSizeDip(speechText, Simple.WC, Simple.WC);

            Simple.setPaddingDip(outerFrame, Defs.PADDING_SMALL);
            Simple.setPaddingDip(colorFrame, Defs.PADDING_SMALL);
            Simple.setPaddingDip(centerCont, Defs.PADDING_SMALL);
            Simple.setPaddingDip(speechText, Defs.PADDING_ZERO);
        }
    }

    @Override
    public void onStart()
    {
        Log.d(LOGTAG, "onStart:");

        super.onStart();

        recognition = new BaseSpeech(this, this);
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

            BaseRegistration.speechRecognitionInhibitUntil = System.currentTimeMillis() + (8 * 1000);
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

        IOTMessage.sendSTOT(speech);
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
