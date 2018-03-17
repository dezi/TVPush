package de.xavaro.android.iot.proxim;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class IOTProximLocation implements LocationListener
{
    private static final String LOGTAG = IOTProximLocation.class.getName();

    public static void startLocationListener(Context appcontext)
    {
        if (IOT.instance == null) return;

        if (IOT.instance.proximLocationListener == null)
        {
            IOT.instance.proximLocationListener = new IOTProximLocation();

            if (Simple.checkLocationPermission(appcontext))
            {
                LocationManager locationManager = Simple.getLocationManager();

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);

                String provider = locationManager.getBestProvider(criteria, true);

                locationManager.requestLocationUpdates(
                        provider, 0, 0,
                        IOT.instance.proximLocationListener);

                Log.d(LOGTAG, "startLocationListener: listener installed.");

                Location last = locationManager.getLastKnownLocation(provider);
                IOT.instance.proximLocationListener.onLocationChanged(last);
            }
            else
            {
                Log.e(LOGTAG, "startLocationListener: no permission!");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location == null) return;

        if (IOT.device != null)
        {
            if (IOT.device.hasCapability("fixed"))
            {
                IOTDevice device = new IOTDevice(IOT.device.uuid);
                device.fixedLatCoarse = location.getLatitude();
                device.fixedLonCoarse = location.getLongitude();

                IOTDevices.addEntry(device, false);
            }

            IOTStatus status = new IOTStatus(IOT.device.uuid);
            status.positionLatCoarse = location.getLatitude();
            status.positionLonCoarse = location.getLongitude();

            IOTStatusses.addEntry(status, false);
        }

        Log.d(LOGTAG, "onLocationChanged: lat=" + location.getLatitude() + " lon=" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d(LOGTAG, "onProviderDisabled: provider=" + provider);
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d(LOGTAG, "onProviderEnabled: provider=" + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.d(LOGTAG, "onStatusChanged: provider=" + provider + " status=" + status + " extras=" + extras);
    }
}
