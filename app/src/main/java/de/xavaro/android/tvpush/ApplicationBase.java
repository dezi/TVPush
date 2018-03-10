package de.xavaro.android.tvpush;

import de.xavaro.android.base.BaseApplication;
import de.xavaro.android.base.BaseRegistration;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.simple.Simple;
import zz.top.tpl.comm.TPLUDP;
import zz.top.tpl.comm.TPLUDPDiscover;

public class ApplicationBase extends BaseApplication
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        //Simple.removeALLPrefs(this);

        Simple.initialize(this);

        //IOT.initialize(this);

        ThirdParty.initialize();

        BaseRegistration.speechRecognitionActivityClass = SpeechRecognitionActivity.class;

        TPLUDP.startService();
        TPLUDPDiscover.discover();
    }
}
