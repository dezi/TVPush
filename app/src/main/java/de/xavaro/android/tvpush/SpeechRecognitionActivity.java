package de.xavaro.android.tvpush;

import android.os.Bundle;

import de.xavaro.android.gui.base.GUIActivity;

import de.xavaro.android.gui.plugin.GUISpeechRecogniton;
import de.xavaro.android.gui.plugin.GUIVideoSurface;

public class SpeechRecognitionActivity extends GUIActivity
{
    private final static String LOGTAG = SpeechRecognitionActivity.class.getSimpleName();

    private GUISpeechRecogniton speechRecognition;
    private GUIVideoSurface videoSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        speechRecognition = new GUISpeechRecogniton(this);

        topframe.addView(speechRecognition);

        videoSurface = new GUIVideoSurface(this);

        topframe.addView(videoSurface);

        setWindowHeightDip(600);
    }
}
