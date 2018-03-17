package de.xavaro.android.iot.proxim;

import android.support.annotation.RequiresApi;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.os.Build;
import android.util.Log;

import de.xavaro.android.iot.simple.Simple;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class IOTProximCallback extends AdvertiseCallback
{
    private static final String LOGTAG = IOTProximCallback.class.getName();

    @Override
    public void onStartSuccess(AdvertiseSettings settingsInEffect)
    {
        Log.d(LOGTAG, "AdvertiseCallback: onStartSuccess.");
    }

    @Override
    public void onStartFailure(int errorCode)
    {
        Log.e(LOGTAG, "AdvertiseCallback: onStartFailure"
                + " err=" + errorCode
                + " desc=" + Simple.getBTAdvertiserFailDescription(errorCode));
    }
}
