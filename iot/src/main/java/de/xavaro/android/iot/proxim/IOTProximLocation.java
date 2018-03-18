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
    private static final String LOGTAG = IOTProximLocation.class.getSimpleName();

    public static void startLocationListener(Context appcontext)
    {
        if (IOT.instance == null) return;

        if (IOT.instance.proximLocationListener == null)
        {
            IOT.instance.proximLocationListener = new IOTProximLocation();

            if (Simple.checkLocationPermission(appcontext))
            {
                LocationManager locationManager = Simple.getLocationManager();

                boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                Location last = null;

                if (networkIsEnabled)
                {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            5000L, 10F,
                            IOT.instance.proximLocationListener);

                    Log.d(LOGTAG, "startLocationListener: NETWORK_PROVIDER installed.");

                    Location netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (netLoc != null)
                    {
                        last = netLoc;

                        Log.d(LOGTAG, "startLocationListener: last NET location"
                                + " lat=" + last.getLatitude()
                                + " lon=" + last.getLongitude()
                                + " age=" + Simple.getAgeInSeconds(last.getTime())
                        );
                    }
                }

                if (gpsIsEnabled)
                {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            5000L, 10F,
                            IOT.instance.proximLocationListener);

                    Log.d(LOGTAG, "startLocationListener: GPS_PROVIDER installed.");

                    Location gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (gpsLoc != null)
                    {
                        last = gpsLoc;

                        Log.d(LOGTAG, "startLocationListener: last GPS location"
                                + " lat=" + last.getLatitude()
                                + " lon=" + last.getLongitude()
                                + " age=" + Simple.getAgeInSeconds(last.getTime())
                        );
                    }
                }

                if (last == null)
                {
                    Log.e(LOGTAG, "startLocationListener: no last location.");
                }
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

        Log.d(LOGTAG, "onLocationChanged:"
                + " lat=" + location.getLatitude()
                + " lon=" + location.getLongitude()
                + " age=" + Simple.getAgeInSeconds(location.getTime())
                + " pro=" + location.getProvider()
        );
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
