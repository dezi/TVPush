package de.xavaro.android.tvpush;

import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.util.Log;

import de.xavaro.android.base.BaseActivity;
import de.xavaro.android.base.BaseViewRainbow;
import de.xavaro.android.base.BaseRegistration;
import de.xavaro.android.base.BaseRecognizer;
import de.xavaro.android.simple.Defs;
import de.xavaro.android.simple.Simple;

public class SpeechRecognitionActivity extends BaseActivity
{
    private final static String LOGTAG = SpeechRecognitionActivity.class.getSimpleName();

    private final Handler handler = new Handler();

    private BaseRecognizer recognition;
    private BaseViewRainbow colorFrame;
    private TextView speechText;
    private boolean hadResult;
    private int orient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        RelativeLayout outerFrame = new RelativeLayout(this);
        Simple.setSizeDip(outerFrame, Simple.MP, Simple.MP);

        topframe.addView(outerFrame);

        colorFrame = new BaseViewRainbow(this);

        outerFrame.addView(colorFrame);

        RelativeLayout centerCont = new RelativeLayout(this);
        centerCont.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);

        colorFrame.addView(centerCont);

        speechText = new TextView(this);
        speechText.setGravity(Gravity.CENTER_HORIZONTAL);
        speechText.setTextColor(Color.WHITE);
        speechText.setSingleLine(true);
        speechText.setEllipsize(TextUtils.TruncateAt.START);

        Simple.setTextSizeDip(speechText, Defs.FONTSIZE_LARGE);
        Simple.setSizeDip(speechText, Simple.WC, Simple.WC);

        centerCont.addView(speechText);

        if (Simple.isPhone())
        {
            outerFrame.setBackgroundColor(Color.BLACK);
            centerCont.setBackgroundColor(Color.BLACK);
            colorFrame.setBackgroundColor(Color.BLACK);
            speechText.setBackgroundColor(Color.BLACK);

            Simple.setSizeDip(colorFrame, Simple.MP, Simple.MP);
            Simple.setSizeDip(centerCont, Simple.MP, Simple.MP);

            speechText.setMinLines(3);

            Simple.setPaddingDip(outerFrame, Defs.PADDING_LARGE);
            Simple.setPaddingDip(colorFrame, Defs.PADDING_LARGE);
            Simple.setPaddingDip(centerCont, Defs.PADDING_LARGE);
            Simple.setPaddingDip(speechText, Defs.PADDING_LARGE);
        }
        else
        {
            outerFrame.setGravity(Gravity.BOTTOM);

            //Simple.setRoundedCorners(centerCont, Defs.ROUNDED_MEDIUM, Defs.COLOR_LIGHT_TRANSPARENT);

            //Simple.setRoundedCorners(outerFrame, Defs.ROUNDED_MEDIUM, Color.GREEN);
            //Simple.setRoundedCorners(colorFrame, Defs.ROUNDED_MEDIUM, Color.RED);
            //Simple.setRoundedCorners(centerCont, Defs.ROUNDED_MEDIUM, Color.BLUE);
            //Simple.setRoundedCorners(speechText, Defs.ROUNDED_MEDIUM, Color.BLACK);

            outerFrame.setBackgroundColor(Color.TRANSPARENT);
            colorFrame.setBackgroundColor(Color.TRANSPARENT);
            speechText.setBackgroundColor(Color.TRANSPARENT);

            Simple.setRoundedCorners(centerCont, Defs.ROUNDED_MEDIUM, Color.GRAY);

            Simple.setSizeDip(colorFrame, Simple.MP, Simple.WC);
            Simple.setSizeDip(centerCont, Simple.MP, Simple.WC);

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

        recognition = new BaseRecognizer(this)
        {
            @Override
            public void onPleaseActivate()
            {
                super.onPleaseActivate();

                SpeechRecognitionActivity.this.onPleaseActivate();
            }

            @Override
            public void onReadyForSpeech(Bundle params)
            {
                super.onReadyForSpeech(params);

                SpeechRecognitionActivity.this.onReadyForSpeech(params);
            }

            @Override
            public void onResults(Bundle results)
            {
                super.onResults(results);

                SpeechRecognitionActivity.this.onResults(results);
            }

            @Override
            public void onPartialResults(Bundle partialResults)
            {
                super.onPartialResults(partialResults);

                SpeechRecognitionActivity.this.onPartialResults(partialResults);
            }

            @Override
            public void onEndOfSpeech()
            {
                super.onEndOfSpeech();

                SpeechRecognitionActivity.this.onEndOfSpeech();
            }
        };
    }

    @Override
    public void onResume()
    {
        Log.d(LOGTAG, "onResume:");

        super.onResume();

        recognition.startListening();
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

    private void onPleaseActivate()
    {
        speechText.setTextColor(Color.GRAY);
        speechText.setText("Bitte drücken Sie die Mikrofon-Taste auf der Fernbedienung");
    }

    private void onReadyForSpeech(Bundle params)
    {
        handler.removeCallbacks(pleaseSpeekNow);
        handler.postDelayed(pleaseSpeekNow, hadResult ? 3000 : 0);
    }

    private void onPartialResults(Bundle partialResults)
    {
        String bestresult = recognition.getBestResult(partialResults);

        if ((bestresult != null) && ! bestresult.isEmpty())
        {
            speechText.setTextColor(Color.WHITE);
            speechText.setText(bestresult);

            hadResult = true;

            colorFrame.start();
        }
    }

    private void onResults(Bundle results)
    {
        String bestresult = recognition.getBestResult(results);

        if ((bestresult != null) && ! bestresult.isEmpty())
        {
            speechText.setTextColor(Color.WHITE);
            speechText.setText(bestresult);

            hadResult = true;
        }
    }

    private void onEndOfSpeech()
    {
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
