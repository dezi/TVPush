package de.xavaro.android.tvpush;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.xavaro.android.base.BaseActivity;
import de.xavaro.android.simple.Simple;

public class SpeechRecognitionActivity extends BaseActivity
{
    private final static String LOGTAG = SpeechRecognitionActivity.class.getSimpleName();

    private SpeechRecognitionTask recognition;
    private TextView speechText;

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
        Simple.setTextSizeDip(speechText, 36);
        Simple.setPaddingDip(speechText, Simple.PADDING_SMALL);

        center.addView(speechText);
    }

    @Override
    public void onResume()
    {
        Log.d(LOGTAG, "onResume:");

        super.onResume();

        recognition = new SpeechRecognitionTask(this)
        {
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

        recognition.startListening();
    }

    @Override
    public void onBackPressed()
    {
        Log.d(LOGTAG, "onBackPressed:");

        if (recognition != null)
        {
            recognition.destroy();
            recognition = null;
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
            recognition.destroy();
            recognition = null;
        }
    }

    private void onResults(Bundle results)
    {
        speechText.setText(recognition.getBestResult(results));
    }

    private void onPartialResults(Bundle partialResults)
    {
        speechText.setText(recognition.getBestResult(partialResults));
    }
}
