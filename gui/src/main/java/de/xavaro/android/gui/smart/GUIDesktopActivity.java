package de.xavaro.android.gui.smart;

import android.os.Bundle;
import android.util.Log;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIActivity;
import de.xavaro.android.gui.plugin.GUISpeechRecogniton;

public class GUIDesktopActivity extends GUIActivity
{
    private final static String LOGTAG = GUIDesktopActivity.class.getSimpleName();

    public GUISpeechRecogniton speechRecognition;
    private GUISpeech recognition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GUI.instance.desktop = this;

        speechRecognition = new GUISpeechRecogniton(this);
    }

    @Override
    public void onStart()
    {
        Log.d(LOGTAG, "onStart:");

        super.onStart();

        recognition = new GUISpeech(this, speechRecognition);
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
        }

        super.onBackPressed();
    }

    public void displaySpeechRecognition(boolean show)
    {
        Log.d(LOGTAG, "displaySpeechRecognition: show=" + show);

        if (show)
        {
            if (speechRecognition.getParent() == null)
            {
                topframe.addView(speechRecognition);
            }
        }
        else
        {
            if (speechRecognition.getParent() != null)
            {
                topframe.removeView(speechRecognition);
            }
        }
    }
}