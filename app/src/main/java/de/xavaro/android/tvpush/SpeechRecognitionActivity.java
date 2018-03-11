package de.xavaro.android.tvpush;

import android.os.Bundle;

import de.xavaro.android.gui.base.GUIActivity;

import de.xavaro.android.gui.widget.GUISpechRecogniton;

public class SpeechRecognitionActivity extends GUIActivity
{
    private final static String LOGTAG = SpeechRecognitionActivity.class.getSimpleName();

    private GUISpechRecogniton spechrecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        spechrecognition = new GUISpechRecogniton(this);

        topframe.addView(spechrecognition);
    }
}
