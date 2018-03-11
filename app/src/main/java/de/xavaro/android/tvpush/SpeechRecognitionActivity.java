package de.xavaro.android.tvpush;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

import de.xavaro.android.gui.base.GUIActivity;

import de.xavaro.android.gui.simple.Simple;
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

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Simple.MP, Simple.WC, Gravity.BOTTOM);

        topframe.addView(spechrecognition, lp);
    }
}
