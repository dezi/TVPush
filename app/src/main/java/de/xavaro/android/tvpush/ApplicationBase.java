package de.xavaro.android.tvpush;

import de.xavaro.android.gui.base.GUIApplication;
import de.xavaro.android.gui.smart.GUIRegistration;

import de.xavaro.android.gui.simple.Simple;

public class ApplicationBase extends GUIApplication
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        //Simple.removeALLPrefs(this);

        Simple.initialize(this);

        ThirdParty.initialize(this);

        GUIRegistration.speechRecognitionActivityClass = SpeechRecognitionActivity.class;
    }
}
