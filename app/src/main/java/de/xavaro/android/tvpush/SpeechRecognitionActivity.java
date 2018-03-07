package de.xavaro.android.tvpush;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.xavaro.android.base.BaseActivity;
import de.xavaro.android.base.BaseRegistration;
import de.xavaro.android.simple.Simple;

public class SpeechRecognitionActivity extends BaseActivity
{
    private final static String LOGTAG = SpeechRecognitionActivity.class.getSimpleName();

    private SpeechRecognitionTask recognition;
    private TextView speechText;
    private boolean hadResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        RelativeLayout bottom = new RelativeLayout(this);
        bottom.setGravity(Gravity.BOTTOM);
        Simple.setSizeDip(bottom, Simple.MP, Simple.MP);

        topframe.addView(bottom);

        RelativeLayout center = new RelativeLayout(this);
        center.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);
        Simple.setSizeDip(center, Simple.MP, Simple.WC);
        Simple.setMarginDip(center, Simple.PADDING_XLARGE, Simple.PADDING_NORMAL, Simple.PADDING_XLARGE, Simple.PADDING_NORMAL);
        Simple.setRoundedCorners(center, Simple.ROUNDED_MEDIUM, 0x11ffffff);

        bottom.addView(center);

        speechText = new TextView(this);
        speechText.setTextColor(Color.WHITE);
        Simple.setTextSizeDip(speechText, 30);
        Simple.setPaddingDip(speechText, Simple.PADDING_SMALL);

        center.addView(speechText);
    }

    @Override
    public void onStart()
    {
        Log.d(LOGTAG, "onStart:");

        super.onStart();

        recognition = new SpeechRecognitionTask(this)
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
        speechText.setText("Bitte drücken Sie die Mikrofon-Taste auf der Fernbedienung.");
    }

    private void onReadyForSpeech(Bundle params)
    {
        if (! hadResult)
        {
            speechText.setTextColor(Color.GRAY);
            speechText.setText("Bitte sprechen Sie jetzt.");
        }
    }

    private void onPartialResults(Bundle partialResults)
    {
        speechText.setTextColor(Color.WHITE);
        speechText.setText(recognition.getBestResult(partialResults));
    }

    private void onResults(Bundle results)
    {
        String bestresult = recognition.getBestResult(results);

        if ((bestresult != null) && ! bestresult.isEmpty())
        {
            speechText.setTextColor(Color.WHITE);
            speechText.setText(bestresult);
        }
    }
}
