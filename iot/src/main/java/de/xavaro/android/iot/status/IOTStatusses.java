package de.xavaro.android.iot.status;

import android.util.Log;

import de.xavaro.android.iot.base.IOTListGeneric;

public class IOTStatusses extends IOTListGeneric<IOTStatus>
{
    private final static String LOGTAG = IOTStatusses.class.getSimpleName();

    //public static IOTListGeneric<IOTStatus> instance = new IOTListGeneric<IOTStatus>((new IOTStatus()).getClassKey());

    public IOTStatusses()
    {
        super((new IOTStatus()).getClassKey());
    }
}
