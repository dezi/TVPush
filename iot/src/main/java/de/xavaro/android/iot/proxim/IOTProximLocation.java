package de.xavaro.android.iot.proxim;

import android.support.annotation.Nullable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class IOTProximLocation implements LocationListener
{
    private static final String LOGTAG = IOTProximLocation.class.getSimpleName();

    private boolean gpsIsEnabled;
    private boolean networkIsEnabled;

    public static void startService(Context appcontext)
    {
        if ((IOT.instance != null) && (IOT.instance.proximLocationListener == null))
        {
            IOT.instance.proximLocationListener = new IOTProximLocation();
            IOT.instance.proximLocationListener.start(appcontext);
        }
    }

    public static void stopService()
    {
        if ((IOT.instance != null) && (IOT.instance.proximLocationListener != null))
        {
            IOT.instance.proximLocationListener.stop();;
            IOT.instance.proximLocationListener = null;
        }
    }

    private void start(Context appcontext)
    {
        if (Simple.checkLocationPermission(appcontext))
        {
            LocationManager locationManager = Simple.getLocationManager();

            gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location last = null;

            if (networkIsEnabled)
            {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5000l, 0f,
                        IOT.instance.proximLocationListener);

                Log.d(LOGTAG, "startService: NETWORK_PROVIDER installed.");

                Location netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (netLoc != null)
                {
                    last = netLoc;

                    Log.d(LOGTAG, "startService: last NET location"
                            + " lat=" + last.getLatitude()
                            + " lon=" + last.getLongitude()
                            + " alt=" + last.getAltitude()
                            + " acc=" + last.getAccuracy()
                            + " age=" + Simple.getAgeInSeconds(last.getTime())
                    );
                }
            }

            if (gpsIsEnabled)
            {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000l, 0f,
                        IOT.instance.proximLocationListener);

                Log.d(LOGTAG, "startService: GPS_PROVIDER installed.");

                Location gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (gpsLoc != null)
                {
                    last = gpsLoc;

                    Log.d(LOGTAG, "startService: last GPS location"
                            + " lat=" + last.getLatitude()
                            + " lon=" + last.getLongitude()
                            + " alt=" + last.getAltitude()
                            + " acc=" + last.getAccuracy()
                            + " age=" + Simple.getAgeInSeconds(last.getTime())
                    );
                }
            }

            if (last == null)
            {
                Log.e(LOGTAG, "startService: no last location.");
            }
        }
        else
        {
            Log.e(LOGTAG, "startService: no permission!");
        }
    }

    private void stop()
    {
        if (networkIsEnabled || gpsIsEnabled)
        {
            LocationManager locationManager = Simple.getLocationManager();
            locationManager.removeUpdates(IOT.instance.proximLocationListener);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location == null) return;

        if (location.getAltitude() == 0.0)
        {
            Double altitude = getAltitude(location.getLatitude(), location.getLongitude());
            if (altitude != null) location.setAltitude(altitude);
        }

        if (IOT.device != null)
        {
            if (IOT.device.hasCapability("fixed"))
            {
                IOTDevice device = new IOTDevice(IOT.device.uuid);
                device.fixedLatCoarse = location.getLatitude();
                device.fixedLonCoarse = location.getLongitude();
                device.fixedAltCoarse = location.getAltitude();

                IOTDevices.addEntry(device, false);
            }

            IOTStatus status = new IOTStatus(IOT.device.uuid);
            status.positionLatCoarse = location.getLatitude();
            status.positionLonCoarse = location.getLongitude();
            status.positionAltCoarse = location.getAltitude();

            IOTStatusses.addEntry(status, false);

            IOT.instance.proximServer.advertiseGPSCoarse();
        }

        Log.d(LOGTAG, "onLocationChanged:"
                + " lat=" + location.getLatitude()
                + " lon=" + location.getLongitude()
                + " alt=" + location.getAltitude()
                + " age=" + Simple.getAgeInSeconds(location.getTime())
                + " acc=" + location.getAccuracy()
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

    @Nullable
    public static Double getAltitude(Double lat, Double lon)
    {
        Double altitude = null;

        String url = "https://maps.googleapis.com/maps/api/elevation/json"
                + "?locations=" + String.valueOf(lat) + "," + String.valueOf(lon)
                + "&sensor=true" + "&key=AIzaSyBJ1BXy83xwFwJNhJdD-imW7AfxBZsRkZs";

        try
        {
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");

            InputStream stream = connection.getInputStream();

            if (stream != null)
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder respStr = new StringBuilder();

                String line;

                while ((line = bufferedReader.readLine()) != null)
                {
                    respStr.append(line);
                    respStr.append("\n");
                }

                stream.close();

                JSONObject respJson = Json.fromStringObject(respStr.toString());
                JSONArray results = Json.getArray(respJson, "results");

                if ((results != null) && (results.length() > 0))
                {
                    JSONObject entry = Json.getObject(results, 0);
                    if (entry != null)
                    {
                        altitude = Json.getDouble(entry, "elevation");
                    }
                }

                if (altitude == null)
                {
                    Log.e(LOGTAG, "getAltitude: url=" + url);
                    Log.e(LOGTAG, "getAltitude: respStr=" + respStr);
                }
                else
                {
                    Log.d(LOGTAG, "getAltitude: lat=" + lat + " lon=" + lon + " alt=" + altitude);
                }
            }
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
        }

        return altitude;
    }
}
